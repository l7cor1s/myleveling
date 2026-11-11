package com.l7cor1s.myleveling.network;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import com.l7cor1s.myleveling.skill.Skills;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UnlockSkillPayload(String skillId) implements CustomPacketPayload {
    public static final Type<UnlockSkillPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "unlock_skill")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockSkillPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UnlockSkillPayload::skillId,
            UnlockSkillPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleServer(UnlockSkillPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            Skills.byId(payload.skillId()).ifPresent(skill -> PlayerSkillData.tryUnlock(player, skill));
        });
    }
}
