package com.l7cor1s.myleveling.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeyMappings {
    public static final String CATEGORY = "key.categories.myleveling";

    public static final KeyMapping OPEN_SKILL_TREE = new KeyMapping(
            "key.myleveling.open_skills",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            CATEGORY
    );

    private ModKeyMappings() {
    }
}
