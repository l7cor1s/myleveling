package com.l7cor1s.myleveling.event;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Re-applies skill attribute modifiers after login/respawn.
 * Uses unique modifier IDs so Epic Fight / Apotheosis attributes stack cleanly.
 */
@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class SkillSyncHandler {
    private SkillSyncHandler() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerSkillData.applySkillModifiers(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerSkillData.applySkillModifiers(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        // Attachment copyOnDeath already restores progress; re-apply attrs on the new player.
        if (!event.getEntity().level().isClientSide()) {
            PlayerSkillData.applySkillModifiers(event.getEntity());
        }
    }
}
