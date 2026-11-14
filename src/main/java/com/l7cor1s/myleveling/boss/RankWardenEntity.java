package com.l7cor1s.myleveling.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Mid boss (B–A): two phases — enrage under 50% HP.
 */
public class RankWardenEntity extends Monster {
    private boolean enraged;

    public RankWardenEntity(EntityType<? extends RankWardenEntity> type, Level level) {
        super(type, level);
        this.xpReward = 120;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 180.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }
        if (!enraged && this.getHealth() <= this.getMaxHealth() * 0.5f) {
            enraged = true;
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 60, 1));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 60, 1));
            if (this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getY() + 1.5, this.getZ(), 30, 0.5, 0.5, 0.5, 0.05);
            }
            for (var p : this.level().getEntitiesOfClass(net.minecraft.server.level.ServerPlayer.class, this.getBoundingBox().inflate(40))) {
                p.displayClientMessage(net.minecraft.network.chat.Component.translatable("boss.myleveling.rank_warden.phase"), false);
            }
        }
        if (enraged && this.tickCount % 40 == 0 && this.getTarget() instanceof LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
            target.hurt(this.damageSources().mobAttack(this), 4.0f);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        this.spawnAtLocation(new ItemStack(com.l7cor1s.myleveling.item.ModItems.RANK_B_BLADE.get()));
        this.spawnAtLocation(new ItemStack(com.l7cor1s.myleveling.item.ModItems.RANK_B_CHEST.get()));
        if (this.getRandom().nextFloat() < 0.5f) {
            this.spawnAtLocation(new ItemStack(com.l7cor1s.myleveling.item.ModItems.GATE_KEY.get()));
        }
    }
}
