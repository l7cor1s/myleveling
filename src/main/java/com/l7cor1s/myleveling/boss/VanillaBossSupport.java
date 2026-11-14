package com.l7cor1s.myleveling.boss;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;

/**
 * Shared boss-bar, naming, enrage and loot for vanilla-based ranked bosses.
 */
public final class VanillaBossSupport {
    private VanillaBossSupport() {
    }

    public static AttributeSupplier.Builder attributes(BossId id) {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, id.maxHealth())
                .add(Attributes.MOVEMENT_SPEED, id.speed())
                .add(Attributes.ATTACK_DAMAGE, id.damage())
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ARMOR, id.armor())
                .add(Attributes.KNOCKBACK_RESISTANCE, id.tier() == BossTier.LARGE ? 0.85 : (id.tier() == BossTier.MID ? 0.55 : 0.25))
                .add(Attributes.ATTACK_KNOCKBACK, id.tier() == BossTier.LARGE ? 1.4 : 0.7);
    }

    public static ServerBossEvent createBar(BossId id) {
        ServerBossEvent bar = new ServerBossEvent(
                Component.translatable("entity.myleveling." + id.id()),
                id.barColor(),
                BossEvent.BossBarOverlay.NOTCHED_6
        );
        bar.setVisible(id.tier().usesBossBar());
        return bar;
    }

    public static void applyIdentity(Mob mob, BossId id) {
        mob.setCustomName(Component.translatable("entity.myleveling." + id.id()));
        mob.setCustomNameVisible(true);
        mob.setPersistenceRequired();
        // Ensure attributes match catalog even if parent class sets defaults
        set(mob, Attributes.MAX_HEALTH, id.maxHealth());
        set(mob, Attributes.MOVEMENT_SPEED, id.speed());
        set(mob, Attributes.ATTACK_DAMAGE, id.damage());
        set(mob, Attributes.ARMOR, id.armor());
        mob.setHealth(mob.getMaxHealth());
    }

    private static void set(Mob mob, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr, double value) {
        AttributeInstance instance = mob.getAttribute(attr);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    public static void tickBar(ServerBossEvent bar, Mob mob) {
        if (!bar.isVisible()) {
            return;
        }
        bar.setProgress(mob.getHealth() / mob.getMaxHealth());
    }

    public static void onSeen(ServerBossEvent bar, ServerPlayer player, Mob mob, BossId id, boolean[] announced) {
        if (bar.isVisible()) {
            bar.addPlayer(player);
        }
        if (!announced[0]) {
            announced[0] = true;
            yell(mob, id, "spawn");
        }
    }

    public static void onUnseen(ServerBossEvent bar, ServerPlayer player) {
        bar.removePlayer(player);
    }

    public static void tickCombat(Mob mob, BossId id, boolean[] enraged) {
        if (mob.level().isClientSide()) {
            return;
        }
        float hp = mob.getHealth() / mob.getMaxHealth();
        if (!enraged[0] && hp <= 0.5f && id.maxPhases() >= 2) {
            enraged[0] = true;
            mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 90, id.tier() == BossTier.LARGE ? 1 : 0));
            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 90, 0));
            yell(mob, id, "phase");
        }
        if (enraged[0] && id.tier() == BossTier.LARGE && mob.tickCount % 80 == 0) {
            mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0));
        }
    }

    public static void drop(Mob mob, BossId id, DamageSource source, boolean recentlyHit) {
        yell(mob, id, "death");
        BossLootHelper.dropStandard(mob, id, mob.getRandom());
    }

    public static void yell(Mob mob, BossId id, String key) {
        if (!(mob.level() instanceof ServerLevel)) {
            return;
        }
        Component line = Component.translatable("boss.myleveling." + id.id() + "." + key);
        Component prefix = Component.literal("§5[§d")
                .append(Component.translatable("entity.myleveling." + id.id()))
                .append(Component.literal("§5]§r "));
        for (ServerPlayer player : mob.level().getEntitiesOfClass(ServerPlayer.class, mob.getBoundingBox().inflate(48.0))) {
            player.displayClientMessage(Component.empty().append(prefix).append(line), false);
        }
    }

    public static void clearBar(ServerBossEvent bar) {
        bar.removeAllPlayers();
    }
}
