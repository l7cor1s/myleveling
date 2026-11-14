package com.l7cor1s.myleveling.network;

import com.l7cor1s.myleveling.MyLeveling;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestSyncPayload() implements CustomPacketPayload {
    public static final Type<RequestSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "request_sync")
    );

    public static final RequestSyncPayload INSTANCE = new RequestSyncPayload();

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestSyncPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleServer(RequestSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ProgressSync.sendTo(player);
            }
        });
    }
}
