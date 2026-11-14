package com.l7cor1s.myleveling.boss;

/**
 * Visual / timing states for attack telegraphs without custom model packs.
 */
public enum BossAnimState {
    IDLE,
    WINDUP,
    STRIKE,
    CAST,
    SUMMON,
    DASH;

    public static BossAnimState byId(int id) {
        BossAnimState[] values = values();
        if (id < 0 || id >= values.length) {
            return IDLE;
        }
        return values[id];
    }
}
