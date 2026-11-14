package com.l7cor1s.myleveling.item;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.compat.CuriosCompat;
import com.l7cor1s.myleveling.network.ProgressSync;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies artifact bonuses from Curios slots (if present) or inventory/offhand fallback.
 */
@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class ArtifactEffects {
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "artifact_speed");
    private static final ResourceLocation DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "artifact_damage");
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "artifact_armor");
    private static final ResourceLocation LUCK_ID = ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "artifact_luck");

    private ArtifactEffects() {
    }

    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        List<ArtifactItem> arts = equippedArtifacts(player);
        clearModifiers(player);

        for (ArtifactItem art : arts) {
            switch (art.bonus()) {
                case SPEED -> addModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), SPEED_ID, 0.04 + art.rank().ordinal() * 0.01, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                case FURY -> addModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), DAMAGE_ID, 0.06 + art.rank().ordinal() * 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                case GUARD -> addModifier(player.getAttribute(Attributes.ARMOR), ARMOR_ID, 1.0 + art.rank().ordinal(), AttributeModifier.Operation.ADD_VALUE);
                case LUCK -> addModifier(player.getAttribute(Attributes.LUCK), LUCK_ID, 0.5 + art.rank().ordinal() * 0.25, AttributeModifier.Operation.ADD_VALUE);
                case SYSTEM -> {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, true, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false, false));
                }
                case MANA -> {
                    if (player instanceof ServerPlayer serverPlayer && player.tickCount % 40 == 0) {
                        PlayerProgress progress = PlayerSkillData.get(serverPlayer);
                        PlayerSkillData.set(serverPlayer, progress.withMana(Math.min(PlayerProgress.BASE_MAX_MANA + 30.0f, progress.mana() + 1.5f)));
                        ProgressSync.sendTo(serverPlayer);
                    }
                }
                case XP -> {
                    // handled in MobKillXpHandler via hasBonus
                }
                case VAMP -> {
                    // handled on hit
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        for (ArtifactItem art : equippedArtifacts(player)) {
            if (art.bonus() == ArtifactItem.Bonus.VAMP) {
                player.heal(event.getNewDamage() * (0.04f + art.rank().ordinal() * 0.01f));
            }
        }
    }

    public static boolean hasXpBonus(Player player) {
        return equippedArtifacts(player).stream().anyMatch(a -> a.bonus() == ArtifactItem.Bonus.XP);
    }

    public static float xpMultiplier(Player player) {
        float mult = 1.0f;
        for (ArtifactItem art : equippedArtifacts(player)) {
            if (art.bonus() == ArtifactItem.Bonus.XP) {
                mult += 0.08f + art.rank().ordinal() * 0.02f;
            }
        }
        return mult;
    }

    public static List<ArtifactItem> equippedArtifacts(Player player) {
        List<ArtifactItem> list = new ArrayList<>();
        for (ItemStack stack : CuriosCompat.equippedCurios(player)) {
            if (stack.getItem() instanceof ArtifactItem art) {
                list.add(art);
            }
        }
        // Fallback / dual: inventory + offhand (works without Curios)
        if (player.getOffhandItem().getItem() instanceof ArtifactItem art) {
            list.add(art);
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ArtifactItem artifactItem) {
                // Only count one of each bonus from inventory to avoid stacking 36 artifacts
                boolean exists = list.stream().anyMatch(a -> a.bonus() == artifactItem.bonus());
                if (!exists) {
                    list.add(artifactItem);
                }
            }
        }
        return list;
    }

    private static void clearModifiers(Player player) {
        remove(player.getAttribute(Attributes.MOVEMENT_SPEED), SPEED_ID);
        remove(player.getAttribute(Attributes.ATTACK_DAMAGE), DAMAGE_ID);
        remove(player.getAttribute(Attributes.ARMOR), ARMOR_ID);
        remove(player.getAttribute(Attributes.LUCK), LUCK_ID);
    }

    private static void remove(AttributeInstance attr, ResourceLocation id) {
        if (attr != null) {
            attr.removeModifier(id);
        }
    }

    private static void addModifier(AttributeInstance attr, ResourceLocation id, double amount, AttributeModifier.Operation op) {
        if (attr == null) {
            return;
        }
        attr.addTransientModifier(new AttributeModifier(id, amount, op));
    }
}
