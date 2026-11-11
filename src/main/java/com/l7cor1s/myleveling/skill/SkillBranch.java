package com.l7cor1s.myleveling.skill;

import net.minecraft.network.chat.Component;

public enum SkillBranch {
    MELEE("melee"),
    MAGIC("magic"),
    ENGINEERING("engineering"),
    SURVIVAL("survival");

    private final String id;

    SkillBranch(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return Component.translatable("skill.myleveling.branch." + id);
    }
}
