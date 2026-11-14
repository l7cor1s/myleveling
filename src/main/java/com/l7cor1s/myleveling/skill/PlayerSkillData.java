package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.attachment.ModAttachments;
import com.l7cor1s.myleveling.network.ProgressSync;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PlayerSkillData {
    private PlayerSkillData() {
    }

    public static PlayerProgress get(Player player) {
        return player.getData(ModAttachments.PLAYER_PROGRESS);
    }

    public static void set(Player player, PlayerProgress progress) {
        player.setData(ModAttachments.PLAYER_PROGRESS, progress);
    }

    public static boolean hasPassive(Player player, String id) {
        return get(player).hasPassive(id);
    }

    public static Result buyMinor(ServerPlayer player, String upgradeId) {
        var upgradeOpt = MinorUpgrade.byId(upgradeId);
        if (upgradeOpt.isEmpty()) {
            return Result.UNKNOWN;
        }
        MinorUpgrade upgrade = upgradeOpt.get();
        PlayerProgress progress = get(player);
        int level = progress.minorLevel(upgradeId);
        if (level >= upgrade.maxLevel()) {
            return Result.MAXED;
        }
        if (progress.skillPoints() < upgrade.costPerLevel()) {
            return Result.NOT_ENOUGH_SKILL_POINTS;
        }

        Map<String, Integer> minors = new HashMap<>(progress.minorLevels());
        minors.put(upgradeId, level + 1);
        set(player, progress
                .withSkillPoints(progress.skillPoints() - upgrade.costPerLevel())
                .withMinors(minors));
        applyAttributeModifiers(player);
        ProgressSync.sendTo(player);
        return Result.SUCCESS;
    }

    public static Result selectPassive(ServerPlayer player, String passiveId) {
        var passiveOpt = SkillCatalog.passive(passiveId);
        if (passiveOpt.isEmpty()) {
            return Result.UNKNOWN;
        }
        PassiveSkill passive = passiveOpt.get();
        PlayerProgress progress = get(player);
        Rank rank = PlayerRankData.getRank(player);

        if (!rank.isAtLeast(passive.requiredRank())) {
            return Result.RANK_TOO_LOW;
        }
        if (progress.hasPassive(passiveId)) {
            return Result.ALREADY;
        }

        // Already chose the other option in this slot?
        if (progress.hasPassive(passive.pairId())) {
            return Result.SLOT_TAKEN;
        }
        for (PassiveSkill other : SkillCatalog.passivesFor(passive.branch(), passive.requiredRank())) {
            if (progress.hasPassive(other.id())) {
                return Result.SLOT_TAKEN;
            }
        }

        if (progress.passivePoints() < passive.passivePointCost()) {
            return Result.NOT_ENOUGH_PASSIVE_POINTS;
        }

        Set<String> selected = new HashSet<>(progress.selectedPassives());
        selected.add(passiveId);
        set(player, progress
                .withPassivePoints(progress.passivePoints() - passive.passivePointCost())
                .withPassives(selected));
        applyAttributeModifiers(player);
        ProgressSync.sendTo(player);
        player.sendSystemMessage(Component.translatable("message.myleveling.passive_selected", passive.getDisplayName()));
        return Result.SUCCESS;
    }

    public static Result unlockActive(ServerPlayer player, String activeId) {
        var activeOpt = SkillCatalog.active(activeId);
        if (activeOpt.isEmpty()) {
            return Result.UNKNOWN;
        }
        ActiveSkill active = activeOpt.get();
        PlayerProgress progress = get(player);
        Rank rank = PlayerRankData.getRank(player);

        if (!rank.isAtLeast(active.requiredRank())) {
            return Result.RANK_TOO_LOW;
        }
        if (progress.hasActiveUnlocked(activeId)) {
            return Result.ALREADY;
        }
        if (progress.passivePoints() < active.passivePointCost()) {
            return Result.NOT_ENOUGH_PASSIVE_POINTS;
        }

        Set<String> unlocked = new HashSet<>(progress.unlockedActives());
        unlocked.add(activeId);

        List<String> equipped = new ArrayList<>(progress.equippedActives());
        if (equipped.size() < PlayerProgress.MAX_EQUIPPED_ACTIVES) {
            equipped.add(activeId);
        }

        set(player, progress
                .withPassivePoints(progress.passivePoints() - active.passivePointCost())
                .withUnlockedActives(unlocked)
                .withEquipped(equipped));
        ProgressSync.sendTo(player);
        player.sendSystemMessage(Component.translatable("message.myleveling.active_unlocked", active.getDisplayName()));
        return Result.SUCCESS;
    }

    public static Result equipActive(ServerPlayer player, int slot, String activeId) {
        if (slot < 0 || slot >= PlayerProgress.MAX_EQUIPPED_ACTIVES) {
            return Result.UNKNOWN;
        }
        PlayerProgress progress = get(player);
        List<String> equipped = new ArrayList<>();
        for (int i = 0; i < PlayerProgress.MAX_EQUIPPED_ACTIVES; i++) {
            equipped.add(progress.equipped(i));
        }

        if (activeId == null || activeId.isEmpty()) {
            equipped.set(slot, "");
        } else {
            if (!progress.hasActiveUnlocked(activeId)) {
                return Result.UNKNOWN;
            }
            // Remove from other slot if present.
            for (int i = 0; i < equipped.size(); i++) {
                if (activeId.equals(equipped.get(i))) {
                    equipped.set(i, "");
                }
            }
            equipped.set(slot, activeId);
        }

        // Compact empties at end but keep slot positions meaningful: keep as-is with blanks trimmed from list end.
        while (!equipped.isEmpty() && equipped.get(equipped.size() - 1).isEmpty()) {
            equipped.remove(equipped.size() - 1);
        }
        set(player, progress.withEquipped(equipped));
        ProgressSync.sendTo(player);
        return Result.SUCCESS;
    }

    public static Result resetTree(ServerPlayer player) {
        PlayerProgress progress = get(player);
        int spent = spentPassivePoints(progress);
        if (spent <= 0 && progress.selectedPassives().isEmpty() && progress.unlockedActives().isEmpty()) {
            return Result.NOTHING_TO_RESET;
        }

        // Prefer gold cost; fall back to player levels.
        boolean paid = tryPayGold(player, 10);
        if (!paid) {
            if (player.experienceLevel < PlayerProgress.RESET_LEVEL_COST) {
                return Result.CANNOT_AFFORD_RESET;
            }
            player.giveExperienceLevels(-PlayerProgress.RESET_LEVEL_COST);
        }

        int refund = totalPassivePointsEarned(progress.xp());
        set(player, progress.resetPassivesAndActives(refund));
        applyAttributeModifiers(player);
        ProgressSync.sendTo(player);
        player.sendSystemMessage(Component.translatable("message.myleveling.reset_done", refund));
        return Result.SUCCESS;
    }

    public static int totalPassivePointsEarned(int xp) {
        Rank rank = Rank.fromXp(xp);
        int total = 0;
        for (Rank r : Rank.values()) {
            if (rank.isAtLeast(r)) {
                total += r.getPassivePointsOnReach();
            }
        }
        return total;
    }

    public static int spentPassivePoints(PlayerProgress progress) {
        int spent = 0;
        for (String id : progress.selectedPassives()) {
            spent += SkillCatalog.passive(id).map(PassiveSkill::passivePointCost).orElse(0);
        }
        for (String id : progress.unlockedActives()) {
            spent += SkillCatalog.active(id).map(ActiveSkill::passivePointCost).orElse(0);
        }
        return spent;
    }

    public static void applyAttributeModifiers(Player player) {
        // Clear known minor modifiers.
        for (MinorUpgrade upgrade : MinorUpgrade.all()) {
            AttributeInstance instance = player.getAttribute(upgrade.attribute());
            if (instance != null) {
                instance.removeModifier(upgrade.modifierId());
            }
        }
        PassiveAttributeEffects.clear(player);

        PlayerProgress progress = get(player);
        for (var entry : progress.minorLevels().entrySet()) {
            MinorUpgrade.byId(entry.getKey()).ifPresent(upgrade -> {
                if (entry.getValue() <= 0) {
                    return;
                }
                AttributeInstance instance = player.getAttribute(upgrade.attribute());
                if (instance != null) {
                    instance.addOrReplacePermanentModifier(upgrade.modifierForLevel(entry.getValue()));
                }
            });
        }
        PassiveAttributeEffects.apply(player, progress);
    }

    private static boolean tryPayGold(ServerPlayer player, int amount) {
        int available = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.GOLD_INGOT)) {
                available += stack.getCount();
            }
        }
        if (available < amount) {
            return false;
        }
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(Items.GOLD_INGOT)) {
                continue;
            }
            int take = Math.min(remaining, stack.getCount());
            stack.shrink(take);
            remaining -= take;
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }

    public enum Result {
        SUCCESS,
        UNKNOWN,
        MAXED,
        ALREADY,
        SLOT_TAKEN,
        RANK_TOO_LOW,
        NOT_ENOUGH_SKILL_POINTS,
        NOT_ENOUGH_PASSIVE_POINTS,
        NOTHING_TO_RESET,
        CANNOT_AFFORD_RESET
    }
}
