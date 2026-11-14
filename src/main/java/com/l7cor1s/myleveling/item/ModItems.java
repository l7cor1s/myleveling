package com.l7cor1s.myleveling.item;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.rank.Rank;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Ranked loot: vanilla models + strong naming/effects. Effects handled in {@link RankLootEffects}.
 */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MyLeveling.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MyLeveling.MOD_ID);

    // Weapons
    public static final DeferredItem<Item> RANK_E_BLADE = sword("rank_e_blade", Tiers.IRON, 2, -2.4f);
    public static final DeferredItem<Item> RANK_D_BLADE = sword("rank_d_blade", Tiers.IRON, 3, -2.4f);
    public static final DeferredItem<Item> RANK_C_BLADE = sword("rank_c_blade", Tiers.DIAMOND, 3, -2.4f);
    public static final DeferredItem<Item> RANK_B_BLADE = sword("rank_b_blade", Tiers.DIAMOND, 4, -2.3f);
    public static final DeferredItem<Item> RANK_A_BLADE = sword("rank_a_blade", Tiers.NETHERITE, 4, -2.2f);
    public static final DeferredItem<Item> RANK_S_BLADE = sword("rank_s_blade", Tiers.NETHERITE, 6, -2.2f);

    public static final DeferredItem<Item> RANK_C_BOW = ITEMS.register("rank_c_bow", () -> new BowItem(new Item.Properties().durability(512)));
    public static final DeferredItem<Item> RANK_A_BOW = ITEMS.register("rank_a_bow", () -> new BowItem(new Item.Properties().durability(768)));
    public static final DeferredItem<Item> RANK_S_HAMMER = ITEMS.register("rank_s_hammer",
            () -> new AxeItem(Tiers.NETHERITE, new Item.Properties().attributes(
                    net.minecraft.world.item.DiggerItem.createAttributes(Tiers.NETHERITE, 7.0f, -3.0f))));

    // Armor (chestpieces as signature pieces)
    public static final DeferredItem<Item> RANK_D_CHEST = chest("rank_d_chest", ArmorMaterials.IRON);
    public static final DeferredItem<Item> RANK_B_CHEST = chest("rank_b_chest", ArmorMaterials.DIAMOND);
    public static final DeferredItem<Item> RANK_S_CHEST = chest("rank_s_chest", ArmorMaterials.NETHERITE);

    // Accessories (trinkets as durable items kept in inventory / offhand)
    public static final DeferredItem<Item> RANK_E_RING = accessory("rank_e_ring");
    public static final DeferredItem<Item> RANK_D_RING = accessory("rank_d_ring");
    public static final DeferredItem<Item> RANK_C_AMULET = accessory("rank_c_amulet");
    public static final DeferredItem<Item> RANK_A_AMULET = accessory("rank_a_amulet");
    public static final DeferredItem<Item> RANK_S_CROWN = accessory("rank_s_crown");
    public static final DeferredItem<Item> GATE_KEY = ITEMS.register("gate_key", () -> new Item(new Item.Properties().stacksTo(1)));

    // Unique boss drops
    public static final DeferredItem<Item> GROVE_FANG = accessory("grove_fang");
    public static final DeferredItem<Item> SCORPION_STINGER = accessory("scorpion_stinger");
    public static final DeferredItem<Item> FROST_CORE = accessory("frost_core");
    public static final DeferredItem<Item> WITCH_GRIMOIRE = accessory("witch_grimoire");
    public static final DeferredItem<Item> REEF_PEARL = accessory("reef_pearl");
    public static final DeferredItem<Item> ASH_EMBER = accessory("ash_ember");
    public static final DeferredItem<Item> THUNDER_CORE = accessory("thunder_core");
    public static final DeferredItem<Item> ORACLE_SHARD = accessory("oracle_shard");
    public static final DeferredItem<Item> CRIMSON_SIGIL = accessory("crimson_sigil");
    public static final DeferredItem<Item> ABYSS_PLATE = chest("abyss_plate", ArmorMaterials.NETHERITE);
    public static final DeferredItem<Item> SANDSTORM_VEIL = accessory("sandstorm_veil");
    public static final DeferredItem<Item> VOID_SCYTHE = sword("void_scythe", Tiers.NETHERITE, 5, -2.6f);
    public static final DeferredItem<Item> BEHEMOTH_HEART = accessory("behemoth_heart");
    public static final DeferredItem<Item> GLACIAL_CROWN = accessory("glacial_crown");

    public static final Supplier<CreativeModeTab> TAB = TABS.register("myleveling", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.myleveling"))
            .icon(() -> RANK_S_BLADE.get().getDefaultInstance())
            .displayItems((params, out) -> {
                out.accept(RANK_E_BLADE.get());
                out.accept(RANK_D_BLADE.get());
                out.accept(RANK_C_BLADE.get());
                out.accept(RANK_B_BLADE.get());
                out.accept(RANK_A_BLADE.get());
                out.accept(RANK_S_BLADE.get());
                out.accept(RANK_C_BOW.get());
                out.accept(RANK_A_BOW.get());
                out.accept(RANK_S_HAMMER.get());
                out.accept(RANK_D_CHEST.get());
                out.accept(RANK_B_CHEST.get());
                out.accept(RANK_S_CHEST.get());
                out.accept(RANK_E_RING.get());
                out.accept(RANK_D_RING.get());
                out.accept(RANK_C_AMULET.get());
                out.accept(RANK_A_AMULET.get());
                out.accept(RANK_S_CROWN.get());
                out.accept(GATE_KEY.get());
                out.accept(GROVE_FANG.get());
                out.accept(SCORPION_STINGER.get());
                out.accept(FROST_CORE.get());
                out.accept(WITCH_GRIMOIRE.get());
                out.accept(REEF_PEARL.get());
                out.accept(ASH_EMBER.get());
                out.accept(THUNDER_CORE.get());
                out.accept(ORACLE_SHARD.get());
                out.accept(CRIMSON_SIGIL.get());
                out.accept(ABYSS_PLATE.get());
                out.accept(SANDSTORM_VEIL.get());
                out.accept(VOID_SCYTHE.get());
                out.accept(BEHEMOTH_HEART.get());
                out.accept(GLACIAL_CROWN.get());
                out.accept(com.l7cor1s.myleveling.gate.ModBlocks.RANK_GATE_ITEM.get());
            }).build());

    private ModItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }

    public static Rank rankOf(Item item) {
        String path = item.builtInRegistryHolder().key().location().getPath();
        if (path.startsWith("rank_e")) return Rank.E;
        if (path.startsWith("rank_d") || path.equals("grove_fang") || path.equals("scorpion_stinger")) return Rank.D;
        if (path.startsWith("rank_c") || path.equals("frost_core") || path.equals("witch_grimoire")
                || path.equals("reef_pearl") || path.equals("ash_ember")) return Rank.C;
        if (path.startsWith("rank_b") || path.equals("thunder_core") || path.equals("crimson_sigil")) return Rank.B;
        if (path.startsWith("rank_a") || path.equals("oracle_shard") || path.equals("abyss_plate")
                || path.equals("sandstorm_veil")) return Rank.A;
        if (path.startsWith("rank_s") || path.equals("void_scythe") || path.equals("behemoth_heart")
                || path.equals("glacial_crown") || path.equals("gate_key")) return Rank.S;
        return Rank.E;
    }

    private static DeferredItem<Item> sword(String id, Tiers tier, int dmg, float speed) {
        return ITEMS.register(id, () -> new SwordItem(tier, new Item.Properties()
                .attributes(SwordItem.createAttributes(tier, dmg, speed))));
    }

    private static DeferredItem<Item> chest(String id, Holder<ArmorMaterial> material) {
        return ITEMS.register(id, () -> new ArmorItem(material, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(
                ArmorItem.Type.CHESTPLATE.getDurability(20))));
    }

    private static DeferredItem<Item> accessory(String id) {
        return ITEMS.register(id, () -> new Item(new Item.Properties().stacksTo(1).durability(256)));
    }
}
