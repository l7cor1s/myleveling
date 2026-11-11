package com.l7cor1s.myleveling.network;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.client.ClientProgressCache;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.List;

public record SyncProgressPayload(int xp, int skillPoints, List<String> unlockedSkills) implements CustomPacketPayload {
    public static final Type<SyncProgressPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "sync_progress")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncProgressPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncProgressPayload::xp,
            ByteBufCodecs.VAR_INT, SyncProgressPayload::skillPoints,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SyncProgressPayload::unlockedSkills,
            SyncProgressPayload::new
    );

    public static SyncProgressPayload from(PlayerProgress progress) {
        return new SyncProgressPayload(
                progress.xp(),
                progress.skillPoints(),
                List.copyOf(progress.unlockedSkills())
        );
    }

    public PlayerProgress toProgress() {
        return new PlayerProgress(xp, skillPoints, new HashSet<>(unlockedSkills));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(SyncProgressPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientProgressCache.set(payload.toProgress()));
    }
}
