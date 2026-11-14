package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.geo.SimpleGeoBoss;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Mini-boss (D–C): fast shadow scout.
 */
public class ShadowScoutEntity extends Monster implements GeoEntity {
    private final AnimatableInstanceCache geoCache = SimpleGeoBoss.cache(this);
    private boolean announced;

    public ShadowScoutEntity(EntityType<? extends ShadowScoutEntity> type, Level level) {
        super(type, level);
        this.xpReward = 40;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && !announced && this.getTarget() instanceof Player) {
            announced = true;
            for (var p : this.level().getEntitiesOfClass(net.minecraft.server.level.ServerPlayer.class, this.getBoundingBox().inflate(32))) {
                p.displayClientMessage(net.minecraft.network.chat.Component.translatable("boss.myleveling.shadow_scout.aggro"), false);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.34)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.FOLLOW_RANGE, 28.0)
                .add(Attributes.ARMOR, 4.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        this.spawnAtLocation(new ItemStack(com.l7cor1s.myleveling.item.ModItems.RANK_D_BLADE.get()));
        if (this.getRandom().nextFloat() < 0.35f) {
            this.spawnAtLocation(new ItemStack(com.l7cor1s.myleveling.item.ModItems.RANK_D_RING.get()));
        }
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
