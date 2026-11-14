package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.boss.BossAnimState;
import com.l7cor1s.myleveling.boss.Kv2BossEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Blocky tank silhouette for the KV-2 meme boss.
 */
public class Kv2TankModel extends EntityModel<Kv2BossEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("myleveling", "kv2"), "main");

    private final ModelPart root;
    private final ModelPart turret;
    private final ModelPart barrel;

    public Kv2TankModel(ModelPart root) {
        this.root = root;
        this.turret = root.getChild("turret");
        this.barrel = this.turret.getChild("barrel");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("hull",
                CubeListBuilder.create().texOffs(0, 0).addBox(-10, 0, -14, 20, 10, 28),
                PartPose.offset(0, 8, 0));
        root.addOrReplaceChild("track_l",
                CubeListBuilder.create().texOffs(0, 40).addBox(-12, 6, -15, 3, 8, 30),
                PartPose.ZERO);
        root.addOrReplaceChild("track_r",
                CubeListBuilder.create().texOffs(0, 40).addBox(9, 6, -15, 3, 8, 30),
                PartPose.ZERO);

        PartDefinition turret = root.addOrReplaceChild("turret",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6, -8, -6, 12, 10, 12),
                PartPose.offset(0, 8, 0));
        turret.addOrReplaceChild("barrel",
                CubeListBuilder.create().texOffs(40, 40).addBox(-1.5f, -1.5f, 0, 3, 3, 18),
                PartPose.offset(0, -3, 6));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(Kv2BossEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.turret.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.35f;
        float pitch = headPitch * Mth.DEG_TO_RAD * 0.25f;
        if (entity.animState() == BossAnimState.CAST || entity.animState() == BossAnimState.WINDUP) {
            pitch -= 0.35f + 0.08f * Mth.sin(ageInTicks * 0.6f);
            this.barrel.z = 6 + Mth.sin(ageInTicks * 0.8f);
        } else {
            this.barrel.z = 6;
        }
        this.barrel.xRot = pitch;
        if (entity.phase() >= 3) {
            this.root.zRot = Mth.sin(ageInTicks * 0.25f) * 0.04f;
        } else {
            this.root.zRot = 0;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
