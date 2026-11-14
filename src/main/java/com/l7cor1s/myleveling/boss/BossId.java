package com.l7cor1s.myleveling.boss;

import net.minecraft.world.BossEvent;

/**
 * Ranked field bosses — vanilla-based reskins (plus dedicated KV-2).
 */
public enum BossId {
    // Legacy aliases kept as ranked vanilla reskins
    SHADOW_SCOUT("shadow_scout", BossTier.MINI, VanillaBase.VINDICATOR, "is_overworld",
            70, 0.34, 8, 4, 45, 1, BossEvent.BossBarColor.PURPLE),
    RANK_WARDEN("rank_warden", BossTier.MID, VanillaBase.RAVAGER, "is_overworld",
            200, 0.27, 14, 12, 130, 2, BossEvent.BossBarColor.RED),
    GATE_MONARCH("gate_monarch", BossTier.LARGE, VanillaBase.WITHER_SKELETON, "is_overworld",
            400, 0.28, 18, 14, 500, 3, BossEvent.BossBarColor.PURPLE),

    // Mini D–C
    GROVE_STALKER("grove_stalker", BossTier.MINI, VanillaBase.ZOMBIE, "is_forest",
            75, 0.33, 8, 4, 48, 1, BossEvent.BossBarColor.GREEN),
    DUNE_SCORPION("dune_scorpion", BossTier.MINI, VanillaBase.CAVE_SPIDER, "is_badlands",
            85, 0.34, 9, 5, 50, 1, BossEvent.BossBarColor.YELLOW),
    FROST_HOWLER("frost_howler", BossTier.MINI, VanillaBase.STRAY, "is_mountain",
            80, 0.32, 8, 4, 50, 1, BossEvent.BossBarColor.BLUE),
    BOG_WITCH("bog_witch", BossTier.MINI, VanillaBase.WITCH, "has_closer_water",
            70, 0.28, 7, 3, 55, 1, BossEvent.BossBarColor.GREEN),
    REEF_LEVIATHAN("reef_leviathan", BossTier.MINI, VanillaBase.ELDER_GUARDIAN, "is_ocean",
            110, 0.25, 10, 8, 60, 1, BossEvent.BossBarColor.BLUE),
    ASH_IMP_LORD("ash_imp_lord", BossTier.MINI, VanillaBase.BLAZE, "is_nether",
            90, 0.30, 10, 4, 58, 1, BossEvent.BossBarColor.RED),

    // Mid B–A
    THUNDER_COLOSSUS("thunder_colossus", BossTier.MID, VanillaBase.RAVAGER, "is_overworld",
            220, 0.26, 15, 14, 145, 2, BossEvent.BossBarColor.YELLOW),
    CRYSTAL_ORACLE("crystal_oracle", BossTier.MID, VanillaBase.EVOKER, "is_overworld",
            165, 0.29, 12, 6, 135, 2, BossEvent.BossBarColor.PURPLE),
    CRIMSON_SUMMONER("crimson_summoner", BossTier.MID, VanillaBase.PIGLIN_BRUTE, "is_nether",
            180, 0.28, 13, 9, 150, 2, BossEvent.BossBarColor.RED),
    ABYSS_KNIGHT("abyss_knight", BossTier.MID, VanillaBase.DROWNED, "is_deep_ocean",
            210, 0.27, 14, 12, 155, 2, BossEvent.BossBarColor.BLUE),
    SANDSTORM_EMPRESS("sandstorm_empress", BossTier.MID, VanillaBase.HUSK, "is_badlands",
            175, 0.31, 12, 8, 140, 2, BossEvent.BossBarColor.YELLOW),

    // Large S
    VOID_REAPER("void_reaper", BossTier.LARGE, VanillaBase.ENDERMAN, "is_end",
            320, 0.32, 17, 10, 330, 3, BossEvent.BossBarColor.PURPLE),
    INFERNAL_BEHEMOTH("infernal_behemoth", BossTier.LARGE, VanillaBase.RAVAGER, "is_nether",
            380, 0.25, 19, 18, 370, 3, BossEvent.BossBarColor.RED),
    GLACIAL_SOVEREIGN("glacial_sovereign", BossTier.LARGE, VanillaBase.EVOKER, "is_mountain",
            350, 0.27, 16, 12, 350, 3, BossEvent.BossBarColor.WHITE);

    public enum VanillaBase {
        ZOMBIE, HUSK, DROWNED, VINDICATOR, RAVAGER, WITCH, STRAY,
        CAVE_SPIDER, BLAZE, EVOKER, PIGLIN_BRUTE, ELDER_GUARDIAN,
        ENDERMAN, WITHER_SKELETON
    }

    private final String id;
    private final BossTier tier;
    private final VanillaBase base;
    private final String biomeHint;
    private final double maxHealth;
    private final double speed;
    private final double damage;
    private final double armor;
    private final int xpReward;
    private final int maxPhases;
    private final BossEvent.BossBarColor barColor;

    BossId(String id, BossTier tier, VanillaBase base, String biomeHint,
           double maxHealth, double speed, double damage, double armor,
           int xpReward, int maxPhases, BossEvent.BossBarColor barColor) {
        this.id = id;
        this.tier = tier;
        this.base = base;
        this.biomeHint = biomeHint;
        this.maxHealth = maxHealth;
        this.speed = speed;
        this.damage = damage;
        this.armor = armor;
        this.xpReward = xpReward;
        this.maxPhases = maxPhases;
        this.barColor = barColor;
    }

    public String id() { return id; }
    public BossTier tier() { return tier; }
    public VanillaBase base() { return base; }
    public String biomeHint() { return biomeHint; }
    public double maxHealth() { return maxHealth; }
    public double speed() { return speed; }
    public double damage() { return damage; }
    public double armor() { return armor; }
    public int xpReward() { return xpReward; }
    public int maxPhases() { return maxPhases; }
    public BossEvent.BossBarColor barColor() { return barColor; }

    public static BossId byId(String path) {
        for (BossId id : values()) {
            if (id.id.equals(path)) {
                return id;
            }
        }
        return null;
    }
}
