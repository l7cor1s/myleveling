package com.l7cor1s.myleveling.rank;

import com.l7cor1s.myleveling.attachment.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Helpers for reading and mutating a player's stored XP / derived rank.
 */
public final class PlayerRankData {
    private PlayerRankData() {
    }

    public static int getXp(Player player) {
        return player.getData(ModAttachments.PLAYER_XP);
    }

    public static Rank getRank(Player player) {
        return Rank.fromXp(getXp(player));
    }

    public static void setXp(Player player, int xp) {
        player.setData(ModAttachments.PLAYER_XP, Math.max(0, xp));
    }

    /**
     * Adds XP on the server, promotes rank if thresholds are crossed,
     * and notifies the player in chat for each rank-up.
     *
     * @return XP actually awarded
     */
    public static int addXp(ServerPlayer player, int amount) {
        if (amount <= 0) {
            return 0;
        }

        Rank previous = getRank(player);
        int newXp = getXp(player) + amount;
        setXp(player, newXp);

        Rank current = Rank.fromXp(newXp);
        if (current.ordinal() > previous.ordinal()) {
            for (int i = previous.ordinal() + 1; i <= current.ordinal(); i++) {
                Rank unlocked = Rank.values()[i];
                player.sendSystemMessage(Component.translatable(
                        "message.myleveling.rank_up",
                        unlocked.getDisplayName()
                ));
            }
        }

        return amount;
    }
}
