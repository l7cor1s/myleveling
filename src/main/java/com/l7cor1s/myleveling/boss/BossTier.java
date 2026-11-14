package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.rank.Rank;

public enum BossTier {
    MINI(Rank.D, Rank.C),
    MID(Rank.B, Rank.A),
    LARGE(Rank.S, Rank.S);

    private final Rank lootMin;
    private final Rank lootMax;

    BossTier(Rank lootMin, Rank lootMax) {
        this.lootMin = lootMin;
        this.lootMax = lootMax;
    }

    public Rank lootMin() {
        return lootMin;
    }

    public Rank lootMax() {
        return lootMax;
    }

    public boolean hasPhases() {
        return this != MINI;
    }

    public boolean usesBossBar() {
        return this != MINI;
    }
}
