package com.l7cor1s.myleveling.network;

import com.l7cor1s.myleveling.rank.PlayerRankData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ProgressSync {
    private ProgressSync() {
    }

    public static void sendTo(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SyncProgressPayload(PlayerRankData.getProgress(player)));
    }
}
