package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.rank.Rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Full passive / active definitions for all branches.
 */
public final class SkillCatalog {
    private static final Map<String, PassiveSkill> PASSIVES = new LinkedHashMap<>();
    private static final Map<String, ActiveSkill> ACTIVES = new LinkedHashMap<>();

    static {
        // --- Melee ---
        pair(melee("bleed", Rank.D), melee("stun_chance", Rank.D));
        pair(melee("vampirism", Rank.C), melee("shield_break", Rank.C));
        pair(melee("low_hp_damage", Rank.B), melee("crit_chance", Rank.B));
        pair(melee("cleave", Rank.A), melee("damage_reduction", Rank.A));
        pair(melee("strong_vampirism", Rank.S), melee("execute", Rank.S));
        active("melee_dash", SkillBranch.MELEE, Rank.C, 1, 12 * 20);
        active("melee_smash", SkillBranch.MELEE, Rank.A, 2, 20 * 20);

        // --- Ranged ---
        pair(ranged("double_shot", Rank.D), ranged("poison_tip", Rank.D));
        pair(ranged("random_debuff", Rank.C), ranged("extra_range", Rank.C));
        pair(ranged("pierce", Rank.B), ranged("ignite", Rank.B));
        pair(ranged("slow_shot", Rank.A), ranged("free_arrow", Rank.A));
        pair(ranged("heavy_pierce", Rank.S), ranged("soft_homing", Rank.S));
        active("ranged_volley", SkillBranch.RANGED, Rank.C, 1, 14 * 20);
        active("ranged_snipe", SkillBranch.RANGED, Rank.A, 2, 18 * 20);

        // --- Magic ---
        pair(magic("max_mana", Rank.D), magic("mana_regen", Rank.D));
        pair(magic("cdr", Rank.C), magic("mana_vamp", Rank.C));
        pair(magic("chain_spells", Rank.B), magic("spell_power", Rank.B));
        pair(magic("mana_shield", Rank.A), magic("mana_cost_down", Rank.A));
        pair(magic("spell_amp", Rank.S), magic("low_hp_regen", Rank.S));
        active("magic_blink", SkillBranch.MAGIC, Rank.C, 1, 10 * 20);
        active("magic_nova", SkillBranch.MAGIC, Rank.A, 2, 22 * 20);

        // --- Engineering ---
        pair(eng("mine_speed", Rank.D), eng("tool_efficiency", Rank.D));
        pair(eng("auto_repair", Rank.C), eng("craft_bonus", Rank.C));
        pair(eng("create_bonus", Rank.B), eng("fuel_save", Rank.B));
        pair(eng("mech_damage", Rank.A), eng("auto_loot", Rank.A));
        pair(eng("create_mastery", Rank.S), eng("double_ore", Rank.S));
        active("eng_haste", SkillBranch.ENGINEERING, Rank.C, 1, 16 * 20);
        active("eng_construct", SkillBranch.ENGINEERING, Rank.A, 2, 30 * 20);

        // --- Survival ---
        pair(surv("max_hp", Rank.D), surv("regen", Rank.D));
        pair(surv("resistance", Rank.C), surv("speed", Rank.C));
        pair(surv("food_regen", Rank.B), surv("fall_guard", Rank.B));
        pair(surv("panic_iframe", Rank.A), surv("fast_regen", Rank.A));
        pair(surv("strong_regen", Rank.S), surv("death_save", Rank.S));
        active("surv_bulwark", SkillBranch.SURVIVAL, Rank.C, 1, 20 * 20);
        active("surv_shield", SkillBranch.SURVIVAL, Rank.A, 2, 24 * 20);

        // --- Hunter ---
        pair(hunt("mob_xp", Rank.D), hunt("boss_damage", Rank.D));
        pair(hunt("extra_loot", Rank.C), hunt("boss_resist", Rank.C));
        pair(hunt("elite_damage", Rank.B), hunt("more_xp", Rank.B));
        pair(hunt("rare_loot", Rank.A), hunt("all_mob_damage", Rank.A));
        pair(hunt("boss_slayer", Rank.S), hunt("double_loot", Rank.S));
        active("hunt_mark", SkillBranch.HUNTER, Rank.C, 1, 16 * 20);
        active("hunt_wrath", SkillBranch.HUNTER, Rank.A, 2, 26 * 20);
    }

    private SkillCatalog() {
    }

    public static void bootstrap() {
        // Forces static init.
        passives();
        actives();
        MinorUpgrade.all();
    }

    public static Collection<PassiveSkill> passives() {
        return PASSIVES.values();
    }

    public static Collection<ActiveSkill> actives() {
        return ACTIVES.values();
    }

    public static Optional<PassiveSkill> passive(String id) {
        return Optional.ofNullable(PASSIVES.get(id));
    }

    public static Optional<ActiveSkill> active(String id) {
        return Optional.ofNullable(ACTIVES.get(id));
    }

    public static List<PassiveSkill> passivesFor(SkillBranch branch, Rank rank) {
        List<PassiveSkill> list = new ArrayList<>(2);
        for (PassiveSkill skill : PASSIVES.values()) {
            if (skill.branch() == branch && skill.requiredRank() == rank) {
                list.add(skill);
            }
        }
        return list;
    }

    public static List<PassiveSkill> passivesFor(SkillBranch branch) {
        List<PassiveSkill> list = new ArrayList<>();
        for (PassiveSkill skill : PASSIVES.values()) {
            if (skill.branch() == branch) {
                list.add(skill);
            }
        }
        return list;
    }

    public static List<ActiveSkill> activesFor(SkillBranch branch) {
        List<ActiveSkill> list = new ArrayList<>();
        for (ActiveSkill skill : ACTIVES.values()) {
            if (skill.branch() == branch) {
                list.add(skill);
            }
        }
        return list;
    }

    private static void pair(PassiveSkill a, PassiveSkill b) {
        PASSIVES.put(a.id(), new PassiveSkill(a.id(), a.branch(), a.requiredRank(), b.id(), a.passivePointCost()));
        PASSIVES.put(b.id(), new PassiveSkill(b.id(), b.branch(), b.requiredRank(), a.id(), b.passivePointCost()));
    }

    private static PassiveSkill melee(String name, Rank rank) {
        return new PassiveSkill("melee_" + rank.name().toLowerCase() + "_" + name, SkillBranch.MELEE, rank, "", 1);
    }

    private static PassiveSkill ranged(String name, Rank rank) {
        return new PassiveSkill("ranged_" + rank.name().toLowerCase() + "_" + name, SkillBranch.RANGED, rank, "", 1);
    }

    private static PassiveSkill magic(String name, Rank rank) {
        return new PassiveSkill("magic_" + rank.name().toLowerCase() + "_" + name, SkillBranch.MAGIC, rank, "", 1);
    }

    private static PassiveSkill eng(String name, Rank rank) {
        return new PassiveSkill("eng_" + rank.name().toLowerCase() + "_" + name, SkillBranch.ENGINEERING, rank, "", 1);
    }

    private static PassiveSkill surv(String name, Rank rank) {
        return new PassiveSkill("surv_" + rank.name().toLowerCase() + "_" + name, SkillBranch.SURVIVAL, rank, "", 1);
    }

    private static PassiveSkill hunt(String name, Rank rank) {
        return new PassiveSkill("hunt_" + rank.name().toLowerCase() + "_" + name, SkillBranch.HUNTER, rank, "", 1);
    }

    private static void active(String id, SkillBranch branch, Rank rank, int cost, int cooldown) {
        ACTIVES.put(id, new ActiveSkill(id, branch, rank, cost, cooldown));
    }
}
