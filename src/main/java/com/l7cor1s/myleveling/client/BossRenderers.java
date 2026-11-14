package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.boss.BossId;
import com.l7cor1s.myleveling.boss.BossTier;
import com.l7cor1s.myleveling.boss.CatalogBossEntity;
import com.l7cor1s.myleveling.boss.ModEntities;
import com.l7cor1s.myleveling.client.geo.BossGeoRenderer;
import com.l7cor1s.myleveling.client.geo.Kv2GeoRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class BossRenderers {
    private BossRenderers() {
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADOW_SCOUT.get(),
                ctx -> new BossGeoRenderer<>(ctx, "shadow_scout", 0.5f, 1.05f));
        event.registerEntityRenderer(ModEntities.RANK_WARDEN.get(),
                ctx -> new BossGeoRenderer<>(ctx, "rank_warden", 0.6f, 1.2f));
        event.registerEntityRenderer(ModEntities.GATE_MONARCH.get(),
                ctx -> new BossGeoRenderer<>(ctx, "gate_monarch", 0.75f, 1.45f));
        event.registerEntityRenderer(ModEntities.SHADOW_MINION.get(),
                ctx -> new BossGeoRenderer<>(ctx, "shadow_minion", 0.4f, 0.85f));
        event.registerEntityRenderer(ModEntities.KV2.get(), Kv2GeoRenderer::new);
        event.registerEntityRenderer(ModEntities.DYNAMITE_PROJECTILE.get(), ThrownItemRenderer::new);

        for (BossId id : BossId.values()) {
            float scale = switch (id.tier()) {
                case MINI -> 1.1f;
                case MID -> 1.3f;
                case LARGE -> 1.55f;
            };
            float shadow = id.tier() == BossTier.LARGE ? 0.85f : 0.55f;
            event.registerEntityRenderer(typeFor(id),
                    ctx -> new BossGeoRenderer<CatalogBossEntity>(ctx, id.id(), shadow, scale));
        }
    }

    private static EntityType<CatalogBossEntity> typeFor(BossId id) {
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
