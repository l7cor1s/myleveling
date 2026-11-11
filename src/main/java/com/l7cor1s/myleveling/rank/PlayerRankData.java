package com.l7cor1s.myleveling.rank;

import com.l7cor1s.myleveling.attachment.ModAttachments;
import com.l7cor1s.myleveling.network.ProgressSync;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Helpers for reading and mutating a player's stored XP / derived rank / skill points.
 */
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
     * Adds XP on the server, promotes rank if thresholds are crossed,
     * grants skill points, and notifies the player in chat for each rank-up.
     *
     * @return XP actually awarded
     */
    public static int addXp(ServerPlayer player, int amount) {
        if (amount <= 0) {
            return 0;
        }

        PlayerProgress progress = getProgress(player);
        Rank previous = Rank.fromXp(progress.xp());
        int newXp = progress.xp() + amount;
        PlayerProgress updated = progress.withXp(newXp);

        Rank current = Rank.fromXp(newXp);
        if (current.ordinal() > previous.ordinal()) {
            for (int i = previous.ordinal() + 1; i <= current.ordinal(); i++) {
                Rank unlocked = Rank.values()[i];
                updated = updated.addSkillPoints(unlocked.getSkillPointsOnReach());
                player.sendSystemMessage(Component.translatable(
                        "message.myleveling.rank_up",
                        unlocked.getDisplayName()
                ));
                if (unlocked.getSkillPointsOnReach() > 0) {
                    player.sendSystemMessage(Component.translatable(
                            "message.myleveling.skill_points_gained",
                            unlocked.getSkillPointsOnReach()
                    ));
                }
            }
        }

        setProgress(player, updated);
        ProgressSync.sendTo(player);
        return amount;
    }
}
