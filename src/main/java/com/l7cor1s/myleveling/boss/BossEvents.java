package com.l7cor1s.myleveling.boss;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public final class BossEvents {
    private BossEvents() {
    }

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SHADOW_SCOUT.get(), VindicatorRankBoss.createAttributes(BossId.SHADOW_SCOUT).build());
        event.put(ModEntities.RANK_WARDEN.get(), RavagerRankBoss.createAttributes(BossId.RANK_WARDEN).build());
        event.put(ModEntities.GATE_MONARCH.get(), WitherSkeletonRankBoss.createAttributes(BossId.GATE_MONARCH).build());
        event.put(ModEntities.SHADOW_MINION.get(), ShadowMinionEntity.createAttributes().build());
        event.put(ModEntities.KV2.get(), Kv2BossEntity.createAttributes().build());

        event.put(ModEntities.GROVE_STALKER.get(), ZombieRankBoss.createAttributes(BossId.GROVE_STALKER).build());
        event.put(ModEntities.DUNE_SCORPION.get(), CaveSpiderRankBoss.createAttributes(BossId.DUNE_SCORPION).build());
        event.put(ModEntities.FROST_HOWLER.get(), StrayRankBoss.createAttributes(BossId.FROST_HOWLER).build());
        event.put(ModEntities.BOG_WITCH.get(), WitchRankBoss.createAttributes(BossId.BOG_WITCH).build());
        event.put(ModEntities.REEF_LEVIATHAN.get(), ElderGuardianRankBoss.createAttributes(BossId.REEF_LEVIATHAN).build());
        event.put(ModEntities.ASH_IMP_LORD.get(), BlazeRankBoss.createAttributes(BossId.ASH_IMP_LORD).build());
        event.put(ModEntities.THUNDER_COLOSSUS.get(), RavagerRankBoss.createAttributes(BossId.THUNDER_COLOSSUS).build());
        event.put(ModEntities.CRYSTAL_ORACLE.get(), EvokerRankBoss.createAttributes(BossId.CRYSTAL_ORACLE).build());
        event.put(ModEntities.CRIMSON_SUMMONER.get(), PiglinBruteRankBoss.createAttributes(BossId.CRIMSON_SUMMONER).build());
        event.put(ModEntities.ABYSS_KNIGHT.get(), DrownedRankBoss.createAttributes(BossId.ABYSS_KNIGHT).build());
        event.put(ModEntities.SANDSTORM_EMPRESS.get(), HuskRankBoss.createAttributes(BossId.SANDSTORM_EMPRESS).build());
        event.put(ModEntities.VOID_REAPER.get(), EndermanRankBoss.createAttributes(BossId.VOID_REAPER).build());
        event.put(ModEntities.INFERNAL_BEHEMOTH.get(), RavagerRankBoss.createAttributes(BossId.INFERNAL_BEHEMOTH).build());
        event.put(ModEntities.GLACIAL_SOVEREIGN.get(), EvokerRankBoss.createAttributes(BossId.GLACIAL_SOVEREIGN).build());
    }
}
