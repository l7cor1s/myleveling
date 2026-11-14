package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.boss.GateMonarchEntity;
import com.l7cor1s.myleveling.boss.ModEntities;
import com.l7cor1s.myleveling.boss.RankWardenEntity;
import com.l7cor1s.myleveling.boss.ShadowScoutEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class BossRenderers {
    private BossRenderers() {
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADOW_SCOUT.get(), BossRenderers::scout);
        event.registerEntityRenderer(ModEntities.RANK_WARDEN.get(), BossRenderers::warden);
        event.registerEntityRenderer(ModEntities.GATE_MONARCH.get(), BossRenderers::monarch);
    }

    private static HumanoidMobRenderer<ShadowScoutEntity, HumanoidModel<ShadowScoutEntity>> scout(EntityRendererProvider.Context ctx) {
        return new HumanoidMobRenderer<>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f) {
            @Override
            public ResourceLocation getTextureLocation(ShadowScoutEntity entity) {
                return ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
            }
        };
    }

    private static HumanoidMobRenderer<RankWardenEntity, HumanoidModel<RankWardenEntity>> warden(EntityRendererProvider.Context ctx) {
        return new HumanoidMobRenderer<>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.6f) {
            @Override
            public ResourceLocation getTextureLocation(RankWardenEntity entity) {
                return ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");
            }
        };
    }

    private static HumanoidMobRenderer<GateMonarchEntity, HumanoidModel<GateMonarchEntity>> monarch(EntityRendererProvider.Context ctx) {
        return new HumanoidMobRenderer<>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.7f) {
            @Override
            public ResourceLocation getTextureLocation(GateMonarchEntity entity) {
                return ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
            }
        };
    }
}
