package com.l7cor1s.myleveling.tag;

import com.l7cor1s.myleveling.MyLeveling;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ModEntityTags {
    public static final TagKey<EntityType<?>> BOSSES = bind("bosses");
    public static final TagKey<EntityType<?>> STRONG_MOBS = bind("strong_mobs");
    public static final TagKey<EntityType<?>> NO_XP = bind("no_xp");

    private ModEntityTags() {
    }

    private static TagKey<EntityType<?>> bind(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, path));
    }
}
