package com.l7cor1s.myleveling.boss;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public final class BossEvents {
    private BossEvents() {
    }

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SHADOW_SCOUT.get(), ShadowScoutEntity.createAttributes().build());
        event.put(ModEntities.RANK_WARDEN.get(), RankWardenEntity.createAttributes().build());
        event.put(ModEntities.GATE_MONARCH.get(), GateMonarchEntity.createAttributes().build());
    }
}
