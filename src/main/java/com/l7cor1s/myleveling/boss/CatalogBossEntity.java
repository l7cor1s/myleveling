package com.l7cor1s.myleveling.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Data-driven ranked boss — combat kit selected by {@link BossId}.
 */
public class CatalogBossEntity extends AbstractRankedBoss {
    private int attackCooldown;

    public CatalogBossEntity(EntityType<? extends CatalogBossEntity> type, Level level, BossId bossId) {
        super(type, level, bossId);
    }

    // Factories for EntityType.Builder.of
    public static CatalogBossEntity grove(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.GROVE_STALKER); }
    public static CatalogBossEntity dune(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.DUNE_SCORPION); }
    public static CatalogBossEntity frost(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.FROST_HOWLER); }
    public static CatalogBossEntity bog(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.BOG_WITCH); }
    public static CatalogBossEntity reef(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.REEF_LEVIATHAN); }
    public static CatalogBossEntity ash(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.ASH_IMP_LORD); }
    public static CatalogBossEntity thunder(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.THUNDER_COLOSSUS); }
    public static CatalogBossEntity crystal(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.CRYSTAL_ORACLE); }
    public static CatalogBossEntity crimson(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.CRIMSON_SUMMONER); }
    public static CatalogBossEntity abyss(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.ABYSS_KNIGHT); }
    public static CatalogBossEntity sand(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.SANDSTORM_EMPRESS); }
    public static CatalogBossEntity voidReaper(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.VOID_REAPER); }
    public static CatalogBossEntity behemoth(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.INFERNAL_BEHEMOTH); }
    public static CatalogBossEntity glacial(EntityType<CatalogBossEntity> t, Level l) { return new CatalogBossEntity(t, l, BossId.GLACIAL_SOVEREIGN); }

    @Override
    protected void onPhaseChange(int newPhase) {
        if (newPhase >= 2) {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 90, newPhase - 2));
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 90, 0));
        }
        if (newPhase >= 3) {
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 20, 1));
        }
    }

    @Override
    protected void tickCombat() {
        if (!(this.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        double dist = this.distanceToSqr(target);
        int phase = phase();
        int cd = switch (bossId.tier()) {
            case MINI -> 45;
            case MID -> 35;
            case LARGE -> 28;
        } - (phase - 1) * 4;

        switch (bossId) {
            case GROVE_STALKER -> {
                beginAnim(BossAnimState.DASH, 12);
                attackCooldown = cd;
            }
            case DUNE_SCORPION -> {
                beginAnim(BossAnimState.WINDUP, 16);
                attackCooldown = cd;
            }
            case FROST_HOWLER -> {
                beginAnim(BossAnimState.CAST, 18);
                attackCooldown = cd;
            }
            case BOG_WITCH -> {
                beginAnim(this.random.nextBoolean() ? BossAnimState.CAST : BossAnimState.SUMMON, 20);
                attackCooldown = cd + 10;
            }
            case REEF_LEVIATHAN -> {
                beginAnim(BossAnimState.WINDUP, 18);
                attackCooldown = cd;
            }
            case ASH_IMP_LORD -> {
                beginAnim(BossAnimState.DASH, 10);
                attackCooldown = cd - 5;
            }
            case THUNDER_COLOSSUS -> {
                beginAnim(dist < 36 ? BossAnimState.STRIKE : BossAnimState.CAST, 22);
                attackCooldown = cd;
            }
            case CRYSTAL_ORACLE -> {
                beginAnim(BossAnimState.CAST, 16);
                attackCooldown = cd;
            }
            case CRIMSON_SUMMONER -> {
                beginAnim(phase >= 2 || this.random.nextFloat() < 0.4f ? BossAnimState.SUMMON : BossAnimState.CAST, 20);
                attackCooldown = cd + 5;
            }
            case ABYSS_KNIGHT -> {
                beginAnim(BossAnimState.WINDUP, 20);
                attackCooldown = cd;
            }
            case SANDSTORM_EMPRESS -> {
                beginAnim(BossAnimState.CAST, 18);
                attackCooldown = cd;
            }
            case VOID_REAPER -> {
                beginAnim(this.random.nextFloat() < 0.5f ? BossAnimState.DASH : BossAnimState.CAST, 14);
                attackCooldown = cd;
            }
            case INFERNAL_BEHEMOTH -> {
                beginAnim(BossAnimState.STRIKE, 24);
                attackCooldown = cd;
            }
            case GLACIAL_SOVEREIGN -> {
                beginAnim(phase >= 2 && this.random.nextFloat() < 0.45f ? BossAnimState.SUMMON : BossAnimState.CAST, 20);
                attackCooldown = cd;
            }
        }
    }

    @Override
    protected void onAnimResolve(BossAnimState state) {
        LivingEntity target = this.getTarget();
        float phaseBonus = 1.0f + (phase() - 1) * 0.25f;

        switch (bossId) {
            case GROVE_STALKER -> {
                if (target != null) {
                    dashToward(target, 1.4);
                    if (this.distanceToSqr(target) < 16) {
                        target.hurt(this.damageSources().mobAttack(this), 6.0f * phaseBonus);
                        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0));
                    }
                }
                pulse(ParticleTypes.CRIT);
            }
            case DUNE_SCORPION -> {
                aoePlayers(3.5, 5.0f * phaseBonus, true);
                for (LivingEntity e : nearbyPlayers(3.5)) {
                    e.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
                }
                pulse(ParticleTypes.CRIT);
            }
            case FROST_HOWLER -> {
                if (target != null) {
                    target.hurt(this.damageSources().magic(), 5.0f * phaseBonus);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
                    target.setTicksFrozen(Math.min(140, target.getTicksFrozen() + 80));
                }
                pulse(ParticleTypes.SNOWFLAKE);
            }
            case BOG_WITCH -> {
                if (state == BossAnimState.SUMMON) {
                    spawnMinions(ShadowMinionEntity.Kind.SHADOW, 2);
                } else {
                    aoePlayers(4.0, 3.5f * phaseBonus, false);
                    for (LivingEntity e : nearbyPlayers(4.0)) {
                        e.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                        e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
                    }
                    pulse(ParticleTypes.WITCH);
                }
            }
            case REEF_LEVIATHAN -> {
                aoePlayers(4.0, 6.0f * phaseBonus, true);
                for (LivingEntity e : nearbyPlayers(4.0)) {
                    Vec3 push = e.position().subtract(this.position()).normalize().scale(1.1);
                    e.setDeltaMovement(e.getDeltaMovement().add(push.x, 0.35, push.z));
                    e.hasImpulse = true;
                }
                pulse(ParticleTypes.BUBBLE);
            }
            case ASH_IMP_LORD -> {
                if (target != null) {
                    dashToward(target, 1.6);
                    target.hurt(this.damageSources().mobAttack(this), 5.5f * phaseBonus);
                    target.igniteForSeconds(4);
                }
                pulse(ParticleTypes.FLAME);
            }
            case THUNDER_COLOSSUS -> {
                if (state == BossAnimState.CAST && target != null && this.level() instanceof ServerLevel server) {
                    server.explode(this, target.getX(), target.getY(), target.getZ(), 1.6f * phaseBonus, false, Level.ExplosionInteraction.NONE);
                    target.hurt(this.damageSources().lightningBolt(), 7.0f * phaseBonus);
                    pulse(ParticleTypes.ELECTRIC_SPARK);
                } else {
                    aoePlayers(4.5, 8.0f * phaseBonus, true);
                    pulse(ParticleTypes.EXPLOSION);
                }
            }
            case CRYSTAL_ORACLE -> {
                if (target != null) {
                    target.hurt(this.damageSources().magic(), 7.0f * phaseBonus);
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
                    // bounce
                    for (LivingEntity e : nearbyPlayers(8.0)) {
                        if (e != target && this.random.nextFloat() < 0.5f) {
                            e.hurt(this.damageSources().magic(), 3.0f * phaseBonus);
                        }
                    }
                }
                pulse(ParticleTypes.END_ROD);
            }
            case CRIMSON_SUMMONER -> {
                if (state == BossAnimState.SUMMON) {
                    spawnMinions(ShadowMinionEntity.Kind.ASH, 2 + phase());
                } else if (target != null) {
                    target.hurt(this.damageSources().magic(), 6.0f * phaseBonus);
                    target.igniteForSeconds(3);
                    pulse(ParticleTypes.FLAME);
                }
            }
            case ABYSS_KNIGHT -> {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1));
                aoePlayers(3.5, 9.0f * phaseBonus, true);
                if (target != null) {
                    target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
                }
                pulse(ParticleTypes.SCULK_SOUL);
            }
            case SANDSTORM_EMPRESS -> {
                aoePlayers(5.0, 5.0f * phaseBonus, true);
                for (LivingEntity e : nearbyPlayers(5.0)) {
                    e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0));
                    e.addEffect(new MobEffectInstance(MobEffects.HUNGER, 80, 0));
                }
                if (target != null) {
                    Vec3 pull = this.position().subtract(target.position()).normalize().scale(0.6);
                    target.setDeltaMovement(target.getDeltaMovement().add(pull));
                    target.hasImpulse = true;
                }
                pulse(ParticleTypes.CLOUD);
            }
            case VOID_REAPER -> {
                if (state == BossAnimState.DASH && target != null) {
                    // teleport behind
                    Vec3 behind = target.position().subtract(target.getLookAngle().scale(2.0));
                    this.teleportTo(behind.x, target.getY(), behind.z);
                    target.hurt(this.damageSources().mobAttack(this), 10.0f * phaseBonus);
                    pulse(ParticleTypes.PORTAL);
                } else if (target != null) {
                    target.hurt(this.damageSources().magic(), 8.0f * phaseBonus);
                    target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 0));
                    pulse(ParticleTypes.REVERSE_PORTAL);
                }
            }
            case INFERNAL_BEHEMOTH -> {
                aoePlayers(5.5, 12.0f * phaseBonus, true);
                for (LivingEntity e : nearbyPlayers(5.5)) {
                    e.igniteForSeconds(5);
                }
                if (this.level() instanceof ServerLevel server) {
                    server.explode(this, this.getX(), this.getY(), this.getZ(), 2.0f, false, Level.ExplosionInteraction.NONE);
                }
                pulse(ParticleTypes.LAVA);
            }
            case GLACIAL_SOVEREIGN -> {
                if (state == BossAnimState.SUMMON) {
                    spawnMinions(ShadowMinionEntity.Kind.FROST, 2 + phase());
                } else {
                    aoePlayers(5.0, 8.0f * phaseBonus, true);
                    for (LivingEntity e : nearbyPlayers(5.0)) {
                        e.setTicksFrozen(Math.min(200, e.getTicksFrozen() + 120));
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2));
                    }
                    pulse(ParticleTypes.SNOWFLAKE);
                }
            }
        }
    }

    private java.util.List<LivingEntity> nearbyPlayers(double radius) {
        return this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius),
                e -> e instanceof Player && e.isAlive());
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        if (bossId.archetype() == BossId.Archetype.FLYING) {
            return false;
        }
        return super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        // Soft hover for flying archetype
        if (bossId.archetype() == BossId.Archetype.FLYING && this.getTarget() != null) {
            if (this.getY() < this.getTarget().getY() + 1.5) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.05, 0));
            }
            if (this.getAttribute(Attributes.GRAVITY) != null) {
                // keep light gravity feel via upward nudge only
            }
        }
    }
}
