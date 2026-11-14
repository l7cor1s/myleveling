package com.l7cor1s.myleveling.network;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.effect.ActiveSkillExecutor;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → server skill actions.
 * action: 0 buy minor, 1 select passive, 2 unlock active, 3 equip active, 4 activate slot, 5 reset
 */
public record SkillActionPayload(int action, String id, int slot) implements CustomPacketPayload {
    public static final int BUY_MINOR = 0;
    public static final int SELECT_PASSIVE = 1;
    public static final int UNLOCK_ACTIVE = 2;
    public static final int EQUIP_ACTIVE = 3;
    public static final int ACTIVATE_SLOT = 4;
    public static final int RESET = 5;

    public static final Type<SkillActionPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "skill_action")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SkillActionPayload::action,
            ByteBufCodecs.STRING_UTF8, SkillActionPayload::id,
            ByteBufCodecs.VAR_INT, SkillActionPayload::slot,
            SkillActionPayload::new
    );

    public static SkillActionPayload buyMinor(String id) {
        return new SkillActionPayload(BUY_MINOR, id, 0);
    }

    public static SkillActionPayload selectPassive(String id) {
        return new SkillActionPayload(SELECT_PASSIVE, id, 0);
    }

    public static SkillActionPayload unlockActive(String id) {
        return new SkillActionPayload(UNLOCK_ACTIVE, id, 0);
    }

    public static SkillActionPayload equipActive(int slot, String id) {
        return new SkillActionPayload(EQUIP_ACTIVE, id == null ? "" : id, slot);
    }

    public static SkillActionPayload activateSlot(int slot) {
        return new SkillActionPayload(ACTIVATE_SLOT, "", slot);
    }

    public static SkillActionPayload reset() {
        return new SkillActionPayload(RESET, "", 0);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleServer(SkillActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            switch (payload.action()) {
                case BUY_MINOR -> PlayerSkillData.buyMinor(player, payload.id());
                case SELECT_PASSIVE -> PlayerSkillData.selectPassive(player, payload.id());
                case UNLOCK_ACTIVE -> PlayerSkillData.unlockActive(player, payload.id());
                case EQUIP_ACTIVE -> PlayerSkillData.equipActive(player, payload.slot(), payload.id());
                case ACTIVATE_SLOT -> ActiveSkillExecutor.activateSlot(player, payload.slot());
                case RESET -> PlayerSkillData.resetTree(player);
                default -> {
                }
            }
        });
    }
}
