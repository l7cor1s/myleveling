package com.l7cor1s.myleveling.skill;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Static skill catalog for stage 2. Branches map to pack themes:
 * Melee (Epic Fight / Apotheosis-friendly attrs), Magic (Iron's Spells),
 * Engineering (Create), Survival (Farmer's Delight / Kaleidoscope lifestyle).
 */
public final class Skills {
    private static final Map<String, Skill> BY_ID = new LinkedHashMap<>();

    // --- Melee ---
    public static final Skill MELEE_GUARD = register(skill(
            "melee_guard", SkillBranch.MELEE, 1, Rank.E, null,
            bonus(Attributes.ARMOR, "melee_guard", 1.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill MELEE_POWER = register(skill(
            "melee_power", SkillBranch.MELEE, 1, Rank.D, "melee_guard",
            bonus(Attributes.ATTACK_DAMAGE, "melee_power", 1.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill MELEE_HASTE = register(skill(
            "melee_haste", SkillBranch.MELEE, 1, Rank.C, "melee_power",
            bonus(Attributes.ATTACK_SPEED, "melee_haste", 0.1, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill MELEE_VETERAN = register(skill(
            "melee_veteran", SkillBranch.MELEE, 2, Rank.B, "melee_haste",
            bonus(Attributes.ATTACK_DAMAGE, "melee_veteran", 2.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill MELEE_CHAMPION = register(skill(
            "melee_champion", SkillBranch.MELEE, 2, Rank.A, "melee_veteran",
            bonus(Attributes.ARMOR_TOUGHNESS, "melee_champion_toughness", 2.0, AttributeModifier.Operation.ADD_VALUE),
            bonus(Attributes.ATTACK_DAMAGE, "melee_champion_damage", 1.0, AttributeModifier.Operation.ADD_VALUE)
    ));

    // --- Magic (Iron's Spells 'n Spellbooks themed; soft effects, no hard API) ---
    public static final Skill MAGIC_INITIATE = register(skill(
            "magic_initiate", SkillBranch.MAGIC, 1, Rank.E, null,
            bonus(Attributes.MAX_HEALTH, "magic_initiate", 2.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill MAGIC_ATTUNEMENT = register(skill(
            "magic_attunement", SkillBranch.MAGIC, 1, Rank.D, "magic_initiate",
            bonus(Attributes.MOVEMENT_SPEED, "magic_attunement", 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    ));
    public static final Skill MAGIC_SCHOLAR = register(skill(
            "magic_scholar", SkillBranch.MAGIC, 1, Rank.C, "magic_attunement",
            bonus(Attributes.MAX_HEALTH, "magic_scholar", 2.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill MAGIC_ARCHMAGE = register(skill(
            "magic_archmage", SkillBranch.MAGIC, 2, Rank.B, "magic_scholar",
            bonus(Attributes.MAX_HEALTH, "magic_archmage", 4.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill MAGIC_ASCENDANT = register(skill(
            "magic_ascendant", SkillBranch.MAGIC, 2, Rank.A, "magic_archmage",
            bonus(Attributes.MAX_HEALTH, "magic_ascendant_hp", 4.0, AttributeModifier.Operation.ADD_VALUE),
            bonus(Attributes.MOVEMENT_SPEED, "magic_ascendant_speed", 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    ));

    // --- Engineering (Create themed) ---
    public static final Skill ENG_TINKERER = register(skill(
            "eng_tinkerer", SkillBranch.ENGINEERING, 1, Rank.E, null,
            bonus(Attributes.BLOCK_BREAK_SPEED, "eng_tinkerer", 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    ));
    public static final Skill ENG_MACHINIST = register(skill(
            "eng_machinist", SkillBranch.ENGINEERING, 1, Rank.D, "eng_tinkerer",
            bonus(Attributes.BLOCK_BREAK_SPEED, "eng_machinist", 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    ));
    public static final Skill ENG_BUILDER = register(skill(
            "eng_builder", SkillBranch.ENGINEERING, 1, Rank.C, "eng_machinist",
            bonus(Attributes.BLOCK_INTERACTION_RANGE, "eng_builder", 0.5, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill ENG_INVENTOR = register(skill(
            "eng_inventor", SkillBranch.ENGINEERING, 2, Rank.B, "eng_builder",
            bonus(Attributes.BLOCK_BREAK_SPEED, "eng_inventor", 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    ));
    public static final Skill ENG_VISIONARY = register(skill(
            "eng_visionary", SkillBranch.ENGINEERING, 2, Rank.A, "eng_inventor",
            bonus(Attributes.BLOCK_BREAK_SPEED, "eng_visionary_break", 0.35, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            bonus(Attributes.BLOCK_INTERACTION_RANGE, "eng_visionary_reach", 0.5, AttributeModifier.Operation.ADD_VALUE)
    ));

    // --- Survival (Farmer's Delight / Kaleidoscope themed) ---
    public static final Skill SURV_HARDY = register(skill(
            "surv_hardy", SkillBranch.SURVIVAL, 1, Rank.E, null,
            bonus(Attributes.MAX_HEALTH, "surv_hardy", 2.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill SURV_FORAGER = register(skill(
            "surv_forager", SkillBranch.SURVIVAL, 1, Rank.D, "surv_hardy",
            bonus(Attributes.MOVEMENT_SPEED, "surv_forager", 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    ));
    public static final Skill SURV_RESILIENT = register(skill(
            "surv_resilient", SkillBranch.SURVIVAL, 1, Rank.C, "surv_forager",
            bonus(Attributes.ARMOR, "surv_resilient", 1.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill SURV_SURVIVOR = register(skill(
            "surv_survivor", SkillBranch.SURVIVAL, 2, Rank.B, "surv_resilient",
            bonus(Attributes.MAX_HEALTH, "surv_survivor", 4.0, AttributeModifier.Operation.ADD_VALUE)
    ));
    public static final Skill SURV_IRON_WILL = register(skill(
            "surv_iron_will", SkillBranch.SURVIVAL, 2, Rank.A, "surv_survivor",
            bonus(Attributes.ARMOR, "surv_iron_will_armor", 2.0, AttributeModifier.Operation.ADD_VALUE),
            bonus(Attributes.MAX_HEALTH, "surv_iron_will_hp", 4.0, AttributeModifier.Operation.ADD_VALUE)
    ));

    private Skills() {
    }

    public static Collection<Skill> all() {
        return BY_ID.values();
    }

    public static Optional<Skill> byId(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public static List<Skill> byBranch(SkillBranch branch) {
        return BY_ID.values().stream().filter(skill -> skill.branch() == branch).toList();
    }

    private static Skill register(Skill skill) {
        BY_ID.put(skill.id(), skill);
        return skill;
    }

    private static Skill skill(
            String id,
            SkillBranch branch,
            int cost,
            Rank minRank,
            String prerequisite,
            Skill.AttributeBonus... bonuses
    ) {
        return new Skill(
                id,
                branch,
                cost,
                minRank,
                Optional.ofNullable(prerequisite),
                List.of(bonuses)
        );
    }

    private static Skill.AttributeBonus bonus(
            Holder<Attribute> attribute,
            String path,
            double amount,
            AttributeModifier.Operation operation
    ) {
        return new Skill.AttributeBonus(
                attribute,
                ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "skill/" + path),
                amount,
                operation
        );
    }
}
