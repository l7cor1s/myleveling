package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.item.ModItems;
import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class BossLootHelper {
    private BossLootHelper() {
    }

    public static void dropStandard(Mob boss, BossId id, RandomSource random) {
        BossTier tier = id.tier();
        // Guaranteed mid loot for the tier floor
        spawn(boss, pickGuaranteed(tier));

        float highChance = switch (tier) {
            case MINI -> 0.25f;
            case MID -> 0.45f;
            case LARGE -> 0.75f;
        };
        if (random.nextFloat() < highChance) {
            spawn(boss, pickHigh(tier, random));
        }
        if (tier == BossTier.LARGE && random.nextFloat() < 0.55f) {
            spawn(boss, ModItems.GATE_KEY.get().getDefaultInstance());
        }
        if (tier == BossTier.MID && random.nextFloat() < 0.20f) {
            spawn(boss, ModItems.GATE_KEY.get().getDefaultInstance());
        }

        ItemStack unique = uniqueFor(id);
        if (!unique.isEmpty()) {
            spawn(boss, unique);
        }
        if (random.nextFloat() < (tier == BossTier.LARGE ? 0.55f : tier == BossTier.MID ? 0.35f : 0.20f)) {
            spawn(boss, pickArtifact(tier, random));
        }
    }

    private static ItemStack pickGuaranteed(BossTier tier) {
        return switch (tier) {
            case MINI -> stack(ModItems.RANK_D_BLADE);
            case MID -> stack(ModItems.RANK_B_BLADE);
            case LARGE -> stack(ModItems.RANK_A_BLADE);
        };
    }

    private static ItemStack pickHigh(BossTier tier, RandomSource random) {
        List<Supplier<ItemStack>> pool = new ArrayList<>();
        switch (tier) {
            case MINI -> {
                pool.add(() -> stack(ModItems.RANK_D_RING));
                pool.add(() -> stack(ModItems.RANK_C_BLADE));
                pool.add(() -> stack(ModItems.RANK_C_BOW));
                pool.add(() -> stack(ModItems.RANK_D_CHEST));
                pool.add(() -> stack(ModItems.RANK_D_HELM));
                pool.add(() -> stack(ModItems.RANK_E_BOW));
                pool.add(() -> stack(ModItems.RANK_D_AXE));
            }
            case MID -> {
                pool.add(() -> stack(ModItems.RANK_B_CHEST));
                pool.add(() -> stack(ModItems.RANK_A_BLADE));
                pool.add(() -> stack(ModItems.RANK_A_BOW));
                pool.add(() -> stack(ModItems.RANK_A_AMULET));
                pool.add(() -> stack(ModItems.RANK_C_AMULET));
                pool.add(() -> stack(ModItems.RANK_B_AXE));
                pool.add(() -> stack(ModItems.RANK_B_HELM));
                pool.add(() -> stack(ModItems.RANK_B_LEGS));
                pool.add(() -> stack(ModItems.RANK_B_BOOTS));
            }
            case LARGE -> {
                pool.add(() -> stack(ModItems.RANK_S_BLADE));
                pool.add(() -> stack(ModItems.RANK_S_CHEST));
                pool.add(() -> stack(ModItems.RANK_S_HAMMER));
                pool.add(() -> stack(ModItems.RANK_S_CROWN));
                pool.add(() -> stack(ModItems.RANK_A_AMULET));
                pool.add(() -> stack(ModItems.RANK_S_BOW));
                pool.add(() -> stack(ModItems.RANK_S_HELM));
                pool.add(() -> stack(ModItems.RANK_S_LEGS));
                pool.add(() -> stack(ModItems.RANK_S_BOOTS));
                pool.add(() -> stack(ModItems.RANK_S_RING));
            }
        }
        return pool.get(random.nextInt(pool.size())).get();
    }

    private static ItemStack pickArtifact(BossTier tier, RandomSource random) {
        List<Supplier<ItemStack>> pool = new ArrayList<>();
        switch (tier) {
            case MINI -> {
                pool.add(() -> stack(ModItems.ARTIFACT_E_LUCK));
                pool.add(() -> stack(ModItems.ARTIFACT_D_SPEED));
                pool.add(() -> stack(ModItems.ARTIFACT_C_GUARD));
                pool.add(() -> stack(ModItems.ARTIFACT_C_XP));
            }
            case MID -> {
                pool.add(() -> stack(ModItems.ARTIFACT_C_GUARD));
                pool.add(() -> stack(ModItems.ARTIFACT_B_FURY));
                pool.add(() -> stack(ModItems.ARTIFACT_A_MANA));
                pool.add(() -> stack(ModItems.ARTIFACT_C_XP));
            }
            case LARGE -> {
                pool.add(() -> stack(ModItems.ARTIFACT_A_VAMP));
                pool.add(() -> stack(ModItems.ARTIFACT_A_MANA));
                pool.add(() -> stack(ModItems.ARTIFACT_S_SYSTEM));
                pool.add(() -> stack(ModItems.ARTIFACT_B_FURY));
            }
        }
        return pool.get(random.nextInt(pool.size())).get();
    }

    private static ItemStack uniqueFor(BossId id) {
        return switch (id) {
            case GROVE_STALKER -> stack(ModItems.GROVE_FANG);
            case DUNE_SCORPION -> stack(ModItems.SCORPION_STINGER);
            case FROST_HOWLER -> stack(ModItems.FROST_CORE);
            case BOG_WITCH -> stack(ModItems.WITCH_GRIMOIRE);
            case REEF_LEVIATHAN -> stack(ModItems.REEF_PEARL);
            case ASH_IMP_LORD -> stack(ModItems.ASH_EMBER);
            case THUNDER_COLOSSUS -> stack(ModItems.THUNDER_CORE);
            case CRYSTAL_ORACLE -> stack(ModItems.ORACLE_SHARD);
            case CRIMSON_SUMMONER -> stack(ModItems.CRIMSON_SIGIL);
            case ABYSS_KNIGHT -> stack(ModItems.ABYSS_PLATE);
            case SANDSTORM_EMPRESS -> stack(ModItems.SANDSTORM_VEIL);
            case VOID_REAPER -> stack(ModItems.VOID_SCYTHE);
            case INFERNAL_BEHEMOTH -> stack(ModItems.BEHEMOTH_HEART);
            case GLACIAL_SOVEREIGN -> stack(ModItems.GLACIAL_CROWN);
        };
    }

    private static ItemStack stack(DeferredItem<Item> item) {
        return item.get().getDefaultInstance();
    }

    private static void spawn(Mob boss, ItemStack stack) {
        if (!stack.isEmpty()) {
            boss.spawnAtLocation(stack);
        }
    }

    public static Rank suggestedRank(BossTier tier, RandomSource random) {
        return switch (tier) {
            case MINI -> random.nextBoolean() ? Rank.D : Rank.C;
            case MID -> random.nextBoolean() ? Rank.B : Rank.A;
            case LARGE -> Rank.S;
        };
    }
}
