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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class BossRenderers {
    private BossRenderers() {
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADOW_SCOUT.get(), BossRenderers::scout);
        event.registerEntityRenderer(ModEntities.RANK_WARDEN.get(), BossRenderers::warden);
        event.registerEntityRenderer(ModEntities.GATE_MONARCH.get(), BossRenderers::monarch);
        event.registerEntityRenderer(ModEntities.SHADOW_MINION.get(), BossRenderers::minion);

        event.registerEntityRenderer(ModEntities.GROVE_STALKER.get(), ctx -> catalog(ctx, BossId.GROVE_STALKER));
        event.registerEntityRenderer(ModEntities.DUNE_SCORPION.get(), ctx -> catalog(ctx, BossId.DUNE_SCORPION));
        event.registerEntityRenderer(ModEntities.FROST_HOWLER.get(), ctx -> catalog(ctx, BossId.FROST_HOWLER));
        event.registerEntityRenderer(ModEntities.BOG_WITCH.get(), ctx -> catalog(ctx, BossId.BOG_WITCH));
        event.registerEntityRenderer(ModEntities.REEF_LEVIATHAN.get(), ctx -> catalog(ctx, BossId.REEF_LEVIATHAN));
        event.registerEntityRenderer(ModEntities.ASH_IMP_LORD.get(), ctx -> catalog(ctx, BossId.ASH_IMP_LORD));
        event.registerEntityRenderer(ModEntities.THUNDER_COLOSSUS.get(), ctx -> catalog(ctx, BossId.THUNDER_COLOSSUS));
        event.registerEntityRenderer(ModEntities.CRYSTAL_ORACLE.get(), ctx -> catalog(ctx, BossId.CRYSTAL_ORACLE));
        event.registerEntityRenderer(ModEntities.CRIMSON_SUMMONER.get(), ctx -> catalog(ctx, BossId.CRIMSON_SUMMONER));
        event.registerEntityRenderer(ModEntities.ABYSS_KNIGHT.get(), ctx -> catalog(ctx, BossId.ABYSS_KNIGHT));
        event.registerEntityRenderer(ModEntities.SANDSTORM_EMPRESS.get(), ctx -> catalog(ctx, BossId.SANDSTORM_EMPRESS));
        event.registerEntityRenderer(ModEntities.VOID_REAPER.get(), ctx -> catalog(ctx, BossId.VOID_REAPER));
        event.registerEntityRenderer(ModEntities.INFERNAL_BEHEMOTH.get(), ctx -> catalog(ctx, BossId.INFERNAL_BEHEMOTH));
        event.registerEntityRenderer(ModEntities.GLACIAL_SOVEREIGN.get(), ctx -> catalog(ctx, BossId.GLACIAL_SOVEREIGN));
    }

    private static HumanoidMobRenderer<ShadowScoutEntity, HumanoidModel<ShadowScoutEntity>> scout(EntityRendererProvider.Context ctx) {
        return plain(ctx, "textures/entity/zombie/zombie.png", 0.5f);
    }

    private static HumanoidMobRenderer<RankWardenEntity, HumanoidModel<RankWardenEntity>> warden(EntityRendererProvider.Context ctx) {
        return plain(ctx, "textures/entity/wither/wither.png", 0.6f);
    }

    private static HumanoidMobRenderer<GateMonarchEntity, HumanoidModel<GateMonarchEntity>> monarch(EntityRendererProvider.Context ctx) {
        return plain(ctx, "textures/entity/enderdragon/dragon.png", 0.7f);
    }

    private static HumanoidMobRenderer<ShadowMinionEntity, HumanoidModel<ShadowMinionEntity>> minion(EntityRendererProvider.Context ctx) {
        return new HumanoidMobRenderer<>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.4f) {
            @Override
            public ResourceLocation getTextureLocation(ShadowMinionEntity entity) {
                return switch (entity.kind()) {
                    case FROST -> ResourceLocation.withDefaultNamespace("textures/entity/stray/stray.png");
                    case ASH -> ResourceLocation.withDefaultNamespace("textures/entity/blaze.png");
                    case ABYSS -> ResourceLocation.withDefaultNamespace("textures/entity/wither/wither.png");
                    default -> ResourceLocation.withDefaultNamespace("textures/entity/zombie/zombie.png");
                };
            }
        };
    }

    private static AnimatedBossRenderer catalog(EntityRendererProvider.Context ctx, BossId id) {
        return new AnimatedBossRenderer(ctx, id);
    }

    private static <T extends Mob> HumanoidMobRenderer<T, HumanoidModel<T>> plain(
            EntityRendererProvider.Context ctx, String texture, float shadow
    ) {
        return new HumanoidMobRenderer<>(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), shadow) {
            @Override
            public ResourceLocation getTextureLocation(T entity) {
                return ResourceLocation.withDefaultNamespace(texture);
            }
        };
    }

    private static final class AnimatedBossRenderer
            extends HumanoidMobRenderer<CatalogBossEntity, HumanoidModel<CatalogBossEntity>> {
        private final BossId bossId;

        private AnimatedBossRenderer(EntityRendererProvider.Context ctx, BossId bossId) {
            super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), bossId.tier() == com.l7cor1s.myleveling.boss.BossTier.LARGE ? 0.8f : 0.55f);
            this.bossId = bossId;
        }

        @Override
        public ResourceLocation getTextureLocation(CatalogBossEntity entity) {
            return ResourceLocation.withDefaultNamespace(bossId.texture());
        }

        @Override
        protected void scale(CatalogBossEntity entity, PoseStack pose, float partialTick) {
            float base = switch (bossId.tier()) {
                case MINI -> 1.05f;
                case MID -> 1.25f;
                case LARGE -> 1.55f;
            };
            BossAnimState anim = entity.animState();
            float pulse = 1f;
            if (anim == BossAnimState.WINDUP || anim == BossAnimState.CAST) {
                pulse = 1f + 0.08f * Mth.sin((entity.tickCount + partialTick) * 0.8f);
            } else if (anim == BossAnimState.DASH) {
                pose.scale(1.15f, 0.9f, 1.15f);
                return;
            } else if (anim == BossAnimState.STRIKE) {
                pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-18f));
            } else if (anim == BossAnimState.SUMMON) {
                pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees((entity.tickCount + partialTick) * 8f));
            }
            pose.scale(base * pulse, base * pulse, base * pulse);
        }
    }
}
