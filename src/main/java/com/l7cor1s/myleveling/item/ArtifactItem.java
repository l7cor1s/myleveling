package com.l7cor1s.myleveling.item;

import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Ranked Curios-ready artifact. Works from inventory/offhand if Curios is absent.
 */
public class ArtifactItem extends Item {
    public enum Bonus {
        LUCK, SPEED, GUARD, FURY, VAMP, SYSTEM, XP, MANA
    }

    private final Rank rank;
    private final Bonus bonus;

    public ArtifactItem(Properties properties, Rank rank, Bonus bonus) {
        super(properties);
        this.rank = rank;
        this.bonus = bonus;
    }

    public Rank rank() {
        return rank;
    }

    public Bonus bonus() {
        return bonus;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("lore.myleveling.rank", rank.getDisplayName()).withColor(RankGear.color(rank)));
        tooltip.add(Component.translatable("lore.myleveling.artifact." + bonus.name().toLowerCase()));
        tooltip.add(Component.translatable("lore.myleveling.artifact.curios"));
    }
}
