package com.l7cor1s.myleveling.quest;

import com.l7cor1s.myleveling.compat.CompatMods;
import com.l7cor1s.myleveling.network.ProgressSync;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PlayerQuestData {
    private PlayerQuestData() {
    }

    public static PlayerSkillData.Result accept(ServerPlayer player, String questId) {
        var defOpt = QuestCatalog.byId(questId);
        if (defOpt.isEmpty()) {
            return PlayerSkillData.Result.UNKNOWN;
        }
        QuestDefinition def = defOpt.get();
        if (def.requiresMca() && !CompatMods.isLoaded(CompatMods.MCA)) {
            return PlayerSkillData.Result.UNKNOWN;
        }

        PlayerProgress progress = PlayerSkillData.get(player);
        progress = refreshDailies(player, progress);

        if (progress.activeQuests().contains(questId)) {
            return PlayerSkillData.Result.ALREADY;
        }
        if (progress.completedQuests().contains(questId) && def.kind() != QuestKind.DAILY && def.kind() != QuestKind.REPEATABLE) {
            return PlayerSkillData.Result.ALREADY;
        }
        if (def.prerequisiteQuestId().isPresent() && !progress.completedQuests().contains(def.prerequisiteQuestId().get())) {
            return PlayerSkillData.Result.RANK_TOO_LOW;
        }
        if (progress.activeQuests().size() >= 5) {
            player.displayClientMessage(Component.translatable("message.myleveling.quest_limit"), true);
            return PlayerSkillData.Result.MAXED;
        }

        Set<String> active = new HashSet<>(progress.activeQuests());
        active.add(questId);
        Map<String, Integer> qProg = new HashMap<>(progress.questProgress());
        qProg.put(questId, 0);
        PlayerSkillData.set(player, progress.withQuests(active, progress.completedQuests(), qProg, progress.dailyQuestDay()));
        ProgressSync.sendTo(player);
        player.sendSystemMessage(Component.translatable("message.myleveling.quest_accepted", def.title()));
        return PlayerSkillData.Result.SUCCESS;
    }

    public static PlayerSkillData.Result turnIn(ServerPlayer player, String questId) {
        var defOpt = QuestCatalog.byId(questId);
        if (defOpt.isEmpty()) {
            return PlayerSkillData.Result.UNKNOWN;
        }
        QuestDefinition def = defOpt.get();
        PlayerProgress progress = PlayerSkillData.get(player);
        if (!progress.activeQuests().contains(questId)) {
            return PlayerSkillData.Result.UNKNOWN;
        }
        if (progress.questCount(questId) < def.targetCount()) {
            player.displayClientMessage(Component.translatable("message.myleveling.quest_incomplete"), true);
            return PlayerSkillData.Result.NOT_ENOUGH_SKILL_POINTS;
        }

        Set<String> active = new HashSet<>(progress.activeQuests());
        active.remove(questId);
        Set<String> completed = new HashSet<>(progress.completedQuests());
        if (def.kind() != QuestKind.DAILY && def.kind() != QuestKind.REPEATABLE) {
            completed.add(questId);
        }
        Map<String, Integer> qProg = new HashMap<>(progress.questProgress());
        qProg.remove(questId);

        PlayerProgress updated = progress.withQuests(active, completed, qProg, progress.dailyQuestDay())
                .addPassivePoints(def.rewardPassivePoints());
        PlayerSkillData.set(player, updated);
        if (def.rewardXp() > 0) {
            PlayerRankData.addXp(player, def.rewardXp());
        } else {
            ProgressSync.sendTo(player);
        }
        for (var supplier : def.rewardItems()) {
            ItemStack stack = supplier.get();
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
        player.sendSystemMessage(Component.translatable("message.myleveling.quest_complete", def.title()));
        return PlayerSkillData.Result.SUCCESS;
    }

    public static void onMobKilled(ServerPlayer player, LivingEntity victim) {
        PlayerProgress progress = refreshDailies(player, PlayerSkillData.get(player));
        ResourceLocation typeId = EntityType.getKey(victim.getType());
        boolean changed = false;
        Map<String, Integer> qProg = new HashMap<>(progress.questProgress());
        for (String questId : progress.activeQuests()) {
            QuestDefinition def = QuestCatalog.byId(questId).orElse(null);
            if (def == null || def.targetEntity().isEmpty()) {
                continue;
            }
            if (!def.targetEntity().get().equals(typeId)) {
                continue;
            }
            int next = Math.min(def.targetCount(), qProg.getOrDefault(questId, 0) + 1);
            qProg.put(questId, next);
            changed = true;
        }
        if (changed) {
            PlayerSkillData.set(player, progress.withQuests(progress.activeQuests(), progress.completedQuests(), qProg, progress.dailyQuestDay()));
            ProgressSync.sendTo(player);
        }
    }

    public static void onBossKilled(ServerPlayer player, ResourceLocation bossId) {
        // Reuse mob kill path with synthetic matching.
        PlayerProgress progress = PlayerSkillData.get(player);
        Map<String, Integer> qProg = new HashMap<>(progress.questProgress());
        boolean changed = false;
        for (String questId : progress.activeQuests()) {
            QuestDefinition def = QuestCatalog.byId(questId).orElse(null);
            if (def == null || def.targetEntity().isEmpty()) {
                continue;
            }
            if (!def.targetEntity().get().equals(bossId)) {
                continue;
            }
            qProg.put(questId, def.targetCount());
            changed = true;
        }
        if (changed) {
            PlayerSkillData.set(player, progress.withQuests(progress.activeQuests(), progress.completedQuests(), qProg, progress.dailyQuestDay()));
            ProgressSync.sendTo(player);
        }
    }

    public static void onBlockMined(ServerPlayer player, ResourceLocation blockId) {
        PlayerProgress progress = refreshDailies(player, PlayerSkillData.get(player));
        Map<String, Integer> qProg = new HashMap<>(progress.questProgress());
        boolean changed = false;
        for (String questId : progress.activeQuests()) {
            QuestDefinition def = QuestCatalog.byId(questId).orElse(null);
            if (def == null || def.targetItem().isEmpty()) {
                continue;
            }
            if (!def.targetItem().get().equals(blockId)) {
                continue;
            }
            int next = Math.min(def.targetCount(), qProg.getOrDefault(questId, 0) + 1);
            qProg.put(questId, next);
            changed = true;
        }
        if (changed) {
            PlayerSkillData.set(player, progress.withQuests(progress.activeQuests(), progress.completedQuests(), qProg, progress.dailyQuestDay()));
            ProgressSync.sendTo(player);
        }
    }

    public static void onVillagerInteract(ServerPlayer player) {
        PlayerProgress progress = PlayerSkillData.get(player);
        Map<String, Integer> qProg = new HashMap<>(progress.questProgress());
        boolean changed = false;
        for (String questId : progress.activeQuests()) {
            QuestDefinition def = QuestCatalog.byId(questId).orElse(null);
            if (def == null || def.kind() != QuestKind.VILLAGER) {
                continue;
            }
            int next = Math.min(def.targetCount(), qProg.getOrDefault(questId, 0) + 1);
            qProg.put(questId, next);
            changed = true;
        }
        if (changed) {
            PlayerSkillData.set(player, progress.withQuests(progress.activeQuests(), progress.completedQuests(), qProg, progress.dailyQuestDay()));
            ProgressSync.sendTo(player);
        }
    }

    public static PlayerProgress refreshDailies(ServerPlayer player, PlayerProgress progress) {
        long day = player.level().getDayTime() / 24000L;
        if (progress.dailyQuestDay() == day) {
            return progress;
        }
        Set<String> active = new HashSet<>(progress.activeQuests());
        Map<String, Integer> qProg = new HashMap<>(progress.questProgress());
        for (QuestDefinition def : QuestCatalog.all()) {
            if (def.kind() == QuestKind.DAILY) {
                active.remove(def.id());
                qProg.remove(def.id());
            }
        }
        PlayerProgress updated = progress.withQuests(active, progress.completedQuests(), qProg, day);
        PlayerSkillData.set(player, updated);
        return updated;
    }
}
