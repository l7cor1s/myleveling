package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.attachment.ModAttachments;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;

public final class PlayerSkillData {
    private PlayerSkillData() {
    }

    public static int getSkillPoints(Player player) {
        return player.getData(ModAttachments.PLAYER_PROGRESS).skillPoints();
    }

    public static Set<String> getUnlocked(Player player) {
        return player.getData(ModAttachments.PLAYER_PROGRESS).unlockedSkills();
    }

    public static boolean hasSkill(Player player, String skillId) {
        return player.getData(ModAttachments.PLAYER_PROGRESS).hasSkill(skillId);
    }

    public static UnlockResult tryUnlock(ServerPlayer player, Skill skill) {
        PlayerProgress progress = player.getData(ModAttachments.PLAYER_PROGRESS);

        if (progress.hasSkill(skill.id())) {
            return UnlockResult.ALREADY_UNLOCKED;
        }
        if (progress.skillPoints() < skill.cost()) {
            return UnlockResult.NOT_ENOUGH_POINTS;
        }

        Rank rank = PlayerRankData.getRank(player);
        if (rank.ordinal() < skill.minRank().ordinal()) {
            return UnlockResult.RANK_TOO_LOW;
        }

        if (skill.prerequisite().isPresent() && !progress.hasSkill(skill.prerequisite().get())) {
            return UnlockResult.MISSING_PREREQUISITE;
        }

        Set<String> unlocked = new HashSet<>(progress.unlockedSkills());
        unlocked.add(skill.id());

        PlayerProgress updated = progress
                .withSkillPoints(progress.skillPoints() - skill.cost())
                .withUnlocked(unlocked);
        player.setData(ModAttachments.PLAYER_PROGRESS, updated);

        applySkillModifiers(player);
        player.sendSystemMessage(Component.translatable(
                "message.myleveling.skill_unlocked",
                skill.getDisplayName()
        ));
        return UnlockResult.SUCCESS;
    }

    public static void applySkillModifiers(Player player) {
        // Clear previous MyLeveling skill modifiers, then re-apply unlocked ones.
        for (Skill skill : Skills.all()) {
            for (Skill.AttributeBonus bonus : skill.bonuses()) {
                AttributeInstance instance = player.getAttribute(bonus.attribute());
                if (instance != null) {
                    instance.removeModifier(bonus.modifierId());
                }
            }
        }

        PlayerProgress progress = player.getData(ModAttachments.PLAYER_PROGRESS);
        for (String skillId : progress.unlockedSkills()) {
            Skills.byId(skillId).ifPresent(skill -> {
                for (Skill.AttributeBonus bonus : skill.bonuses()) {
                    AttributeInstance instance = player.getAttribute(bonus.attribute());
                    if (instance != null) {
                        instance.addOrReplacePermanentModifier(bonus.toModifier());
                    }
                }
            });
        }
    }

    public enum UnlockResult {
        SUCCESS,
        ALREADY_UNLOCKED,
        NOT_ENOUGH_POINTS,
        RANK_TOO_LOW,
        MISSING_PREREQUISITE
    }
}
