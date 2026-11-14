package com.l7cor1s.myleveling.effect;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.skill.PassiveAttributeEffects;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class PlayerTickHandler {
    private PlayerTickHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel && player.tickCount % 20 == 0) {
            ActiveSkillExecutor.tickTempEntities(serverLevel);
        }

        if (player.tickCount % 20 != 0) {
            return;
        }

        PlayerProgress progress = PlayerSkillData.get(player);
        float maxMana = PassiveAttributeEffects.maxMana(progress);
        float regen = PassiveAttributeEffects.manaRegenPerSecond(progress);
        if (progress.hasPassive("magic_s_low_hp_regen") && player.getHealth() / player.getMaxHealth() <= 0.35f) {
            regen *= 2.0f;
            player.heal(0.5f);
        }

        float newMana = Math.min(maxMana, progress.mana() + regen);
        boolean changed = Math.abs(newMana - progress.mana()) > 0.01f;

        if (progress.hasPassive("surv_d_regen") || progress.hasPassive("surv_a_fast_regen") || progress.hasPassive("surv_s_strong_regen")) {
            float heal = progress.hasPassive("surv_s_strong_regen") ? 0.6f
                    : progress.hasPassive("surv_a_fast_regen") ? 0.4f : 0.2f;
            player.heal(heal);
        }

        if (progress.hasPassive("surv_b_food_regen")) {
            FoodData food = player.getFoodData();
            if (food.getFoodLevel() < 20 && player.tickCount % 40 == 0) {
                food.setFoodLevel(Math.min(20, food.getFoodLevel() + 1));
            }
        }

        if (progress.hasPassive("eng_c_auto_repair") && player.tickCount % 100 == 0) {
            var stack = player.getMainHandItem();
            if (stack.isDamageableItem() && stack.isDamaged()) {
                stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
            }
        }

        // Mild resistance aura for survival resistance passive is attribute-based.
        if (progress.hasPassive("eng_b_fuel_save") && player.isOnFire() && player.getRandom().nextFloat() < 0.25f) {
            player.clearFire();
        }

        if (changed) {
            PlayerSkillData.set(player, progress.withMana(newMana));
            // Avoid spamming full sync every second: sync mana every 5s.
            if (player.tickCount % 100 == 0) {
                com.l7cor1s.myleveling.network.ProgressSync.sendTo(player);
            }
        }
    }
}
