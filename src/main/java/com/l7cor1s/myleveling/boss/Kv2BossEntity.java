package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.item.ModItems;
import com.l7cor1s.myleveling.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.phys.Vec3;

/**
 * Meme large boss: KV-2 — slow howitzer tank with KorbenDallas-style voice lines.
 */
public class Kv2BossEntity extends Monster {
    private static final EntityDataAccessor<Integer> ANIM =
            SynchedEntityData.defineId(Kv2BossEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(Kv2BossEntity.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.myleveling.kv2"),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.NOTCHED_10
    );

    private boolean announcedSpawn;
    private boolean announcedAggro;
    private boolean announcedRage;
    private int animTicks;
    private BossAnimState pending = BossAnimState.IDLE;
    private int attackCooldown;
    private int voiceCooldown;
    private Vec3 aimPoint;

    public Kv2BossEntity(EntityType<? extends Kv2BossEntity> type, Level level) {
        super(type, level);
        this.xpReward = 420;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 450.0)
                .add(Attributes.MOVEMENT_SPEED, 0.16)
                .add(Attributes.ATTACK_DAMAGE, 22.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0);
    }

    public BossAnimState animState() {
        return BossAnimState.byId(this.entityData.get(ANIM));
    }

    public int phase() {
        return this.entityData.get(PHASE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIM, 0);
        builder.define(PHASE, 1);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // Slow melee poke only — main damage is howitzer
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 0.85, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
        if (!announcedSpawn) {
            announcedSpawn = true;
            yell("spawn");
            playVoice(ModSounds.AAAAY.get());
        }
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
            clientFx();
            return;
        }

        if (!announcedAggro && this.getTarget() instanceof Player) {
            announcedAggro = true;
            yell("aggro");
            playVoice(ModSounds.SHUT_UP.get());
        }

        float hp = this.getHealth() / this.getMaxHealth();
        if (phase() == 1 && hp <= 0.55f) {
            this.entityData.set(PHASE, 2);
            yell("phase");
            playVoice(ModSounds.VASELINE_SCREECH.get());
            burst(ParticleTypes.LAVA, 60);
        }
        if (phase() == 2 && hp <= 0.28f) {
            this.entityData.set(PHASE, 3);
            yell("rage");
            playVoice(ModSounds.CORBEN_SCREAM.get());
            announcedRage = true;
            burst(ParticleTypes.FLAME, 80);
        }
        if (!announcedRage && hp <= 0.18f) {
            announcedRage = true;
            yell("low_hp");
            playVoice(ModSounds.CORBEN_SCREAM.get());
        }

        if (voiceCooldown > 0) {
            voiceCooldown--;
        }

