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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Ranked loot + Curios artifacts. Textures under assets/myleveling/textures/item.
 */
public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MyLeveling.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MyLeveling.MOD_ID);

    private static final List<DeferredItem<? extends Item>> ALL_TAB = new ArrayList<>();

    // Weapons
    public static final DeferredItem<Item> RANK_E_BLADE = sword("rank_e_blade", Tiers.IRON, 2, -2.4f);
    public static final DeferredItem<Item> RANK_D_BLADE = sword("rank_d_blade", Tiers.IRON, 3, -2.4f);
    public static final DeferredItem<Item> RANK_C_BLADE = sword("rank_c_blade", Tiers.DIAMOND, 3, -2.4f);
    public static final DeferredItem<Item> RANK_B_BLADE = sword("rank_b_blade", Tiers.DIAMOND, 4, -2.3f);
    public static final DeferredItem<Item> RANK_A_BLADE = sword("rank_a_blade", Tiers.NETHERITE, 4, -2.2f);
    public static final DeferredItem<Item> RANK_S_BLADE = sword("rank_s_blade", Tiers.NETHERITE, 6, -2.2f);
    public static final DeferredItem<Item> RANK_D_AXE = axe("rank_d_axe", Tiers.IRON, 5.0f, -3.0f);
    public static final DeferredItem<Item> RANK_B_AXE = axe("rank_b_axe", Tiers.DIAMOND, 6.0f, -3.0f);

    public static final DeferredItem<Item> RANK_E_BOW = bow("rank_e_bow", 384);
    public static final DeferredItem<Item> RANK_C_BOW = bow("rank_c_bow", 512);
    public static final DeferredItem<Item> RANK_B_BOW = bow("rank_b_bow", 640);
    public static final DeferredItem<Item> RANK_A_BOW = bow("rank_a_bow", 768);
    public static final DeferredItem<Item> RANK_S_BOW = bow("rank_s_bow", 896);
    public static final DeferredItem<Item> RANK_S_HAMMER = axe("rank_s_hammer", Tiers.NETHERITE, 7.0f, -3.0f);

    // Armor sets
    public static final DeferredItem<Item> RANK_E_HELM = helm("rank_e_helm", ArmorMaterials.IRON);
    public static final DeferredItem<Item> RANK_E_CHEST = chest("rank_e_chest", ArmorMaterials.IRON);
    public static final DeferredItem<Item> RANK_E_LEGS = legs("rank_e_legs", ArmorMaterials.IRON);
    public static final DeferredItem<Item> RANK_E_BOOTS = boots("rank_e_boots", ArmorMaterials.IRON);

    public static final DeferredItem<Item> RANK_D_HELM = helm("rank_d_helm", ArmorMaterials.IRON);
    public static final DeferredItem<Item> RANK_D_CHEST = chest("rank_d_chest", ArmorMaterials.IRON);
    public static final DeferredItem<Item> RANK_D_LEGS = legs("rank_d_legs", ArmorMaterials.IRON);
    public static final DeferredItem<Item> RANK_D_BOOTS = boots("rank_d_boots", ArmorMaterials.IRON);

    public static final DeferredItem<Item> RANK_C_HELM = helm("rank_c_helm", ArmorMaterials.DIAMOND);
    public static final DeferredItem<Item> RANK_C_CHEST = chest("rank_c_chest", ArmorMaterials.DIAMOND);
    public static final DeferredItem<Item> RANK_C_LEGS = legs("rank_c_legs", ArmorMaterials.DIAMOND);
    public static final DeferredItem<Item> RANK_C_BOOTS = boots("rank_c_boots", ArmorMaterials.DIAMOND);

    public static final DeferredItem<Item> RANK_B_HELM = helm("rank_b_helm", ArmorMaterials.DIAMOND);
    public static final DeferredItem<Item> RANK_B_CHEST = chest("rank_b_chest", ArmorMaterials.DIAMOND);
    public static final DeferredItem<Item> RANK_B_LEGS = legs("rank_b_legs", ArmorMaterials.DIAMOND);
    public static final DeferredItem<Item> RANK_B_BOOTS = boots("rank_b_boots", ArmorMaterials.DIAMOND);

    public static final DeferredItem<Item> RANK_A_HELM = helm("rank_a_helm", ArmorMaterials.NETHERITE);
    public static final DeferredItem<Item> RANK_A_CHEST = chest("rank_a_chest", ArmorMaterials.NETHERITE);
    public static final DeferredItem<Item> RANK_A_LEGS = legs("rank_a_legs", ArmorMaterials.NETHERITE);
    public static final DeferredItem<Item> RANK_A_BOOTS = boots("rank_a_boots", ArmorMaterials.NETHERITE);

    public static final DeferredItem<Item> RANK_S_HELM = helm("rank_s_helm", ArmorMaterials.NETHERITE);
    public static final DeferredItem<Item> RANK_S_CHEST = chest("rank_s_chest", ArmorMaterials.NETHERITE);
    public static final DeferredItem<Item> RANK_S_LEGS = legs("rank_s_legs", ArmorMaterials.NETHERITE);
    public static final DeferredItem<Item> RANK_S_BOOTS = boots("rank_s_boots", ArmorMaterials.NETHERITE);

    // Accessories
    public static final DeferredItem<Item> RANK_E_RING = accessory("rank_e_ring");
    public static final DeferredItem<Item> RANK_D_RING = accessory("rank_d_ring");
    public static final DeferredItem<Item> RANK_C_RING = accessory("rank_c_ring");
    public static final DeferredItem<Item> RANK_B_RING = accessory("rank_b_ring");
    public static final DeferredItem<Item> RANK_A_RING = accessory("rank_a_ring");
    public static final DeferredItem<Item> RANK_S_RING = accessory("rank_s_ring");
    public static final DeferredItem<Item> RANK_E_AMULET = accessory("rank_e_amulet");
    public static final DeferredItem<Item> RANK_C_AMULET = accessory("rank_c_amulet");
    public static final DeferredItem<Item> RANK_B_AMULET = accessory("rank_b_amulet");
    public static final DeferredItem<Item> RANK_A_AMULET = accessory("rank_a_amulet");
    public static final DeferredItem<Item> RANK_S_AMULET = accessory("rank_s_amulet");
    public static final DeferredItem<Item> RANK_S_CROWN = accessory("rank_s_crown");
    public static final DeferredItem<Item> GATE_KEY = tabItem("gate_key", () -> new Item(new Item.Properties().stacksTo(1)));

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
    public static final DeferredItem<Item> KV2_HOWITZER = tabItem("kv2_howitzer",
            () -> new Kv2HowitzerItem(new Item.Properties().stacksTo(1).durability(420)));

    // Curios artifacts
    public static final DeferredItem<Item> ARTIFACT_E_LUCK = artifact("artifact_e_luck", Rank.E, ArtifactItem.Bonus.LUCK);
    public static final DeferredItem<Item> ARTIFACT_D_SPEED = artifact("artifact_d_speed", Rank.D, ArtifactItem.Bonus.SPEED);
    public static final DeferredItem<Item> ARTIFACT_C_GUARD = artifact("artifact_c_guard", Rank.C, ArtifactItem.Bonus.GUARD);
    public static final DeferredItem<Item> ARTIFACT_C_XP = artifact("artifact_c_xp", Rank.C, ArtifactItem.Bonus.XP);
    public static final DeferredItem<Item> ARTIFACT_B_FURY = artifact("artifact_b_fury", Rank.B, ArtifactItem.Bonus.FURY);
    public static final DeferredItem<Item> ARTIFACT_A_VAMP = artifact("artifact_a_vamp", Rank.A, ArtifactItem.Bonus.VAMP);
    public static final DeferredItem<Item> ARTIFACT_A_MANA = artifact("artifact_a_mana", Rank.A, ArtifactItem.Bonus.MANA);
    public static final DeferredItem<Item> ARTIFACT_S_SYSTEM = artifact("artifact_s_system", Rank.S, ArtifactItem.Bonus.SYSTEM);

    public static final Supplier<CreativeModeTab> TAB = TABS.register("myleveling", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.myleveling"))
            .icon(() -> RANK_S_BLADE.get().getDefaultInstance())
            .displayItems((params, out) -> {
                for (DeferredItem<? extends Item> item : ALL_TAB) {
                    out.accept(item.get());
                }
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
        if (path.startsWith("rank_e") || path.startsWith("artifact_e")) return Rank.E;
        if (path.startsWith("rank_d") || path.startsWith("artifact_d") || path.equals("grove_fang") || path.equals("scorpion_stinger")) return Rank.D;
        if (path.startsWith("rank_c") || path.startsWith("artifact_c") || path.equals("frost_core") || path.equals("witch_grimoire")
                || path.equals("reef_pearl") || path.equals("ash_ember")) return Rank.C;
        if (path.startsWith("rank_b") || path.startsWith("artifact_b") || path.equals("thunder_core") || path.equals("crimson_sigil")) return Rank.B;
        if (path.startsWith("rank_a") || path.startsWith("artifact_a") || path.equals("oracle_shard") || path.equals("abyss_plate")
                || path.equals("sandstorm_veil")) return Rank.A;
        if (path.startsWith("rank_s") || path.startsWith("artifact_s") || path.equals("void_scythe") || path.equals("behemoth_heart")
                || path.equals("glacial_crown") || path.equals("gate_key") || path.equals("kv2_howitzer")) return Rank.S;
        return Rank.E;
    }

    public static boolean isArtifact(Item item) {
        return item instanceof ArtifactItem;
    }

    private static DeferredItem<Item> sword(String id, Tiers tier, int dmg, float speed) {
        return tabItem(id, () -> new SwordItem(tier, new Item.Properties()
                .attributes(SwordItem.createAttributes(tier, dmg, speed))));
    }

    private static DeferredItem<Item> axe(String id, Tiers tier, float dmg, float speed) {
        return tabItem(id, () -> new AxeItem(tier, new Item.Properties().attributes(
                net.minecraft.world.item.DiggerItem.createAttributes(tier, dmg, speed))));
    }

    private static DeferredItem<Item> bow(String id, int durability) {
        return tabItem(id, () -> new BowItem(new Item.Properties().durability(durability)));
    }

    private static DeferredItem<Item> chest(String id, Holder<ArmorMaterial> material) {
        return tabItem(id, () -> new ArmorItem(material, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(
                ArmorItem.Type.CHESTPLATE.getDurability(20))));
    }

    private static DeferredItem<Item> helm(String id, Holder<ArmorMaterial> material) {
        return tabItem(id, () -> new ArmorItem(material, ArmorItem.Type.HELMET, new Item.Properties().durability(
                ArmorItem.Type.HELMET.getDurability(20))));
    }

    private static DeferredItem<Item> legs(String id, Holder<ArmorMaterial> material) {
        return tabItem(id, () -> new ArmorItem(material, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(
                ArmorItem.Type.LEGGINGS.getDurability(20))));
    }

    private static DeferredItem<Item> boots(String id, Holder<ArmorMaterial> material) {
        return tabItem(id, () -> new ArmorItem(material, ArmorItem.Type.BOOTS, new Item.Properties().durability(
                ArmorItem.Type.BOOTS.getDurability(20))));
    }

    private static DeferredItem<Item> accessory(String id) {
        return tabItem(id, () -> new Item(new Item.Properties().stacksTo(1).durability(256)));
    }

    private static DeferredItem<Item> artifact(String id, Rank rank, ArtifactItem.Bonus bonus) {
        return tabItem(id, () -> new ArtifactItem(new Item.Properties().stacksTo(1).durability(512), rank, bonus));
    }

    private static DeferredItem<Item> tabItem(String id, Supplier<Item> supplier) {
        DeferredItem<Item> item = ITEMS.register(id, supplier);
        ALL_TAB.add(item);
        return item;
    }
}
