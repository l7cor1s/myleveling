package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.network.chat.Component;

/**
 * Unlockable active ability. At most two can be equipped for HUD slots.
 */
public record ActiveSkill(
        String id,
        SkillBranch branch,
        Rank requiredRank,
        int passivePointCost,
        int cooldownTicks
) {
    public Component getDisplayName() {
        return Component.translatable("skill.myleveling.active." + id + ".name");
    }

    public Component getDescription() {
        return Component.translatable("skill.myleveling.active." + id + ".desc");
    }

    public Component getShortName() {
        return Component.translatable("skill.myleveling.active." + id + ".short");
    }
}
