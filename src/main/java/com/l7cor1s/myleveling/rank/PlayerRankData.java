package com.l7cor1s.myleveling.rank;

import com.l7cor1s.myleveling.attachment.ModAttachments;
import com.l7cor1s.myleveling.network.ProgressSync;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class PlayerRankData {
    private PlayerRankData() {
    }

    public static PlayerProgress getProgress(Player player) {
        return player.getData(ModAttachments.PLAYER_PROGRESS);
    }

    public static int getXp(Player player) {
        return getProgress(player).xp();
    }

    public static Rank getRank(Player player) {
        return Rank.fromXp(getXp(player));
    }

    public static void setProgress(Player player, PlayerProgress progress) {
        player.setData(ModAttachments.PLAYER_PROGRESS, progress);
    }

    /**
     * Adds combat XP, grants Skill Points every 400 XP, Passive Points on rank-up, and syncs.
     */
    public static int addXp(ServerPlayer player, int amount) {
        if (amount <= 0) {
            return 0;
        }

        PlayerProgress progress = getProgress(player);
        Rank previous = Rank.fromXp(progress.xp());
        int oldXp = progress.xp();
        int newXp = oldXp + amount;

        int gainedSkillPoints = (newXp / PlayerProgress.SKILL_POINT_XP_INTERVAL)
                - (oldXp / PlayerProgress.SKILL_POINT_XP_INTERVAL);

        PlayerProgress updated = progress.withXp(newXp).addSkillPoints(gainedSkillPoints);

        Rank current = Rank.fromXp(newXp);
        if (current.ordinal() > previous.ordinal()) {
            for (int i = previous.ordinal() + 1; i <= current.ordinal(); i++) {
                Rank unlocked = Rank.values()[i];
                updated = updated.addPassivePoints(unlocked.getPassivePointsOnReach());
                player.sendSystemMessage(Component.translatable(
                        "message.myleveling.rank_up",
                        unlocked.getDisplayName()
                ));
                if (unlocked.getPassivePointsOnReach() > 0) {
                    player.sendSystemMessage(Component.translatable(
                            "message.myleveling.passive_points_gained",
                            unlocked.getPassivePointsOnReach()
                    ));
                }
            }
        }

        if (gainedSkillPoints > 0) {
            player.sendSystemMessage(Component.translatable(
                    "message.myleveling.skill_points_gained",
                    gainedSkillPoints
            ));
        }

        setProgress(player, updated);
        ProgressSync.sendTo(player);
        return amount;
    }
}
