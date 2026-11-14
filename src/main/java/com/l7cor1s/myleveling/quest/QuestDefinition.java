package com.l7cor1s.myleveling.quest;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public record QuestDefinition(
        String id,
        QuestKind kind,
        int targetCount,
        Optional<String> prerequisiteQuestId,
        Optional<ResourceLocation> targetEntity,
        Optional<ResourceLocation> targetItem,
        int rewardXp,
        int rewardPassivePoints,
        List<Supplier<ItemStack>> rewardItems,
        boolean requiresMca
) {
    public Component title() {
        return Component.translatable("quest.myleveling." + id + ".title");
    }

    public Component description() {
        return Component.translatable("quest.myleveling." + id + ".desc");
    }
}
