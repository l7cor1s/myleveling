package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.boss.ModEntities;
import com.l7cor1s.myleveling.client.geo.Kv2GeoRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.CaveSpiderRenderer;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.RavagerRenderer;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Vanilla renderers for ranked reskins — only KV-2 uses a custom GeckoLib model.
 */
public final class BossRenderers {
    private BossRenderers() {
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADOW_SCOUT.get(), VindicatorRenderer::new);
        event.registerEntityRenderer(ModEntities.RANK_WARDEN.get(), RavagerRenderer::new);
        event.registerEntityRenderer(ModEntities.GATE_MONARCH.get(), WitherSkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.SHADOW_MINION.get(), ZombieRenderer::new);

        event.registerEntityRenderer(ModEntities.GROVE_STALKER.get(), ZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.DUNE_SCORPION.get(), CaveSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.FROST_HOWLER.get(), StrayRenderer::new);
        event.registerEntityRenderer(ModEntities.BOG_WITCH.get(), WitchRenderer::new);
        event.registerEntityRenderer(ModEntities.REEF_LEVIATHAN.get(), ElderGuardianRenderer::new);
        event.registerEntityRenderer(ModEntities.ASH_IMP_LORD.get(), BlazeRenderer::new);
        event.registerEntityRenderer(ModEntities.THUNDER_COLOSSUS.get(), RavagerRenderer::new);
        event.registerEntityRenderer(ModEntities.CRYSTAL_ORACLE.get(), EvokerRenderer::new);
        event.registerEntityRenderer(ModEntities.CRIMSON_SUMMONER.get(),
                ctx -> new PiglinRenderer(ctx, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE_INNER_ARMOR,
                        ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, false));
        event.registerEntityRenderer(ModEntities.ABYSS_KNIGHT.get(), DrownedRenderer::new);
        event.registerEntityRenderer(ModEntities.SANDSTORM_EMPRESS.get(), HuskRenderer::new);
        event.registerEntityRenderer(ModEntities.VOID_REAPER.get(), EndermanRenderer::new);
        event.registerEntityRenderer(ModEntities.INFERNAL_BEHEMOTH.get(), RavagerRenderer::new);
        event.registerEntityRenderer(ModEntities.GLACIAL_SOVEREIGN.get(), EvokerRenderer::new);

        event.registerEntityRenderer(ModEntities.KV2.get(), Kv2GeoRenderer::new);
        event.registerEntityRenderer(ModEntities.DYNAMITE_PROJECTILE.get(), ThrownItemRenderer::new);
    }
}
