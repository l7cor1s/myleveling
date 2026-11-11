package com.l7cor1s.myleveling.rank;

import java.util.Locale;

/**
 * Player ranks. Thresholds are total XP required; progression is intentionally slow.
 * Skill points are granted when reaching the rank (not for starting E).
 */
public enum Rank {
    E(0, 0),
    D(5_000, 2),
    C(20_000, 2),
    B(60_000, 3),
    A(150_000, 3),
    S(400_000, 4);

    private final int requiredXp;
    private final int skillPointsOnReach;

    Rank(int requiredXp, int skillPointsOnReach) {
        this.requiredXp = requiredXp;
        this.skillPointsOnReach = skillPointsOnReach;
    }

    public int getRequiredXp() {
        return requiredXp;
    }

    public int getSkillPointsOnReach() {
        return skillPointsOnReach;
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
