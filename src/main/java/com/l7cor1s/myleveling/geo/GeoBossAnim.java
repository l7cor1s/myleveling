package com.l7cor1s.myleveling.geo;

import com.l7cor1s.myleveling.boss.BossAnimState;
import com.l7cor1s.myleveling.boss.Kv2BossEntity;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;

import java.util.function.Function;

/**
 * Shared animation controllers for GeckoLib bosses.
 */
public final class GeoBossAnim {
    public static final RawAnimation IDLE = DefaultAnimations.IDLE;
    public static final RawAnimation WALK = DefaultAnimations.WALK;
    public static final RawAnimation STRIKE = DefaultAnimations.ATTACK_STRIKE;
    public static final RawAnimation CAST = DefaultAnimations.ATTACK_CAST;
    public static final RawAnimation CHARGE = DefaultAnimations.ATTACK_CHARGE;
    public static final RawAnimation SLAM = DefaultAnimations.ATTACK_SLAM;

    public static final RawAnimation KV2_IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation KV2_MOVE = RawAnimation.begin().thenLoop("move");
    public static final RawAnimation KV2_AIM = RawAnimation.begin().thenLoop("aim");
    public static final RawAnimation KV2_FIRE = RawAnimation.begin().thenPlay("fire");
    public static final RawAnimation KV2_RAGE = RawAnimation.begin().thenLoop("rage");

    private GeoBossAnim() {
    }

    public static <T extends LivingEntity & GeoAnimatable> AnimationController<T> walkIdle(T animatable) {
        return new AnimationController<>(animatable, "movement", 5, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(WALK);
            }
            return state.setAndContinue(IDLE);
        });
    }

    public static <T extends GeoAnimatable> AnimationController<T> combat(
            T animatable, Function<T, BossAnimState> animGetter
    ) {
        return new AnimationController<>(animatable, "combat", 2, state -> {
            BossAnimState anim = animGetter.apply(state.getAnimatable());
            if (anim == null || anim == BossAnimState.IDLE) {
                return PlayState.STOP;
            }
            return switch (anim) {
                case STRIKE -> state.setAndContinue(STRIKE);
                case CAST, SUMMON -> state.setAndContinue(CAST);
                case DASH -> state.setAndContinue(CHARGE);
                case WINDUP -> state.setAndContinue(SLAM);
                default -> PlayState.STOP;
            };
        });
    }

    public static AnimationController<Kv2BossEntity> kv2Controller(Kv2BossEntity tank) {
        return new AnimationController<>(tank, "tank", 3, state -> {
            Kv2BossEntity self = state.getAnimatable();
            BossAnimState anim = self.animState();
            if (anim == BossAnimState.CAST || anim == BossAnimState.WINDUP) {
                return state.setAndContinue(KV2_AIM);
            }
            if (self.phase() >= 3 && !state.isMoving() && anim == BossAnimState.IDLE) {
                return state.setAndContinue(KV2_RAGE);
            }
            if (state.isMoving()) {
                return state.setAndContinue(KV2_MOVE);
            }
            return state.setAndContinue(KV2_IDLE);
        });
    }

    public static AnimationController<Kv2BossEntity> kv2FireController(Kv2BossEntity tank) {
        return new AnimationController<>(tank, "fire", 0, state -> PlayState.STOP)
                .triggerableAnim("fire", KV2_FIRE);
    }
}
