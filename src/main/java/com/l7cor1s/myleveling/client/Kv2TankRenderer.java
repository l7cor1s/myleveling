package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.boss.Kv2BossEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class Kv2TankRenderer extends MobRenderer<Kv2BossEntity, Kv2TankModel> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("myleveling", "textures/entity/kv2.png");

    public Kv2TankRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Kv2TankModel(ctx.bakeLayer(Kv2TankModel.LAYER)), 1.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(Kv2BossEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(Kv2BossEntity entity, PoseStack pose, float partialTick) {
        pose.scale(1.35f, 1.2f, 1.45f);
    }
}
