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

    public static final KeyMapping ACTIVE_SLOT_1 = new KeyMapping(
            "key.myleveling.active_1",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY
    );

    public static final KeyMapping ACTIVE_SLOT_2 = new KeyMapping(
            "key.myleveling.active_2",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F,
            CATEGORY
    );

    private ModKeyMappings() {
    }
}
