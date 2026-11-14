package com.l7cor1s.myleveling.item;

import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class RankGear {
    private RankGear() {
    }

    public static Component rankName(Rank rank, String key) {
        return Component.translatable(key).withColor(color(rank));
    }

    public static int color(Rank rank) {
        return switch (rank) {
            case E -> 0x9AA4C7;
            case D -> 0x5DFFB0;
            case C -> 0x4FC3F7;
            case B -> 0x7B5CFF;
            case A -> 0xFF6BCB;
            case S -> 0xFFE082;
        };
    }

    public static void appendRankLore(ItemStack stack, Rank rank, String effectKey) {
        // Lore is applied via item name/tooltip events.
    }
}
