package com.l7cor1s.myleveling.rank;

import java.util.Locale;

/**
 * Player ranks. Thresholds are total XP required.
 * Passive Points are granted when reaching the rank (not for starting E).
 */
public enum Rank {
    E(0, 0),
    D(5_000, 1),
    C(20_000, 2),
    B(60_000, 2),
    A(150_000, 3),
    S(400_000, 3);

    private final int requiredXp;
    private final int passivePointsOnReach;

    Rank(int requiredXp, int passivePointsOnReach) {
        this.requiredXp = requiredXp;
        this.passivePointsOnReach = passivePointsOnReach;
    }

    public int getRequiredXp() {
        return requiredXp;
    }

    public int getPassivePointsOnReach() {
        return passivePointsOnReach;
    }

    public String getDisplayName() {
        return name();
    }

    public Rank next() {
        int nextOrdinal = ordinal() + 1;
        Rank[] values = values();
        return nextOrdinal < values.length ? values[nextOrdinal] : this;
    }

    public boolean isMax() {
        return this == S;
    }

    public boolean isAtLeast(Rank other) {
        return ordinal() >= other.ordinal();
    }

    public static Rank fromXp(int xp) {
        Rank result = E;
        for (Rank rank : values()) {
            if (xp >= rank.requiredXp) {
                result = rank;
            } else {
                break;
            }
        }
        return result;
    }

    public static int xpToNextRank(int xp) {
        Rank current = fromXp(xp);
        if (current.isMax()) {
            return 0;
        }
        return Math.max(0, current.next().requiredXp - xp);
    }

    public static Rank fromName(String name) {
        return Rank.valueOf(name.trim().toUpperCase(Locale.ROOT));
    }
}
