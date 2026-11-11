package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.MyLeveling;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = MyLeveling.MOD_ID, dist = Dist.CLIENT)
public class MyLevelingClient {
    public MyLevelingClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterKeys);
    }

    private void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeyMappings.OPEN_SKILL_TREE);
    }
}
