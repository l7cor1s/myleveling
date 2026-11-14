package com.l7cor1s.myleveling.item;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class RankLootEffects {
    private RankLootEffects() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if (!isOurs(item)) {
            return;
        }
        Rank rank = ModItems.rankOf(item);
        event.getToolTip().add(Component.translatable("lore.myleveling.rank", rank.getDisplayName()).withColor(RankGear.color(rank)));
        String path = item.builtInRegistryHolder().key().location().getPath();
        event.getToolTip().add(Component.translatable("lore.myleveling." + path));
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack main = player.getMainHandItem();
        if (!isOurs(main.getItem())) {
            return;
        }
        LivingEntity target = event.getEntity();
        RandomSource random = player.getRandom();
        Item item = main.getItem();
        String path = item.builtInRegistryHolder().key().location().getPath();

        if (item instanceof SwordItem) {
            if (path.contains("rank_d") || path.contains("rank_c")) {
                if (random.nextFloat() < 0.20f) {
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0));
                }
            }
            if (path.contains("rank_b") || path.contains("rank_a") || path.contains("rank_s")) {
                player.heal(event.getNewDamage() * (path.contains("rank_s") ? 0.12f : 0.06f));
            }
            if (path.contains("rank_e") && random.nextFloat() < 0.12f) {
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0));
            }
        }
        if (item instanceof BowItem || path.contains("bow")) {
            if (random.nextFloat() < 0.25f) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
            }
            if (path.contains("rank_b") && random.nextFloat() < 0.20f) {
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0));
            }
            if (path.contains("rank_a") || path.contains("rank_s")) {
                event.getEntity().hurt(player.damageSources().magic(), path.contains("rank_s") ? 3.0f : 2.0f);
            }
        }
        if (item instanceof AxeItem) {
            if (path.contains("hammer") || path.contains("rank_b_axe")) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, path.contains("hammer") ? 2 : 1));
            }
            if (path.contains("rank_d_axe") && random.nextFloat() < 0.18f) {
                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0));
            }
        }
        if (path.equals("void_scythe") && random.nextFloat() < 0.30f) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
            player.heal(1.0f);
        }

        // Accessories in offhand / inventory
        if (hasAccessory(player, "rank_e_ring") && random.nextFloat() < 0.08f) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
        }
        if (hasAccessory(player, "rank_d_ring") && random.nextFloat() < 0.10f) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0));
        }
        if (hasAccessory(player, "rank_c_ring") && random.nextFloat() < 0.10f) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0));
        }
        if (hasAccessory(player, "rank_b_ring") && random.nextFloat() < 0.12f) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0));
        }
        if (hasAccessory(player, "rank_a_ring") && random.nextFloat() < 0.12f) {
            player.heal(0.75f);
        }
        if (hasAccessory(player, "rank_s_ring")) {
            player.heal(0.35f);
        }
        if (hasAccessory(player, "rank_e_amulet") && random.nextFloat() < 0.08f) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0));
        }
        if (hasAccessory(player, "rank_c_amulet") && random.nextFloat() < 0.10f) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0));
        }
        if (hasAccessory(player, "rank_b_amulet") && random.nextFloat() < 0.12f) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 60, 0));
        }
        if (hasAccessory(player, "rank_s_amulet") && random.nextFloat() < 0.15f) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1));
        }
        if (hasAccessory(player, "rank_s_crown")) {
            player.heal(0.5f);
        }
        if (hasAccessory(player, "frost_core") && random.nextFloat() < 0.15f) {
            target.setTicksFrozen(Math.min(140, target.getTicksFrozen() + 40));
        }
        if (hasAccessory(player, "ash_ember") && random.nextFloat() < 0.15f) {
            target.igniteForSeconds(2);
        }
        if (hasAccessory(player, "behemoth_heart")) {
            player.heal(0.25f);
        }
        if (hasAccessory(player, "glacial_crown") && random.nextFloat() < 0.12f) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
        }
        if (hasAccessory(player, "thunder_core") && random.nextFloat() < 0.12f) {
            target.hurt(player.damageSources().magic(), 1.5f);
        }
        if (hasAccessory(player, "grove_fang") && random.nextFloat() < 0.15f) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0));
        }
        if (hasAccessory(player, "scorpion_stinger") && random.nextFloat() < 0.15f) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1));
        }
    }

    @SubscribeEvent
    public static void onDefend(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (isOurs(chest.getItem())) {
            String path = chest.getItem().builtInRegistryHolder().key().location().getPath();
            RandomSource random = player.getRandom();
            if ((path.contains("rank_d") || path.contains("rank_c") || path.contains("rank_e")) && random.nextFloat() < 0.12f) {
                event.setAmount(event.getAmount() * 0.5f);
            }
            if ((path.contains("rank_b") || path.contains("rank_a") || path.contains("rank_s") || path.equals("abyss_plate"))
                    && random.nextFloat() < 0.15f) {
                if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                    attacker.hurt(player.damageSources().thorns(player), Math.max(2.0f, event.getAmount() * 0.25f));
                }
            }
        }
        // Full-set bonuses: helm/legs/boots contribute small mitigation
        int pieces = 0;
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            ItemStack stack = player.getItemBySlot(slot);
            if (isOurs(stack.getItem()) && stack.getItem() instanceof ArmorItem) {
                pieces++;
            }
        }
        if (pieces >= 4) {
            event.setAmount(event.getAmount() * 0.92f);
        }
        if (hasAccessory(player, "rank_a_amulet") && player.getHealth() / player.getMaxHealth() < 0.3f) {
            event.setAmount(event.getAmount() * 0.85f);
        }
        if (hasAccessory(player, "sandstorm_veil") && randomChance(player, 0.10f)) {
            event.setAmount(event.getAmount() * 0.8f);
        }
    }

    private static boolean randomChance(ServerPlayer player, float chance) {
        return player.getRandom().nextFloat() < chance;
    }

    private static boolean hasAccessory(ServerPlayer player, String path) {
        ItemStack off = player.getOffhandItem();
        if (isOurs(off.getItem()) && off.getItem().builtInRegistryHolder().key().location().getPath().equals(path)) {
            return true;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isOurs(stack.getItem()) && stack.getItem().builtInRegistryHolder().key().location().getPath().equals(path)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOurs(Item item) {
        return item.builtInRegistryHolder().key().location().getNamespace().equals(MyLeveling.MOD_ID);
    }
}
