package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.MyLeveling;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@Mod(value = MyLeveling.MOD_ID, dist = Dist.CLIENT)
public class MyLevelingClient {
    public MyLevelingClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterKeys);
        modEventBus.addListener(this::onRegisterGuiLayers);
    }

    private void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeyMappings.OPEN_SKILL_TREE);
        event.register(ModKeyMappings.ACTIVE_SLOT_1);
        event.register(ModKeyMappings.ACTIVE_SLOT_2);
    }

    private void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "active_skills"),
                ActiveSkillHud::render
        );
    }
}
