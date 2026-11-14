package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.MyLeveling;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Small permanent upgrades bought with Skill Points (~every 400 XP).
 */
public record MinorUpgrade(
        String id,
        int maxLevel,
        int costPerLevel,
        Holder<Attribute> attribute,
        double amountPerLevel,
        AttributeModifier.Operation operation
) {
    private static final Map<String, MinorUpgrade> BY_ID = new LinkedHashMap<>();

    public static final MinorUpgrade VITALITY = register(new MinorUpgrade(
            "vitality", 10, 1, Attributes.MAX_HEALTH, 1.0, AttributeModifier.Operation.ADD_VALUE));
    public static final MinorUpgrade SWIFTNESS = register(new MinorUpgrade(
            "swiftness", 10, 1, Attributes.MOVEMENT_SPEED, 0.005, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final MinorUpgrade FORTUNE = register(new MinorUpgrade(
            "fortune", 8, 1, Attributes.LUCK, 0.25, AttributeModifier.Operation.ADD_VALUE));
    public static final MinorUpgrade MIGHT = register(new MinorUpgrade(
            "might", 10, 1, Attributes.ATTACK_DAMAGE, 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final MinorUpgrade REACH = register(new MinorUpgrade(
            "reach", 8, 1, Attributes.ENTITY_INTERACTION_RANGE, 0.05, AttributeModifier.Operation.ADD_VALUE));
    public static final MinorUpgrade EXCAVATION = register(new MinorUpgrade(
            "excavation", 10, 1, Attributes.BLOCK_BREAK_SPEED, 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final MinorUpgrade TENACITY = register(new MinorUpgrade(
            "tenacity", 8, 1, Attributes.ARMOR, 0.5, AttributeModifier.Operation.ADD_VALUE));

    public Component getDisplayName() {
        return Component.translatable("skill.myleveling.minor." + id + ".name");
    }

    public Component getDescription() {
        return Component.translatable("skill.myleveling.minor." + id + ".desc");
    }

    public ResourceLocation modifierId() {
        return ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "minor/" + id);
    }

    public AttributeModifier modifierForLevel(int level) {
        return new AttributeModifier(modifierId(), amountPerLevel * level, operation);
    }

    public static Collection<MinorUpgrade> all() {
        return BY_ID.values();
    }

    public static Optional<MinorUpgrade> byId(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    private static MinorUpgrade register(MinorUpgrade upgrade) {
        BY_ID.put(upgrade.id, upgrade);
        return upgrade;
    }
}