        if (animTicks > 0) {
            animTicks--;
            // Telegraph ring at aim point while charging howitzer
            if (aimPoint != null && this.level() instanceof ServerLevel server && this.tickCount % 2 == 0) {
                double progress = 1.0 - (animTicks / 40.0);
                double radius = 4.5 - progress * 1.5;
                for (int i = 0; i < 20; i++) {
                    double a = (Math.PI * 2 * i) / 20.0;
                    server.sendParticles(ParticleTypes.FLAME,
                            aimPoint.x + Math.cos(a) * radius,
                            aimPoint.y + 0.15,
                            aimPoint.z + Math.sin(a) * radius,
                            1, 0, 0.01, 0, 0);
                }
                server.sendParticles(ParticleTypes.SMOKE, aimPoint.x, aimPoint.y + 0.2, aimPoint.z, 4, 0.4, 0.1, 0.4, 0.01);
                server.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 2.2, this.getZ(), 3, 0.3, 0.2, 0.3, 0.02);
            }
            if (animTicks == 0) {
                BossAnimState done = pending;
                this.entityData.set(ANIM, BossAnimState.IDLE.ordinal());
                pending = BossAnimState.IDLE;
                if (done == BossAnimState.CAST || done == BossAnimState.WINDUP) {
                    fireHowitzer();
                }
            }
            return;
        }

        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }
        if (!(this.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }

        // Main attack: howitzer
        aimPoint = target.position();
        this.entityData.set(ANIM, BossAnimState.CAST.ordinal());
        pending = BossAnimState.CAST;
        animTicks = phase() >= 3 ? 28 : 40;
        attackCooldown = phase() >= 3 ? 55 : 75;
        yell("aim");
        if (voiceCooldown <= 0) {
            playVoice(phase() >= 2 ? ModSounds.FIRE_ON_KHUTOR.get() : ModSounds.PENETRATION.get());
            voiceCooldown = 80;
        }
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 2.4, this.getZ(), 20, 0.4, 0.4, 0.4, 0.02);
        }
    }

    private void fireHowitzer() {
        if (aimPoint == null || !(this.level() instanceof ServerLevel server)) {
            return;
        }
        Vec3 hit = aimPoint;
        float power = 3.2f + phase() * 0.6f;
        server.explode(this, hit.x, hit.y, hit.z, power, false, Level.ExplosionInteraction.MOB);
        server.sendParticles(ParticleTypes.EXPLOSION_EMITTER, hit.x, hit.y + 0.5, hit.z, 1, 0, 0, 0, 0);
        server.sendParticles(ParticleTypes.LAVA, hit.x, hit.y + 0.5, hit.z, 40, 1.5, 0.6, 1.5, 0.05);
        server.sendParticles(ParticleTypes.FLASH, hit.x, hit.y + 1.0, hit.z, 1, 0, 0, 0, 0);
        playVoice(ModSounds.PENETRATION.get());
        yell("shot");

        for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class,
                new net.minecraft.world.phys.AABB(hit.x - 5.5, hit.y - 3, hit.z - 5.5, hit.x + 5.5, hit.y + 4, hit.z + 5.5),
                ent -> ent instanceof Player && ent.isAlive())) {
            e.hurt(this.damageSources().mobAttack(this), 8.0f + phase() * 3.0f);
            e.setDeltaMovement(e.getDeltaMovement().add(0, 0.45, 0));
            e.hasImpulse = true;
        }
        aimPoint = null;

        if (phase() >= 3 && this.random.nextFloat() < 0.45f && voiceCooldown <= 0) {
            playVoice(ModSounds.CORBEN_SCREAM.get());
            voiceCooldown = 60;
        }
    }

    private void clientFx() {
        BossAnimState anim = animState();
        if (anim == BossAnimState.CAST || anim == BossAnimState.WINDUP) {
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(ParticleTypes.SMOKE,
                        this.getX() + (this.random.nextDouble() - 0.5),
                        this.getY() + 2.0 + this.random.nextDouble(),
                        this.getZ() + (this.random.nextDouble() - 0.5),
                        0, 0.05, 0);
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.6,
                        this.getY() + 2.3,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.6,
                        0, 0.08, 0);
            }
        } else if (phase() >= 2 && this.tickCount % 5 == 0) {
            this.level().addParticle(ParticleTypes.LAVA,
                    this.getX() + (this.random.nextDouble() - 0.5),
                    this.getY() + 1.2,
                    this.getZ() + (this.random.nextDouble() - 0.5),
                    0, 0.02, 0);
        }
    }

    private void yell(String key) {
        Component line = Component.translatable("boss.myleveling.kv2." + key);
        for (ServerPlayer player : this.level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(56.0))) {
            player.displayClientMessage(Component.literal("§c[§6КВ-2§c]§r ").append(line), false);
        }
    }

    private void playVoice(SoundEvent sound) {
        this.level().playSound(null, this.blockPosition(), sound, SoundSource.HOSTILE, 1.6f, 0.85f + this.random.nextFloat() * 0.2f);
    }

    private void burst(net.minecraft.core.particles.SimpleParticleType type, int count) {
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(type, this.getX(), this.getY() + 1.5, this.getZ(), count, 1.2, 1.0, 1.2, 0.05);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        yell("death");
        playVoice(ModSounds.WIG_DROP.get());
        this.spawnAtLocation(new ItemStack(ModItems.KV2_HOWITZER.get()));
        this.spawnAtLocation(new ItemStack(ModItems.RANK_S_CHEST.get()));
        this.spawnAtLocation(new ItemStack(ModItems.ARTIFACT_S_SYSTEM.get()));
        if (this.random.nextFloat() < 0.7f) {
            this.spawnAtLocation(new ItemStack(ModItems.GATE_KEY.get()));
        }
        if (this.random.nextFloat() < 0.5f) {
            this.spawnAtLocation(new ItemStack(ModItems.RANK_A_BLADE.get()));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Phase", phase());
        tag.putBoolean("AnnSpawn", announcedSpawn);
        tag.putBoolean("AnnAggro", announcedAggro);
        tag.putBoolean("AnnRage", announcedRage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(PHASE, Math.max(1, tag.getInt("Phase")));
        announcedSpawn = tag.getBoolean("AnnSpawn");
        announcedAggro = tag.getBoolean("AnnAggro");
        announcedRage = tag.getBoolean("AnnRage");
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.bossEvent.removeAllPlayers();
    }
}
