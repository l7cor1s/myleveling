package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.network.chat.Component;

/**
 * Rank-locked passive choice. Exactly one option may be taken per branch+rank pair.
 */
public record PassiveSkill(
        String id,
        SkillBranch branch,
        Rank requiredRank,
        String pairId,
        int passivePointCost
) {
    public Component getDisplayName() {
        return Component.translatable("skill.myleveling.passive." + id + ".name");
    }

    public Component getDescription() {
        return Component.translatable("skill.myleveling.passive." + id + ".desc");
    }

    public String slotKey() {
        return branch.getId() + ":" + requiredRank.name();
    }
}
