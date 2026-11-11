package com.l7cor1s.myleveling.rank;

import java.util.Locale;

/**
 * Player combat ranks. Thresholds are total XP required to reach the rank.
 */
public enum Rank {
    E(0),
    D(100),
    C(350),
    B(800),
    A(1600),
    S(3200);

    private final int requiredXp;

    Rank(int requiredXp) {
        this.requiredXp = requiredXp;
    }

    public int getRequiredXp() {
        return requiredXp;
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

    /**
     * Highest rank whose required XP is less than or equal to {@code xp}.
     */
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

    /**
     * XP still needed to reach the next rank, or {@code 0} at max rank.
     */
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
