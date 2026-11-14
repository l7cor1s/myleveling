package com.l7cor1s.myleveling.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Shared ranked-boss framework: dialogue, phases, attack telegraphs, boss bar, loot.
 */
public abstract class AbstractRankedBoss extends Monster {
    private static final EntityDataAccessor<Integer> ANIM =
            SynchedEntityData.defineId(AbstractRankedBoss.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(AbstractRankedBoss.class, EntityDataSerializers.INT);

    protected final BossId bossId;
    private final ServerBossEvent bossEvent;
    private boolean announcedSpawn;
    private boolean announcedAggro;
    private boolean announcedLowHp;
    private int animTicks;
    private BossAnimState pendingResolve = BossAnimState.IDLE;

    protected AbstractRankedBoss(EntityType<? extends AbstractRankedBoss> type, Level level, BossId bossId) {
        super(type, level);
        this.bossId = bossId;
        this.xpReward = bossId.xpReward();
        this.bossEvent = new ServerBossEvent(
                Component.translatable("entity.myleveling." + bossId.id()),
                bossId.barColor(),
                BossEvent.BossBarOverlay.NOTCHED_6
        );
        this.bossEvent.setVisible(bossId.tier().usesBossBar());
    }

    public static AttributeSupplier.Builder attributesFor(BossId id) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, id.maxHealth())
                .add(Attributes.MOVEMENT_SPEED, id.speed())
                .add(Attributes.ATTACK_DAMAGE, id.damage())
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ARMOR, id.armor())
                .add(Attributes.KNOCKBACK_RESISTANCE, id.tier() == BossTier.LARGE ? 0.8 : (id.tier() == BossTier.MID ? 0.5 : 0.2))
                .add(Attributes.ATTACK_KNOCKBACK, id.tier() == BossTier.LARGE ? 1.2 : 0.6);
    }

    public BossId bossId() {
        return bossId;
    }

    public BossAnimState animState() {
        return BossAnimState.byId(this.entityData.get(ANIM));
    }

    public int phase() {
        return this.entityData.get(PHASE);
    }

    protected void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    protected void beginAnim(BossAnimState state, int ticks) {
        this.entityData.set(ANIM, state.ordinal());
        this.animTicks = ticks;
        this.pendingResolve = state;
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.blockPosition(),
                    state == BossAnimState.CAST || state == BossAnimState.SUMMON ? SoundEvents.EVOKER_CAST_SPELL : SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.HOSTILE, 1.0f, 0.8f + this.random.nextFloat() * 0.3f);
        }
    }

    protected boolean isAnimating() {
        return animTicks > 0 && animState() != BossAnimState.IDLE;
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
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.95));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (bossId.tier().usesBossBar()) {
            this.bossEvent.addPlayer(player);
        }
        if (!announcedSpawn) {
            announcedSpawn = true;
            speak("spawn");
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
        if (bossId.tier().usesBossBar()) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }

        if (this.level().isClientSide()) {
            clientAnimParticles();
            return;
        }

        if (!announcedAggro && this.getTarget() instanceof Player) {
            announcedAggro = true;
            speak("aggro");
        }

        float hpRatio = this.getHealth() / this.getMaxHealth();
        int maxPhases = bossId.maxPhases();
        if (maxPhases >= 2 && phase() == 1 && hpRatio <= 0.55f) {
            setPhase(2);
            speak("phase");
            onPhaseChange(2);
            pulse(ParticleTypes.FLAME);
        }
        if (maxPhases >= 3 && phase() == 2 && hpRatio <= 0.30f) {
            setPhase(3);
            speak("phase");
            onPhaseChange(3);
            pulse(ParticleTypes.SOUL_FIRE_FLAME);
        }
        if (!announcedLowHp && hpRatio <= 0.18f) {
            announcedLowHp = true;
            speak("low_hp");
        }

        if (animTicks > 0) {
            animTicks--;
            if (animTicks == 0) {
                BossAnimState resolved = pendingResolve;
                this.entityData.set(ANIM, BossAnimState.IDLE.ordinal());
                pendingResolve = BossAnimState.IDLE;
                if (resolved != BossAnimState.IDLE) {
                    onAnimResolve(resolved);
                }
            } else if (animState() == BossAnimState.WINDUP || animState() == BossAnimState.CAST) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.2, 1.0, 0.2));
            }
        }

        if (!isAnimating()) {
            tickCombat();
        }
    }

    protected abstract void tickCombat();

    protected void onPhaseChange(int newPhase) {
    }

    protected void onAnimResolve(BossAnimState state) {
    }

    protected void speak(String key) {
        Component line = Component.translatable("boss.myleveling." + bossId.id() + "." + key);
        for (ServerPlayer player : this.level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(48.0))) {
            player.displayClientMessage(Component.literal("§5[§d" + this.getDisplayName().getString() + "§5]§r ").append(line), false);
        }
    }

    protected void pulse(SimpleParticleType type) {
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(type, this.getX(), this.getY() + 1.2, this.getZ(), 40, 1.0, 1.0, 1.0, 0.05);
        }
    }

    protected void dashToward(LivingEntity target, double strength) {
        Vec3 dir = target.position().subtract(this.position());
        if (dir.lengthSqr() < 0.001) {
            return;
        }
        dir = dir.normalize().scale(strength);
        this.setDeltaMovement(dir.x, Math.max(0.15, dir.y + 0.15), dir.z);
        this.hasImpulse = true;
    }

    protected void aoePlayers(double radius, float damage, boolean slow) {
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius),
                ent -> ent instanceof Player && ent.isAlive())) {
            e.hurt(this.damageSources().mobAttack(this), damage);
            if (slow) {
                e.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
            }
        }
    }

    protected void spawnMinions(ShadowMinionEntity.Kind kind, int count) {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        for (int i = 0; i < count; i++) {
            ShadowMinionEntity minion = ModEntities.SHADOW_MINION.get().create(server);
            if (minion == null) {
                continue;
            }
            double ox = (this.random.nextDouble() - 0.5) * 3.0;
            double oz = (this.random.nextDouble() - 0.5) * 3.0;
            minion.moveTo(this.getX() + ox, this.getY(), this.getZ() + oz, this.getYRot(), 0);
            minion.configure(kind, this.getTarget());
            server.addFreshEntity(minion);
        }
        pulse(ParticleTypes.SMOKE);
    }

    private void clientAnimParticles() {
        BossAnimState state = animState();
        if (state == BossAnimState.IDLE) {
            return;
        }
        SimpleParticleType type = switch (state) {
            case WINDUP, STRIKE -> ParticleTypes.CRIT;
            case CAST -> ParticleTypes.ENCHANT;
            case SUMMON -> ParticleTypes.SOUL;
            case DASH -> ParticleTypes.CLOUD;
            default -> ParticleTypes.SMOKE;
        };
        for (int i = 0; i < 3; i++) {
            this.level().addParticle(type,
                    this.getX() + (this.random.nextDouble() - 0.5),
                    this.getY() + 1.0 + this.random.nextDouble(),
                    this.getZ() + (this.random.nextDouble() - 0.5),
                    0, 0.02, 0);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        speak("death");
        BossLootHelper.dropStandard(this, bossId, this.random);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Phase", phase());
        tag.putBoolean("AnnSpawn", announcedSpawn);
        tag.putBoolean("AnnAggro", announcedAggro);
        tag.putBoolean("AnnLow", announcedLowHp);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setPhase(Math.max(1, tag.getInt("Phase")));
        announcedSpawn = tag.getBoolean("AnnSpawn");
        announcedAggro = tag.getBoolean("AnnAggro");
        announcedLowHp = tag.getBoolean("AnnLow");
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.bossEvent.removeAllPlayers();
    }
}
