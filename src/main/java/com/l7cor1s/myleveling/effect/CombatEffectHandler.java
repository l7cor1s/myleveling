package com.l7cor1s.myleveling.effect;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import com.l7cor1s.myleveling.xp.MobTier;
import com.l7cor1s.myleveling.xp.MobXpCalculator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class CombatEffectHandler {
    private CombatEffectHandler() {
    }

    @SubscribeEvent
    public static void onOutgoing(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) {
            return;
        }

        PlayerProgress progress = PlayerSkillData.get(player);
        RandomSource random = player.getRandom();
        float amount = event.getAmount();
        boolean ranged = isRanged(event.getSource());

        // --- Melee ---
        if (!ranged) {
            if (progress.hasPassive("melee_b_low_hp_damage") && player.getHealth() / player.getMaxHealth() <= 0.35f) {
                amount *= 1.18f;
            }
            if (progress.hasPassive("melee_b_crit_chance") && random.nextFloat() < 0.12f) {
                amount *= 1.5f;
            }
            if (progress.hasPassive("melee_c_shield_break") && target.getUseItem().is(net.minecraft.world.item.Items.SHIELD)) {
                amount *= 1.25f;
                target.stopUsingItem();
            }
        }

        // --- Ranged ---
        if (ranged) {
            if (progress.hasPassive("ranged_b_pierce")) {
                amount *= 1.08f;
            }
            if (progress.hasPassive("ranged_s_heavy_pierce")) {
                amount *= 1.16f;
            }
            if (progress.hasPassive("ranged_d_double_shot") && random.nextFloat() < 0.12f) {
                amount *= 1.35f;
            }
        }

        // --- Magic amp (soft: magic damage sources / ISS-like) ---
        if (isMagic(event.getSource())) {
            if (progress.hasPassive("magic_b_spell_power")) {
                amount *= 1.10f;
            }
            if (progress.hasPassive("magic_s_spell_amp")) {
                amount *= 1.18f;
            }
        }

        // --- Hunter ---
        MobTier tier = MobXpCalculator.classify(target);
        if (progress.hasPassive("hunt_d_boss_damage") && tier == MobTier.BOSS) {
            amount *= 1.12f;
        }
        if (progress.hasPassive("hunt_b_elite_damage") && tier == MobTier.STRONG) {
            amount *= 1.10f;
        }
        if (progress.hasPassive("hunt_a_all_mob_damage") && !(target instanceof Player)) {
            amount *= 1.08f;
        }
        if (progress.hasPassive("hunt_s_boss_slayer") && tier == MobTier.BOSS) {
            amount *= 1.20f;
        }
        if (ActiveSkillExecutor.hasHunterMark(target) || ActiveSkillExecutor.hasWrath(player)) {
            if (tier == MobTier.BOSS || ActiveSkillExecutor.hasWrath(player)) {
                amount *= ActiveSkillExecutor.hasWrath(player) ? 1.35f : 1.15f;
            }
        }

        // Engineering mech damage — bonus vs non-players when active construct buff
        if (progress.hasPassive("eng_a_mech_damage") && !(target instanceof Player)) {
            amount *= 1.06f;
        }

        event.setAmount(amount);
    }

    @SubscribeEvent
    public static void onDamageDealt(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) {
            return;
        }

        PlayerProgress progress = PlayerSkillData.get(player);
        RandomSource random = player.getRandom();
        float dealt = event.getNewDamage();
        boolean ranged = isRanged(event.getSource());

        if (!ranged) {
            if (progress.hasPassive("melee_d_bleed") && random.nextFloat() < 0.25f) {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0));
            }
            if (progress.hasPassive("melee_d_stun_chance") && random.nextFloat() < 0.10f) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 4));
                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30, 2));
            }
            if (progress.hasPassive("melee_c_vampirism")) {
                player.heal(dealt * 0.06f);
            }
            if (progress.hasPassive("melee_s_strong_vampirism")) {
                player.heal(dealt * 0.12f);
            }
            if (progress.hasPassive("melee_a_cleave") && player.level() instanceof ServerLevel level) {
                for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(2.0),
                        e -> e != target && e != player && e.isAlive() && !(e instanceof Player))) {
                    nearby.hurt(player.damageSources().playerAttack(player), dealt * 0.25f);
                }
            }
            if (progress.hasPassive("melee_s_execute") && target.getMaxHealth() <= 30.0f && random.nextFloat() < 0.08f) {
                target.hurt(player.damageSources().playerAttack(player), target.getHealth() + 1.0f);
            }
        }

        if (ranged) {
            if (progress.hasPassive("ranged_d_poison_tip") && random.nextFloat() < 0.30f) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
            }
            if (progress.hasPassive("ranged_c_random_debuff") && random.nextFloat() < 0.20f) {
                applyRandomDebuff(target, random);
            }
            if (progress.hasPassive("ranged_b_ignite") && random.nextFloat() < 0.25f) {
                target.igniteForTicks(60);
            }
            if (progress.hasPassive("ranged_a_slow_shot")) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            }
        }

        if (isMagic(event.getSource()) && progress.hasPassive("magic_c_mana_vamp")) {
            float max = com.l7cor1s.myleveling.skill.PassiveAttributeEffects.maxMana(progress);
            PlayerSkillData.set(player, progress.withMana(Math.min(max, progress.mana() + dealt * 0.08f)));
        }

        if (progress.hasPassive("magic_b_chain_spells") && isMagic(event.getSource())
                && player.level() instanceof ServerLevel level && random.nextFloat() < 0.20f) {
            for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(4.0),
                    e -> e != target && e != player && e.isAlive())) {
                nearby.hurt(player.damageSources().magic(), dealt * 0.35f);
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onIncoming(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        PlayerProgress progress = PlayerSkillData.get(player);
        float amount = event.getAmount();

        if (progress.hasPassive("melee_a_damage_reduction")) {
            amount *= 0.92f;
        }
        if (progress.hasPassive("hunt_c_boss_resist")) {
            if (event.getSource().getEntity() instanceof LivingEntity living
                    && MobXpCalculator.classify(living) == MobTier.BOSS) {
                amount *= 0.88f;
            }
        }
        if (progress.hasPassive("magic_a_mana_shield") && progress.mana() > 1.0f) {
            float absorbed = Math.min(progress.mana(), amount * 0.35f);
            amount -= absorbed;
            PlayerSkillData.set(player, progress.withMana(Math.max(0, progress.mana() - absorbed)));
        }
        if (progress.hasPassive("surv_a_panic_iframe") && player.getHealth() / player.getMaxHealth() <= 0.25f) {
            amount *= 0.70f;
        }

        event.setAmount(Math.max(0.0f, amount));
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgress progress = PlayerSkillData.get(player);
            if (progress.hasPassive("surv_s_death_save") && player.getRandom().nextFloat() < 0.18f) {
                event.setCanceled(true);
                player.setHealth(Math.max(4.0f, player.getMaxHealth() * 0.25f));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            }
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) {
            return;
        }
        LivingEntity victim = event.getEntity();
        PlayerProgress progress = PlayerSkillData.get(killer);
        RandomSource random = killer.getRandom();

        if (progress.hasPassive("eng_a_auto_loot") && killer.level() instanceof ServerLevel level) {
            // Mild magnet: pull nearby item entities toward player shortly after kill.
            level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, victim.getBoundingBox().inflate(6.0))
                    .forEach(item -> item.setPickUpDelay(0));
        }

        if (progress.hasPassive("eng_s_double_ore") && random.nextFloat() < 0.08f) {
            // Handled more precisely on break; here grant a small nugget consolation for combat kills.
            killer.getInventory().add(new ItemStack(net.minecraft.world.item.Items.IRON_NUGGET, 1));
        }

        if ((progress.hasPassive("hunt_c_extra_loot") && random.nextFloat() < 0.10f)
                || (progress.hasPassive("hunt_a_rare_loot") && random.nextFloat() < 0.06f)
                || (progress.hasPassive("hunt_s_double_loot") && random.nextFloat() < 0.12f)) {
            killer.getInventory().add(new ItemStack(net.minecraft.world.item.Items.GOLD_NUGGET, 1 + random.nextInt(2)));
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        // Attribute modifiers already apply; Create soft bonus is in PassiveAttributeEffects.
    }

    private static boolean isRanged(DamageSource source) {
        if (source.getDirectEntity() instanceof AbstractArrow || source.getDirectEntity() instanceof Projectile) {
            return true;
        }
        String msg = source.getMsgId();
        return msg.contains("arrow") || msg.contains("trident") || msg.contains("projectile") || msg.contains("thrown");
    }

    private static boolean isMagic(DamageSource source) {
        if (source.is(net.minecraft.tags.DamageTypeTags.WITCH_RESISTANT_TO) || source.is(net.minecraft.tags.DamageTypeTags.IS_LIGHTNING)) {
            return true;
        }
        String msg = source.getMsgId();
        return msg.contains("magic") || msg.contains("spell") || msg.contains("indirectMagic") || msg.contains("thorns");
    }

    private static void applyRandomDebuff(LivingEntity target, RandomSource random) {
        switch (random.nextInt(3)) {
            case 0 -> target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
            case 1 -> target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
            default -> target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
        }
    }
}
