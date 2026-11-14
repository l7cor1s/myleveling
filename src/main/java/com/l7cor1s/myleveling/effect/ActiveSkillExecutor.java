package com.l7cor1s.myleveling.effect;

import com.l7cor1s.myleveling.compat.CompatMods;
import com.l7cor1s.myleveling.network.ProgressSync;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.skill.ActiveSkill;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import com.l7cor1s.myleveling.skill.SkillCatalog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class ActiveSkillExecutor {
    private static final Map<UUID, Long> WRATH_UNTIL = new HashMap<>();
    private static final Map<UUID, Long> TEMP_GOLEMS = new HashMap<>();
    private static final WeakHashMap<LivingEntity, Long> MARKED_UNTIL = new WeakHashMap<>();

    private ActiveSkillExecutor() {
    }

    public static PlayerSkillData.Result activateSlot(ServerPlayer player, int slot) {
        PlayerProgress progress = PlayerSkillData.get(player);
        String activeId = progress.equipped(slot);
        if (activeId == null || activeId.isEmpty()) {
            return PlayerSkillData.Result.UNKNOWN;
        }
        return activate(player, activeId);
    }

    public static PlayerSkillData.Result activate(ServerPlayer player, String activeId) {
        var skillOpt = SkillCatalog.active(activeId);
        if (skillOpt.isEmpty()) {
            return PlayerSkillData.Result.UNKNOWN;
        }
        ActiveSkill skill = skillOpt.get();
        PlayerProgress progress = PlayerSkillData.get(player);
        if (!progress.hasActiveUnlocked(activeId)) {
            return PlayerSkillData.Result.UNKNOWN;
        }

        long gameTime = player.level().getGameTime();
        if (progress.isOnCooldown(activeId, gameTime)) {
            player.displayClientMessage(Component.translatable("message.myleveling.active_cooldown"), true);
            return PlayerSkillData.Result.ALREADY;
        }

        // Mana cost for magic actives.
        float manaCost = switch (activeId) {
            case "magic_blink" -> 6.0f;
            case "magic_nova" -> 12.0f;
            default -> 0.0f;
        };
        if (manaCost > 0 && progress.mana() < manaCost) {
            player.displayClientMessage(Component.translatable("message.myleveling.not_enough_mana"), true);
            return PlayerSkillData.Result.NOT_ENOUGH_SKILL_POINTS;
        }

        boolean ok = switch (activeId) {
            case "melee_dash" -> dashStrike(player);
            case "melee_smash" -> smash(player);
            case "ranged_volley" -> volleyBuff(player);
            case "ranged_snipe" -> snipeBuff(player);
            case "magic_blink" -> blink(player);
            case "magic_nova" -> nova(player);
            case "eng_haste" -> engHaste(player);
            case "eng_construct" -> construct(player);
            case "surv_bulwark" -> bulwark(player);
            case "surv_shield" -> shield(player);
            case "hunt_mark" -> markBoss(player);
            case "hunt_wrath" -> wrath(player);
            default -> false;
        };

        if (!ok) {
            return PlayerSkillData.Result.UNKNOWN;
        }

        int cooldown = skill.cooldownTicks();
        if (progress.hasPassive("magic_c_cdr") && activeId.startsWith("magic_")) {
            cooldown = Math.round(cooldown * 0.85f);
        }
        if (progress.hasPassive("magic_a_mana_cost_down")) {
            manaCost *= 0.8f;
        }

        Map<String, Long> cds = new HashMap<>(progress.cooldownEnds());
        cds.put(activeId, gameTime + cooldown);
        PlayerProgress updated = progress.withCooldowns(cds);
        if (manaCost > 0) {
            updated = updated.withMana(Math.max(0, updated.mana() - manaCost));
        }
        PlayerSkillData.set(player, updated);
        ProgressSync.sendTo(player);
        player.displayClientMessage(Component.translatable("message.myleveling.active_used", skill.getDisplayName()), true);
        return PlayerSkillData.Result.SUCCESS;
    }

    public static boolean hasWrath(Player player) {
        Long until = WRATH_UNTIL.get(player.getUUID());
        return until != null && player.level().getGameTime() < until;
    }

    public static boolean hasHunterMark(LivingEntity entity) {
        Long until = MARKED_UNTIL.get(entity);
        return until != null && entity.level().getGameTime() < until;
    }

    private static boolean dashStrike(ServerPlayer player) {
        Vec3 look = player.getLookAngle().normalize();
        player.setDeltaMovement(look.x * 1.6, Math.max(0.1, look.y * 0.4), look.z * 1.6);
        player.hurtMarked = true;
        if (player.level() instanceof ServerLevel level) {
            AABB box = player.getBoundingBox().inflate(2.5).move(look.scale(2.0));
            float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.4f;
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive())) {
                target.hurt(player.damageSources().playerAttack(player), damage);
            }
            level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 1, player.getZ(), 12, 0.4, 0.2, 0.4, 0.02);
        }
        return true;
    }

    private static boolean smash(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        float damage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0f;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(3.5),
                e -> e != player && e.isAlive())) {
            target.hurt(player.damageSources().playerAttack(player), damage);
            Vec3 push = target.position().subtract(player.position()).normalize().scale(1.2).add(0, 0.35, 0);
            target.setDeltaMovement(target.getDeltaMovement().add(push));
            target.hurtMarked = true;
        }
        level.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 2, 0, 0, 0, 0);
        return true;
    }

    private static boolean volleyBuff(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 8 * 20, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 8 * 20, 0));
        return true;
    }

    private static boolean snipeBuff(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10 * 20, 1));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 10 * 20, 0));
        return true;
    }

    private static boolean blink(ServerPlayer player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(8.0));
        BlockHitResult hit = player.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 dest = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation().subtract(player.getLookAngle().scale(0.5));
        player.teleportTo(dest.x, dest.y, dest.z);
        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 20, 0.4, 0.5, 0.4, 0.1);
        }
        return true;
    }

    private static boolean nova(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        float damage = 8.0f + (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8f;
        if (PlayerSkillData.hasPassive(player, "magic_s_spell_amp")) {
            damage *= 1.2f;
        }
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5.0),
                e -> e != player && e.isAlive())) {
            target.hurt(player.damageSources().magic(), damage);
        }
        level.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1, player.getZ(), 40, 2.0, 0.8, 2.0, 0.01);
        return true;
    }

    private static boolean engHaste(ServerPlayer player) {
        int amp = CompatMods.isLoaded(CompatMods.CREATE) ? 2 : 1;
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 12 * 20, amp));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 12 * 20, 0));
        return true;
    }

    private static boolean construct(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        if (CompatMods.isLoaded(CompatMods.CREATE)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15 * 20, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 15 * 20, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 15 * 20, 1));
            level.sendParticles(ParticleTypes.CRIT, player.getX(), player.getY() + 1, player.getZ(), 30, 0.6, 0.6, 0.6, 0.1);
            player.displayClientMessage(Component.translatable("message.myleveling.create_construct"), true);
            return true;
        }

        IronGolem golem = net.minecraft.world.entity.EntityType.IRON_GOLEM.create(level);
        if (golem == null) {
            return false;
        }
        BlockPos pos = player.blockPosition().relative(player.getDirection());
        golem.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), 0);
        golem.setPlayerCreated(true);
        level.addFreshEntity(golem);
        golem.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 0));
        TEMP_GOLEMS.put(golem.getUUID(), level.getGameTime() + 20L * 20L);
        return true;
    }

    public static void tickTempEntities(ServerLevel level) {
        if (TEMP_GOLEMS.isEmpty()) {
            return;
        }
        long now = level.getGameTime();
        TEMP_GOLEMS.entrySet().removeIf(entry -> {
            if (now < entry.getValue()) {
                return false;
            }
            var entity = level.getEntity(entry.getKey());
            if (entity != null) {
                entity.discard();
            }
            return true;
        });
    }

    private static boolean bulwark(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 12 * 20, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 12 * 20, 1));
        player.heal(4.0f);
        return true;
    }

    private static boolean shield(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 8 * 20, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 8 * 20, 1));
        return true;
    }

    private static boolean markBoss(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        LivingEntity best = null;
        double bestDist = 24.0 * 24.0;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(24.0),
                e -> e != player && e.isAlive())) {
            if (com.l7cor1s.myleveling.xp.MobXpCalculator.classify(entity) != com.l7cor1s.myleveling.xp.MobTier.BOSS
                    && entity.getMaxHealth() < 80.0f) {
                continue;
            }
            double d = entity.distanceToSqr(player);
            if (d < bestDist) {
                bestDist = d;
                best = entity;
            }
        }
        if (best == null) {
            player.displayClientMessage(Component.translatable("message.myleveling.no_boss_nearby"), true);
            return false;
        }
        MARKED_UNTIL.put(best, level.getGameTime() + 20L * 20L);
        best.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 20, 0));
        return true;
    }

    private static boolean wrath(ServerPlayer player) {
        WRATH_UNTIL.put(player.getUUID(), player.level().getGameTime() + 12L * 20L);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 12 * 20, 1));
        return true;
    }
}
