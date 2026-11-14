package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.compat.CompatMods;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/**
 * Attribute-backed passives (HP, speed, mining...). Combat procs stay in event handlers.
 */
public final class PassiveAttributeEffects {
    private PassiveAttributeEffects() {
    }

    public static void clear(Player player) {
        remove(player, Attributes.MAX_HEALTH, "passive/surv_max_hp");
        remove(player, Attributes.MOVEMENT_SPEED, "passive/surv_speed");
        remove(player, Attributes.ARMOR, "passive/surv_resistance");
        remove(player, Attributes.BLOCK_BREAK_SPEED, "passive/eng_mine_speed");
        remove(player, Attributes.BLOCK_BREAK_SPEED, "passive/eng_tool_efficiency");
        remove(player, Attributes.BLOCK_BREAK_SPEED, "passive/eng_create_bonus");
        remove(player, Attributes.BLOCK_BREAK_SPEED, "passive/eng_create_mastery");
        remove(player, Attributes.MINING_EFFICIENCY, "passive/eng_tool_efficiency_eff");
        remove(player, Attributes.ENTITY_INTERACTION_RANGE, "passive/ranged_extra_range");
        remove(player, Attributes.ATTACK_DAMAGE, "passive/melee_low_hp_base");
        remove(player, Attributes.FALL_DAMAGE_MULTIPLIER, "passive/surv_fall_guard");
    }

    public static void apply(Player player, PlayerProgress progress) {
        if (progress.hasPassive("surv_d_max_hp")) {
            add(player, Attributes.MAX_HEALTH, "passive/surv_max_hp", 4.0, AttributeModifier.Operation.ADD_VALUE);
        }
        if (progress.hasPassive("surv_c_speed")) {
            add(player, Attributes.MOVEMENT_SPEED, "passive/surv_speed", 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        if (progress.hasPassive("surv_c_resistance")) {
            add(player, Attributes.ARMOR, "passive/surv_resistance", 2.0, AttributeModifier.Operation.ADD_VALUE);
        }
        if (progress.hasPassive("surv_b_fall_guard")) {
            add(player, Attributes.FALL_DAMAGE_MULTIPLIER, "passive/surv_fall_guard", -0.35, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        if (progress.hasPassive("eng_d_mine_speed")) {
            add(player, Attributes.BLOCK_BREAK_SPEED, "passive/eng_mine_speed", 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        if (progress.hasPassive("eng_d_tool_efficiency")) {
            add(player, Attributes.BLOCK_BREAK_SPEED, "passive/eng_tool_efficiency", 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            add(player, Attributes.MINING_EFFICIENCY, "passive/eng_tool_efficiency_eff", 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        if (progress.hasPassive("eng_b_create_bonus")) {
            double amount = CompatMods.isLoaded(CompatMods.CREATE) ? 0.18 : 0.10;
            add(player, Attributes.BLOCK_BREAK_SPEED, "passive/eng_create_bonus", amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        if (progress.hasPassive("eng_s_create_mastery")) {
            double amount = CompatMods.isLoaded(CompatMods.CREATE) ? 0.28 : 0.16;
            add(player, Attributes.BLOCK_BREAK_SPEED, "passive/eng_create_mastery", amount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }

        if (progress.hasPassive("ranged_c_extra_range")) {
            add(player, Attributes.ENTITY_INTERACTION_RANGE, "passive/ranged_extra_range", 1.0, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static float maxMana(PlayerProgress progress) {
        float max = PlayerProgress.BASE_MAX_MANA;
        if (progress.hasPassive("magic_d_max_mana")) {
            max += 20.0f;
        }
        if (progress.hasPassive("magic_a_mana_cost_down") || progress.hasPassive("magic_c_cdr")) {
            max += 5.0f;
        }
        if (CompatMods.isLoaded(CompatMods.IRONS_SPELLBOOKS)) {
            max += 10.0f;
        }
        return max;
    }

    public static float manaRegenPerSecond(PlayerProgress progress) {
        float regen = 0.35f;
        if (progress.hasPassive("magic_d_mana_regen")) {
            regen += 0.55f;
        }
        if (progress.hasPassive("magic_s_low_hp_regen")) {
            regen += 0.15f;
        }
        return regen;
    }

    private static void add(
            Player player,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            String path,
            double amount,
            AttributeModifier.Operation operation
    ) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, path);
            instance.addOrReplacePermanentModifier(new AttributeModifier(id, amount, operation));
        }
    }

    private static void remove(
            Player player,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            String path
    ) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, path));
        }
    }
}
