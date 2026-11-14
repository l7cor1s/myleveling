package com.l7cor1s.myleveling.geo;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Mixin-style helper for legacy bosses that only need walk/idle/swing anims.
 */
public final class SimpleGeoBoss {
    private SimpleGeoBoss() {
    }

    public static AnimatableInstanceCache cache(GeoEntity entity) {
        return GeckoLibUtil.createInstanceCache(entity);
    }

    public static <T extends net.minecraft.world.entity.LivingEntity & GeoEntity> void registerWalkAttack(
            T entity, AnimatableManager.ControllerRegistrar controllers
    ) {
        controllers.add(GeoBossAnim.walkIdle(entity));
        controllers.add(DefaultAnimations.genericAttackAnimation(entity, DefaultAnimations.ATTACK_STRIKE));
    }
}
