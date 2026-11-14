package com.l7cor1s.myleveling.xp;

/**
 * Combat XP tiers. Pack makers can extend classification via entity type tags.
 */
public enum MobTier {
    NORMAL(6),
    STRONG(28),
    BOSS(250);

    private final int baseXp;

    MobTier(int baseXp) {
        this.baseXp = baseXp;
    }

    public int getBaseXp() {
        return baseXp;
    }
}
