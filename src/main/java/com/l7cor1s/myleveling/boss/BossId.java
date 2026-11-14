package com.l7cor1s.myleveling.boss;

import net.minecraft.world.BossEvent;

/**
 * Catalog of ranked bosses (new roster). Legacy scout/warden/monarch stay as dedicated classes.
 */
public enum BossId {
    // Mini D–C
    GROVE_STALKER("grove_stalker", BossTier.MINI, Archetype.FAST, "is_forest", 70, 0.36, 8, 3, 45, 1,
            "textures/entity/zombie/zombie.png", BossEvent.BossBarColor.GREEN),
    DUNE_SCORPION("dune_scorpion", BossTier.MINI, Archetype.TANK, "is_badlands", 90, 0.28, 9, 6, 50, 1,
            "textures/entity/spider/spider.png", BossEvent.BossBarColor.YELLOW),
    FROST_HOWLER("frost_howler", BossTier.MINI, Archetype.MAGE, "is_mountain", 75, 0.32, 7, 4, 48, 1,
            "textures/entity/wolf/wolf.png", BossEvent.BossBarColor.BLUE),
    BOG_WITCH("bog_witch", BossTier.MINI, Archetype.SUMMONER, "has_closer_water", 65, 0.27, 6, 2, 55, 1,
            "textures/entity/witch.png", BossEvent.BossBarColor.GREEN),
    REEF_LEVIATHAN("reef_leviathan", BossTier.MINI, Archetype.TANK, "is_ocean", 95, 0.26, 8, 5, 52, 1,
            "textures/entity/guardian.png", BossEvent.BossBarColor.BLUE),
    ASH_IMP_LORD("ash_imp_lord", BossTier.MINI, Archetype.FAST, "is_nether", 80, 0.35, 9, 3, 58, 1,
            "textures/entity/blaze.png", BossEvent.BossBarColor.RED),

    // Mid B–A
    THUNDER_COLOSSUS("thunder_colossus", BossTier.MID, Archetype.TANK, "is_overworld", 200, 0.26, 13, 12, 140, 2,
            "textures/entity/iron_golem/iron_golem.png", BossEvent.BossBarColor.YELLOW),
    CRYSTAL_ORACLE("crystal_oracle", BossTier.MID, Archetype.MAGE, "is_overworld", 160, 0.29, 11, 6, 130, 2,
            "textures/entity/enderman/enderman.png", BossEvent.BossBarColor.PURPLE),
    CRIMSON_SUMMONER("crimson_summoner", BossTier.MID, Archetype.SUMMONER, "is_nether", 175, 0.28, 12, 8, 145, 2,
            "textures/entity/piglin/piglin_brute.png", BossEvent.BossBarColor.RED),
    ABYSS_KNIGHT("abyss_knight", BossTier.MID, Archetype.TANK, "is_deep_ocean", 220, 0.24, 14, 14, 155, 2,
            "textures/entity/wither/wither_armor.png", BossEvent.BossBarColor.BLUE),
    SANDSTORM_EMPRESS("sandstorm_empress", BossTier.MID, Archetype.MAGE, "is_badlands", 170, 0.31, 12, 7, 135, 2,
            "textures/entity/husk/husk.png", BossEvent.BossBarColor.YELLOW),

    // Large S
    VOID_REAPER("void_reaper", BossTier.LARGE, Archetype.FLYING, "is_end", 320, 0.33, 16, 10, 320, 3,
            "textures/entity/enderman/enderman.png", BossEvent.BossBarColor.PURPLE),
    INFERNAL_BEHEMOTH("infernal_behemoth", BossTier.LARGE, Archetype.TANK, "is_nether", 380, 0.25, 18, 18, 360, 3,
            "textures/entity/wither/wither.png", BossEvent.BossBarColor.RED),
    GLACIAL_SOVEREIGN("glacial_sovereign", BossTier.LARGE, Archetype.SUMMONER, "is_mountain", 350, 0.27, 17, 14, 340, 3,
            "textures/entity/stray/stray.png", BossEvent.BossBarColor.WHITE);

    public enum Archetype { FAST, TANK, MAGE, SUMMONER, FLYING }

    private final String id;
    private final BossTier tier;
    private final Archetype archetype;
    private final String biomeHint;
    private final double maxHealth;
    private final double speed;
    private final double damage;
    private final double armor;
    private final int xpReward;
    private final int maxPhases;
    private final String texture;
    private final BossEvent.BossBarColor barColor;

    BossId(String id, BossTier tier, Archetype archetype, String biomeHint,
           double maxHealth, double speed, double damage, double armor, int xpReward, int maxPhases,
           String texture, BossEvent.BossBarColor barColor) {
        this.id = id;
        this.tier = tier;
        this.archetype = archetype;
        this.biomeHint = biomeHint;
        this.maxHealth = maxHealth;
        this.speed = speed;
        this.damage = damage;
        this.armor = armor;
        this.xpReward = xpReward;
        this.maxPhases = maxPhases;
        this.texture = texture;
        this.barColor = barColor;
    }

    public String id() {
        return id;
    }

    public BossTier tier() {
        return tier;
    }

    public Archetype archetype() {
        return archetype;
    }

    public String biomeHint() {
        return biomeHint;
    }

    public double maxHealth() {
        return maxHealth;
    }

    public double speed() {
        return speed;
    }

    public double damage() {
        return damage;
    }

    public double armor() {
        return armor;
    }

    public int xpReward() {
        return xpReward;
    }

    public int maxPhases() {
        return maxPhases;
    }

    public String texture() {
        return texture;
    }

    public BossEvent.BossBarColor barColor() {
        return barColor;
    }

    public static BossId byId(String path) {
        for (BossId id : values()) {
            if (id.id.equals(path)) {
                return id;
            }
        }
        return null;
    }
}
