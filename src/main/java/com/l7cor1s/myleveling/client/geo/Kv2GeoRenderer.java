package com.l7cor1s.myleveling.client.geo;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.boss.Kv2BossEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Kv2GeoRenderer extends GeoEntityRenderer<Kv2BossEntity> {
    public Kv2GeoRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(
                ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "kv2"), false));
        this.shadowRadius = 1.6f;
    }

    @Override
    protected float getDeathMaxRotation(Kv2BossEntity animatable) {
        return 0f; // tanks don't tip over like bipeds
    }
}
