package com.l7cor1s.myleveling.client.geo;

import com.l7cor1s.myleveling.MyLeveling;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Shared GeckoLib renderer for humanoid bosses. Uses per-entity geo/anim/texture ids.
 */
public class BossGeoRenderer<T extends Mob & GeoEntity> extends GeoEntityRenderer<T> {
    private final float scale;

    public BossGeoRenderer(EntityRendererProvider.Context context, String modelId, float shadow, float scale) {
        super(context, new DefaultedEntityGeoModel<T>(
                ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, modelId), true)
                .withAltAnimations(ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "boss")));
        this.shadowRadius = shadow;
        this.scale = scale;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
