package com.l7cor1s.myleveling.geo;

import com.l7cor1s.myleveling.boss.BossAnimState;
import com.l7cor1s.myleveling.boss.Kv2BossEntity;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/** GeckoLib controllers for KV-2 (the only fully custom boss). */
public final class GeoBossAnim {
    public static final RawAnimation KV2_IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation KV2_MOVE = RawAnimation.begin().thenLoop("move");
    public static final RawAnimation KV2_AIM = RawAnimation.begin().thenLoop("aim");
    public static final RawAnimation KV2_FIRE = RawAnimation.begin().thenPlay("fire");
    public static final RawAnimation KV2_RAGE = RawAnimation.begin().thenLoop("rage");

    private GeoBossAnim() {
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
