package com.l7cor1s.myleveling.network;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.client.ClientProgressCache;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncProgressPayload(PlayerProgress progress) implements CustomPacketPayload {
    public static final Type<SyncProgressPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "sync_progress")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncProgressPayload> STREAM_CODEC = StreamCodec.composite(
            PlayerProgress.STREAM_CODEC, SyncProgressPayload::progress,
            SyncProgressPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(SyncProgressPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientProgressCache.set(payload.progress()));
    }
}
