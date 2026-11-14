package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.item.ModItems;
import com.l7cor1s.myleveling.network.ProgressSync;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerBossEvent;

/**
 * Final S+ gate boss with 3 phases.
 */
public class GateMonarchEntity extends Monster {
    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.myleveling.gate_monarch"),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.NOTCHED_10
    );
    private int phase = 1;

    public GateMonarchEntity(EntityType<? extends GateMonarchEntity> type, Level level) {
        super(type, level);
        this.xpReward = 500;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 18.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ARMOR, 16.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.level().isClientSide()) {
            return;
        }

        float hp = this.getHealth() / this.getMaxHealth();
        if (phase == 1 && hp <= 0.66f) {
            phase = 2;
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 10, 1));
            pulse(ParticleTypes.SOUL_FIRE_FLAME);
            announcePhase();
        } else if (phase == 2 && hp <= 0.33f) {
            phase = 3;
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 120, 2));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 120, 1));
            pulse(ParticleTypes.DRAGON_BREATH);
            announcePhase();
        }

        if (this.tickCount % (phase == 3 ? 30 : 50) == 0 && this.getTarget() instanceof LivingEntity target) {
            // Area slam
            for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0 + phase),
                    ent -> ent instanceof Player)) {
                e.hurt(this.damageSources().mobAttack(this), 4.0f + phase * 2.0f);
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
            }
            if (phase >= 2) {
                target.hurt(this.damageSources().magic(), 3.0f * phase);
            }
        }
    }

    private void pulse(net.minecraft.core.particles.SimpleParticleType type) {
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(type, this.getX(), this.getY() + 1.2, this.getZ(), 50, 1.2, 1.0, 1.2, 0.05);
        }
    }

    private void announcePhase() {
        for (ServerPlayer player : this.level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(48.0))) {
            player.displayClientMessage(Component.translatable("boss.myleveling.gate_monarch.phase"), false);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        this.spawnAtLocation(new ItemStack(ModItems.RANK_S_BLADE.get()));
        this.spawnAtLocation(new ItemStack(ModItems.RANK_S_CHEST.get()));
        this.spawnAtLocation(new ItemStack(ModItems.RANK_S_CROWN.get()));
        this.spawnAtLocation(new ItemStack(ModItems.RANK_S_HAMMER.get()));

        if (source.getEntity() instanceof ServerPlayer player) {
            PlayerProgress progress = PlayerSkillData.get(player);
            PlayerSkillData.set(player, progress.withMonarchDefeated(true).addPassivePoints(3).addSkillPoints(5));
            ProgressSync.sendTo(player);
            player.sendSystemMessage(Component.translatable("message.myleveling.monarch_defeated"));
            com.l7cor1s.myleveling.quest.PlayerQuestData.onBossKilled(player, ModEntities.GATE_MONARCH.getId());
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.bossEvent.removeAllPlayers();
    }
}
