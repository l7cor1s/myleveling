package com.l7cor1s.myleveling.gate;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MyLeveling.MOD_ID);

    public static final DeferredBlock<Block> RANK_GATE = BLOCKS.register("rank_gate",
            () -> new RankGateBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(8.0f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.AMETHYST)
                    .lightLevel(s -> 8)));

    public static final DeferredItem<Item> RANK_GATE_ITEM = ModItems.ITEMS.register("rank_gate",
            () -> new BlockItem(RANK_GATE.get(), new Item.Properties()));

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
