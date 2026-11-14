package com.l7cor1s.myleveling.item;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.entity.DynamiteProjectile;
import com.l7cor1s.myleveling.sound.ModSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * KV-2 unique loot: chargeable howitzer that fires dynamite with a WoT-style aim circle.
 */
public class Kv2HowitzerItem extends Item implements GeoItem {
    public static final int MAX_CHARGE = 40;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Kv2HowitzerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<Kv2HowitzerItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) {
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(
                            ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "kv2_howitzer")));
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "howitzer", 5,
                state -> state.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }
        int used = this.getUseDuration(stack, entity) - timeLeft;
        float charge = Mth.clamp(used / (float) MAX_CHARGE, 0.15f, 1.0f);

        if (!level.isClientSide()) {
            DynamiteProjectile shell = new DynamiteProjectile(level, player);
            float spread = (1.0f - charge) * 12.0f;
            float yawJitter = (player.getRandom().nextFloat() - 0.5f) * spread;
            float pitchJitter = (player.getRandom().nextFloat() - 0.5f) * spread;
            shell.shootFromRotation(player, player.getXRot() + pitchJitter, player.getYRot() + yawJitter, 0.0f, 1.1f + charge * 0.5f, 0.5f);
            shell.setExplosionPower(1.8f + charge * 1.6f);
            level.addFreshEntity(shell);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.FIRE_ON_KHUTOR.get(), SoundSource.PLAYERS, 0.9f, 1.0f);
            if (charge > 0.85f) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.PENETRATION.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("lore.myleveling.kv2_howitzer"));
        tooltip.add(Component.translatable("lore.myleveling.kv2_howitzer_aim"));
    }

    /** 0..1 charge progress for HUD aim circle. */
    public static float chargeProgress(Player player) {
        if (!player.isUsingItem() || !(player.getUseItem().getItem() instanceof Kv2HowitzerItem)) {
            return 0f;
        }
        int used = player.getTicksUsingItem();
        return Mth.clamp(used / (float) MAX_CHARGE, 0f, 1f);
    }
}
