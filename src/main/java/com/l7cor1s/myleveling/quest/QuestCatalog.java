package com.l7cor1s.myleveling.quest;

import com.l7cor1s.myleveling.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class QuestCatalog {
    private static final Map<String, QuestDefinition> BY_ID = new LinkedHashMap<>();

    static {
        // Daily
        add(daily("daily_zombies", 8, ResourceLocation.withDefaultNamespace("zombie"), 120, 0));
        add(daily("daily_skeletons", 8, ResourceLocation.withDefaultNamespace("skeleton"), 120, 0));
        add(daily("daily_mine_iron", 16, null, 100, 0, ResourceLocation.withDefaultNamespace("iron_ore")));

        // Mob / gather repeatable
        add(new QuestDefinition("hunt_spiders", QuestKind.MOB, 12,
                Optional.empty(), Optional.of(ResourceLocation.withDefaultNamespace("spider")), Optional.empty(),
                180, 0, List.of(() -> new ItemStack(Items.STRING, 4)), false));
        add(new QuestDefinition("gather_coal", QuestKind.GATHER, 24,
                Optional.empty(), Optional.empty(), Optional.of(ResourceLocation.withDefaultNamespace("coal")),
                140, 0, List.of(() -> new ItemStack(Items.TORCH, 8)), false));
        add(new QuestDefinition("clear_raiders", QuestKind.REPEATABLE, 6,
                Optional.empty(), Optional.of(ResourceLocation.withDefaultNamespace("pillager")), Optional.empty(),
                220, 1, List.of(() -> new ItemStack(Items.EMERALD, 2)), false));

        // Chain
        add(new QuestDefinition("chain_awakening_1", QuestKind.CHAIN, 5,
                Optional.empty(), Optional.of(ResourceLocation.withDefaultNamespace("zombie")), Optional.empty(),
                200, 1, List.of(() -> new ItemStack(ModItems.RANK_E_BLADE.get())), false));
        add(new QuestDefinition("chain_awakening_2", QuestKind.CHAIN, 1,
                Optional.of("chain_awakening_1"), Optional.of(ResourceLocation.fromNamespaceAndPath("myleveling", "shadow_scout")), Optional.empty(),
                400, 1, List.of(() -> new ItemStack(ModItems.RANK_D_RING.get())), false));
        add(new QuestDefinition("chain_awakening_3", QuestKind.CHAIN, 1,
                Optional.of("chain_awakening_2"), Optional.of(ResourceLocation.fromNamespaceAndPath("myleveling", "rank_warden")), Optional.empty(),
                800, 2, List.of(() -> new ItemStack(ModItems.RANK_C_BOW.get()), () -> new ItemStack(ModItems.ARTIFACT_C_XP.get())), false));

        // Boss
        add(new QuestDefinition("boss_scout", QuestKind.BOSS, 1,
                Optional.empty(), Optional.of(ResourceLocation.fromNamespaceAndPath("myleveling", "shadow_scout")), Optional.empty(),
                500, 1, List.of(() -> new ItemStack(ModItems.RANK_D_CHEST.get()), () -> new ItemStack(ModItems.ARTIFACT_D_SPEED.get())), false));
        add(new QuestDefinition("boss_warden", QuestKind.BOSS, 1,
                Optional.of("boss_scout"), Optional.of(ResourceLocation.fromNamespaceAndPath("myleveling", "rank_warden")), Optional.empty(),
                1200, 2, List.of(() -> new ItemStack(ModItems.RANK_B_BLADE.get()), () -> new ItemStack(ModItems.ARTIFACT_B_FURY.get())), false));
        add(new QuestDefinition("boss_monarch", QuestKind.BOSS, 1,
                Optional.of("boss_warden"), Optional.of(ResourceLocation.fromNamespaceAndPath("myleveling", "gate_monarch")), Optional.empty(),
                5000, 3, List.of(() -> new ItemStack(ModItems.RANK_S_CROWN.get()), () -> new ItemStack(ModItems.ARTIFACT_S_SYSTEM.get())), false));

        // Villager / MCA soft
        add(new QuestDefinition("villager_trade", QuestKind.VILLAGER, 3,
                Optional.empty(), Optional.empty(), Optional.empty(),
                160, 1, List.of(() -> new ItemStack(Items.BREAD, 8), () -> new ItemStack(ModItems.ARTIFACT_E_LUCK.get())), false));
        add(new QuestDefinition("mca_help", QuestKind.VILLAGER, 1,
                Optional.empty(), Optional.empty(), Optional.empty(),
                300, 1, List.of(() -> new ItemStack(ModItems.RANK_E_RING.get()), () -> new ItemStack(ModItems.ARTIFACT_C_GUARD.get())), true));
    }

    private QuestCatalog() {
    }

    public static void bootstrap() {
        all();
    }

    public static Collection<QuestDefinition> all() {
        return BY_ID.values();
    }

    public static Optional<QuestDefinition> byId(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public static List<QuestDefinition> availableFor(com.l7cor1s.myleveling.progress.PlayerProgress progress, boolean mcaLoaded) {
        List<QuestDefinition> list = new ArrayList<>();
        for (QuestDefinition q : BY_ID.values()) {
            if (q.requiresMca() && !mcaLoaded) {
                continue;
            }
            if (progress.completedQuests().contains(q.id()) && q.kind() != QuestKind.DAILY && q.kind() != QuestKind.REPEATABLE) {
                continue;
            }
            if (q.prerequisiteQuestId().isPresent() && !progress.completedQuests().contains(q.prerequisiteQuestId().get())) {
                continue;
            }
            list.add(q);
        }
        return list;
    }

    private static void add(QuestDefinition def) {
        BY_ID.put(def.id(), def);
    }

    private static QuestDefinition daily(String id, int count, ResourceLocation entity, int xp, int pp) {
        return daily(id, count, entity, xp, pp, null);
    }

    private static QuestDefinition daily(String id, int count, ResourceLocation entity, int xp, int pp, ResourceLocation item) {
        return new QuestDefinition(id, QuestKind.DAILY, count,
                Optional.empty(),
                Optional.ofNullable(entity),
                Optional.ofNullable(item),
                xp, pp, List.of(() -> new ItemStack(Items.GOLD_NUGGET, 3)), false);
    }
}
