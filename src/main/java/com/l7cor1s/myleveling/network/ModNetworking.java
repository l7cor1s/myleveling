package com.l7cor1s.myleveling.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetworking {
    private ModNetworking() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("2");

        registrar.playToClient(
                SyncProgressPayload.TYPE,
                SyncProgressPayload.STREAM_CODEC,
                SyncProgressPayload::handleClient
        );
        registrar.playToServer(
                RequestSyncPayload.TYPE,
                RequestSyncPayload.STREAM_CODEC,
                RequestSyncPayload::handleServer
        );
        registrar.playToServer(
                SkillActionPayload.TYPE,
                SkillActionPayload.STREAM_CODEC,
                SkillActionPayload::handleServer
        );
    }
}
