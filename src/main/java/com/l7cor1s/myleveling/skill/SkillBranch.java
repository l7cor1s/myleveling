package com.l7cor1s.myleveling.skill;

import net.minecraft.network.chat.Component;

public enum SkillBranch {
    MELEE("melee"),
    RANGED("ranged"),
    MAGIC("magic"),
    ENGINEERING("engineering"),
    SURVIVAL("survival"),
    HUNTER("hunter");

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

    public static SkillBranch byId(String id) {
        for (SkillBranch branch : values()) {
            if (branch.id.equalsIgnoreCase(id)) {
                return branch;
            }
        }
        throw new IllegalArgumentException("Unknown branch: " + id);
    }
}
