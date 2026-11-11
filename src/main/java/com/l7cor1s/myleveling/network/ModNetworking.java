package com.l7cor1s.myleveling.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private ModNetworking() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                SyncProgressPayload.TYPE,
                SyncProgressPayload.STREAM_CODEC,
                SyncProgressPayload::handleClient
        );
        registrar.playToServer(
                UnlockSkillPayload.TYPE,
                UnlockSkillPayload.STREAM_CODEC,
                UnlockSkillPayload::handleServer
        );
        registrar.playToServer(
                RequestSyncPayload.TYPE,
                RequestSyncPayload.STREAM_CODEC,
                RequestSyncPayload::handleServer
        );
    }
}
