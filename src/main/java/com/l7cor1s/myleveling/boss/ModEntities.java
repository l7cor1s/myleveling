package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.MyLeveling;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MyLeveling.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ShadowScoutEntity>> SHADOW_SCOUT =
            ENTITIES.register("shadow_scout", () -> EntityType.Builder.of(ShadowScoutEntity::new, MobCategory.MONSTER)
                    .sized(0.7f, 1.95f).clientTrackingRange(10).build("shadow_scout"));

    public static final DeferredHolder<EntityType<?>, EntityType<RankWardenEntity>> RANK_WARDEN =
            ENTITIES.register("rank_warden", () -> EntityType.Builder.of(RankWardenEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 2.4f).clientTrackingRange(12).build("rank_warden"));

    public static final DeferredHolder<EntityType<?>, EntityType<GateMonarchEntity>> GATE_MONARCH =
            ENTITIES.register("gate_monarch", () -> EntityType.Builder.of(GateMonarchEntity::new, MobCategory.MONSTER)
                    .sized(1.1f, 2.8f).fireImmune().clientTrackingRange(16).build("gate_monarch"));

    private ModEntities() {
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
