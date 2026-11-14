package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.geo.SimpleGeoBoss;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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

/**
 * Weak summoned helper — short lifespan, low stats.
 */
public class ShadowMinionEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache geoCache = SimpleGeoBoss.cache(this);
    private static final EntityDataAccessor<Integer> KIND =
            SynchedEntityData.defineId(ShadowMinionEntity.class, EntityDataSerializers.INT);

    public enum Kind {
        SHADOW, FROST, ASH, ABYSS;

        public static Kind byId(int id) {
            Kind[] values = values();
            if (id < 0 || id >= values.length) {
                return SHADOW;
            }
            return values[id];
        }
    }

    private int lifeTicks = 20 * 25;

    public ShadowMinionEntity(EntityType<? extends ShadowMinionEntity> type, Level level) {
        super(type, level);
        this.xpReward = 3;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.ARMOR, 1.0);
    }

    public void configure(Kind kind, LivingEntity target) {
        this.entityData.set(KIND, kind.ordinal());
        if (target != null) {
            this.setTarget(target);
        }
        switch (kind) {
            case FROST -> this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28);
            case ASH -> {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.34);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.5);
            }
            case ABYSS -> {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(18.0);
                this.setHealth(18.0f);
                this.getAttribute(Attributes.ARMOR).setBaseValue(3.0);
            }
            default -> {
            }
        }
    }

    public Kind kind() {
        return Kind.byId(this.entityData.get(KIND));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(KIND, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            if (--lifeTicks <= 0) {
                this.discard();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Kind", this.entityData.get(KIND));
        tag.putInt("Life", lifeTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(KIND, tag.getInt("Kind"));
        lifeTicks = tag.contains("Life") ? tag.getInt("Life") : lifeTicks;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        SimpleGeoBoss.registerWalkAttack(this, controllers);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
