package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.boss.BossAnimState;
import com.l7cor1s.myleveling.boss.BossId;
import com.l7cor1s.myleveling.boss.CatalogBossEntity;
import com.l7cor1s.myleveling.boss.GateMonarchEntity;
import com.l7cor1s.myleveling.boss.ModEntities;
import com.l7cor1s.myleveling.boss.RankWardenEntity;
import com.l7cor1s.myleveling.boss.ShadowMinionEntity;
import com.l7cor1s.myleveling.boss.ShadowScoutEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class BossRenderers {
    private BossRenderers() {
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Kv2TankModel.LAYER, Kv2TankModel::createBodyLayer);
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADOW_SCOUT.get(), ctx -> plain(ctx, "myleveling:textures/entity/shadow_scout.png", 0.5f));
        event.registerEntityRenderer(ModEntities.RANK_WARDEN.get(), ctx -> plain(ctx, "myleveling:textures/entity/rank_warden.png", 0.6f));
        event.registerEntityRenderer(ModEntities.GATE_MONARCH.get(), ctx -> plain(ctx, "myleveling:textures/entity/gate_monarch.png", 0.7f));
        event.registerEntityRenderer(ModEntities.SHADOW_MINION.get(), BossRenderers::minion);
        event.registerEntityRenderer(ModEntities.KV2.get(), Kv2TankRenderer::new);
        event.registerEntityRenderer(ModEntities.DYNAMITE_PROJECTILE.get(), ThrownItemRenderer::new);

        for (BossId id : BossId.values()) {
            event.registerEntityRenderer(typeFor(id), ctx -> catalog(ctx, id));
        }
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

    private static HumanoidMobRenderer<ShadowMinionEntity, HumanoidModel<ShadowMinionEntity>> minion(EntityRendererProvider.Context ctx) {
        return new HumanoidMobRenderer<>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.4f) {
            @Override
            public ResourceLocation getTextureLocation(ShadowMinionEntity entity) {
                return ResourceLocation.fromNamespaceAndPath("myleveling", "textures/entity/shadow_minion.png");
            }
        };
    }

    private static AnimatedBossRenderer catalog(EntityRendererProvider.Context ctx, BossId id) {
        return new AnimatedBossRenderer(ctx, id);
    }

    private static <T extends Mob> HumanoidMobRenderer<T, HumanoidModel<T>> plain(
            EntityRendererProvider.Context ctx, String texture, float shadow
    ) {
        ResourceLocation loc = ResourceLocation.parse(texture);
        return new HumanoidMobRenderer<>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), shadow) {
            @Override
            public ResourceLocation getTextureLocation(T entity) {
                return loc;
            }
        };
    }

    private static final class AnimatedBossRenderer
            extends HumanoidMobRenderer<CatalogBossEntity, HumanoidModel<CatalogBossEntity>> {
        private final BossId bossId;
        private final ResourceLocation texture;

        private AnimatedBossRenderer(EntityRendererProvider.Context ctx, BossId bossId) {
            super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), bossId.tier() == com.l7cor1s.myleveling.boss.BossTier.LARGE ? 0.8f : 0.55f);
            this.bossId = bossId;
            this.texture = ResourceLocation.fromNamespaceAndPath("myleveling", "textures/entity/" + bossId.id() + ".png");
        }

        @Override
        public ResourceLocation getTextureLocation(CatalogBossEntity entity) {
            return texture;
        }

        @Override
        protected void scale(CatalogBossEntity entity, PoseStack pose, float partialTick) {
            float base = switch (bossId.tier()) {
                case MINI -> 1.05f;
                case MID -> 1.25f;
                case LARGE -> 1.55f;
            };
            BossAnimState anim = entity.animState();
            float t = entity.tickCount + partialTick;
            float pulse = 1f;
            if (anim == BossAnimState.WINDUP) {
                pulse = 1f + 0.14f * Mth.sin(t * 1.1f);
                pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(12f + 6f * Mth.sin(t * 0.9f)));
            } else if (anim == BossAnimState.CAST) {
                pulse = 1f + 0.12f * Mth.sin(t * 1.4f);
                pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(Mth.sin(t * 0.7f) * 18f));
                pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-10f));
            } else if (anim == BossAnimState.DASH) {
                pose.scale(1.35f, 0.78f, 1.35f);
                pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(25f));
                return;
            } else if (anim == BossAnimState.STRIKE) {
                pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-32f));
                pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(Mth.sin(t) * 10f));
                pulse = 1.12f;
            } else if (anim == BossAnimState.SUMMON) {
                pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(t * 14f));
                pulse = 1f + 0.1f * Mth.sin(t * 0.5f);
            }
            pose.scale(base * pulse, base * pulse, base * pulse);
        }
    }
}
