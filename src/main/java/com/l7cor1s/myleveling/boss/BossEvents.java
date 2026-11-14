package com.l7cor1s.myleveling.boss;

import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public final class BossEvents {
    private BossEvents() {
    }

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SHADOW_SCOUT.get(), ShadowScoutEntity.createAttributes().build());
        event.put(ModEntities.RANK_WARDEN.get(), RankWardenEntity.createAttributes().build());
        event.put(ModEntities.GATE_MONARCH.get(), GateMonarchEntity.createAttributes().build());
        event.put(ModEntities.SHADOW_MINION.get(), ShadowMinionEntity.createAttributes().build());
        event.put(ModEntities.KV2.get(), Kv2BossEntity.createAttributes().build());

        for (BossId id : BossId.values()) {
            event.put(typeFor(id), AbstractRankedBoss.attributesFor(id).build());
        }
    }

    @SubscribeEvent
    public static void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        for (BossId id : BossId.values()) {
            event.register(typeFor(id), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        }
        event.register(ModEntities.SHADOW_SCOUT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(ModEntities.RANK_WARDEN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
    }

    private static net.minecraft.world.entity.EntityType<CatalogBossEntity> typeFor(BossId id) {
        return switch (id) {
            case GROVE_STALKER -> ModEntities.GROVE_STALKER.get();
            case DUNE_SCORPION -> ModEntities.DUNE_SCORPION.get();
            case FROST_HOWLER -> ModEntities.FROST_HOWLER.get();
            case BOG_WITCH -> ModEntities.BOG_WITCH.get();
            case REEF_LEVIATHAN -> ModEntities.REEF_LEVIATHAN.get();
            case ASH_IMP_LORD -> ModEntities.ASH_IMP_LORD.get();
            case THUNDER_COLOSSUS -> ModEntities.THUNDER_COLOSSUS.get();
            case CRYSTAL_ORACLE -> ModEntities.CRYSTAL_ORACLE.get();
            case CRIMSON_SUMMONER -> ModEntities.CRIMSON_SUMMONER.get();
            case ABYSS_KNIGHT -> ModEntities.ABYSS_KNIGHT.get();
            case SANDSTORM_EMPRESS -> ModEntities.SANDSTORM_EMPRESS.get();
            case VOID_REAPER -> ModEntities.VOID_REAPER.get();
            case INFERNAL_BEHEMOTH -> ModEntities.INFERNAL_BEHEMOTH.get();
            case GLACIAL_SOVEREIGN -> ModEntities.GLACIAL_SOVEREIGN.get();
        };
    }
}
