package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.network.RequestSyncPayload;
import com.l7cor1s.myleveling.network.SkillActionPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MyLeveling.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        while (ModKeyMappings.OPEN_SKILL_TREE.consumeClick()) {
            if (mc.screen instanceof SkillTreeScreen) {
                mc.setScreen(null);
            } else if (mc.screen == null) {
                PacketDistributor.sendToServer(RequestSyncPayload.INSTANCE);
                mc.setScreen(new SkillTreeScreen());
            }
        }

        if (mc.screen == null) {
            while (ModKeyMappings.ACTIVE_SLOT_1.consumeClick()) {
                PacketDistributor.sendToServer(SkillActionPayload.activateSlot(0));
            }
            while (ModKeyMappings.ACTIVE_SLOT_2.consumeClick()) {
                PacketDistributor.sendToServer(SkillActionPayload.activateSlot(1));
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientProgressCache.clear();
    }
}
