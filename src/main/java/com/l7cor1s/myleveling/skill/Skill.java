package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.core.Holder;

import java.util.List;
import java.util.Optional;

/**
 * A single unlockable skill node. Effects are attribute-based so they stack safely
 * with Epic Fight / Apotheosis instead of rewriting the damage pipeline.
 */
public record Skill(
        String id,
        SkillBranch branch,
        int cost,
        Rank minRank,
        Optional<String> prerequisite,
        List<AttributeBonus> bonuses
) {
    public Component getDisplayName() {
        return Component.translatable("skill.myleveling." + id + ".name");
    }

    public Component getDescription() {
        return Component.translatable("skill.myleveling." + id + ".desc");
    }

    public record AttributeBonus(
            Holder<Attribute> attribute,
            ResourceLocation modifierId,
            double amount,
            AttributeModifier.Operation operation
    ) {
        public AttributeModifier toModifier() {
            return new AttributeModifier(modifierId, amount, operation);
        }
    }
}
