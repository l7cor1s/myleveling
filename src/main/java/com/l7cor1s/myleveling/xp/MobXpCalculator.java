package com.l7cor1s.myleveling.xp;

import com.l7cor1s.myleveling.tag.ModEntityTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.neoforged.neoforge.common.Tags;

/**
 * Classifies killed entities and computes MyLeveling XP without hard-depending on other mods.
 * Boss/strong lists are datapack tags so Create / Iron's / pack bosses can be added safely.
 */
public final class MobXpCalculator {
    private static final float STRONG_HEALTH = 40.0f;
    private static final float BOSS_HEALTH = 200.0f;

    private MobXpCalculator() {
    }

    public static boolean grantsXp(LivingEntity victim) {
        if (victim.getType().is(ModEntityTags.NO_XP)) {
            return false;
        }
        if (victim instanceof AbstractVillager) {
            return false;
        }
        // Passive animals are not a leveling source.
        if (victim instanceof Animal && !(victim instanceof Enemy)) {
            return false;
        }
        return true;
    }

    public static MobTier classify(LivingEntity victim) {
        if (victim.getType().is(ModEntityTags.BOSSES)
                || victim.getType().is(Tags.EntityTypes.BOSSES)
                || isKnownBoss(victim)) {
            return MobTier.BOSS;
        }
        if (victim.getType().is(ModEntityTags.STRONG_MOBS)) {
            return MobTier.STRONG;
        }

        float maxHealth = victim.getMaxHealth();
        if (maxHealth >= BOSS_HEALTH) {
            return MobTier.BOSS;
        }
        if (maxHealth >= STRONG_HEALTH) {
            return MobTier.STRONG;
        }
        return MobTier.NORMAL;
    }

    public static int calculate(LivingEntity victim) {
        if (!grantsXp(victim)) {
            return 0;
        }

        MobTier tier = classify(victim);
        float maxHealth = victim.getMaxHealth();

        return switch (tier) {
            case NORMAL -> MobTier.NORMAL.getBaseXp();
            case STRONG -> MobTier.STRONG.getBaseXp() + Math.round(maxHealth / 10.0f);
            case BOSS -> MobTier.BOSS.getBaseXp() + Math.round(maxHealth / 4.0f);
        };
    }

    private static boolean isKnownBoss(LivingEntity victim) {
        return victim instanceof EnderDragon
                || victim instanceof WitherBoss
                || victim instanceof Warden;
    }
}
