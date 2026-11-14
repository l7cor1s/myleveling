package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.entity.DynamiteProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MyLeveling.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ShadowScoutEntity>> SHADOW_SCOUT =
            ENTITIES.register("shadow_scout", () -> EntityType.Builder.of(ShadowScoutEntity::new, MobCategory.MONSTER)
                    .sized(0.7f, 1.95f).clientTrackingRange(10).build("shadow_scout"));

    public static final DeferredHolder<EntityType<?>, EntityType<RankWardenEntity>> RANK_WARDEN =
            ENTITIES.register("rank_warden", () -> EntityType.Builder.of(RankWardenEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 2.4f).clientTrackingRange(12).build("rank_warden"));

    public static final DeferredHolder<EntityType<?>, EntityType<GateMonarchEntity>> GATE_MONARCH =
            ENTITIES.register("gate_monarch", () -> EntityType.Builder.of(GateMonarchEntity::new, MobCategory.MONSTER)
                    .sized(1.1f, 2.8f).fireImmune().clientTrackingRange(16).build("gate_monarch"));

    public static final DeferredHolder<EntityType<?>, EntityType<ShadowMinionEntity>> SHADOW_MINION =
            ENTITIES.register("shadow_minion", () -> EntityType.Builder.of(ShadowMinionEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.6f).clientTrackingRange(8).build("shadow_minion"));

    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> GROVE_STALKER = catalog("grove_stalker", CatalogBossEntity::grove, 0.7f, 1.9f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> DUNE_SCORPION = catalog("dune_scorpion", CatalogBossEntity::dune, 1.0f, 1.2f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> FROST_HOWLER = catalog("frost_howler", CatalogBossEntity::frost, 0.8f, 1.8f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> BOG_WITCH = catalog("bog_witch", CatalogBossEntity::bog, 0.7f, 1.95f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> REEF_LEVIATHAN = catalog("reef_leviathan", CatalogBossEntity::reef, 1.2f, 1.4f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> ASH_IMP_LORD = catalog("ash_imp_lord", CatalogBossEntity::ash, 0.7f, 1.8f, true);

    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> THUNDER_COLOSSUS = catalog("thunder_colossus", CatalogBossEntity::thunder, 1.2f, 2.8f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> CRYSTAL_ORACLE = catalog("crystal_oracle", CatalogBossEntity::crystal, 0.8f, 2.4f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> CRIMSON_SUMMONER = catalog("crimson_summoner", CatalogBossEntity::crimson, 0.9f, 2.2f, true);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> ABYSS_KNIGHT = catalog("abyss_knight", CatalogBossEntity::abyss, 1.0f, 2.6f, false);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> SANDSTORM_EMPRESS = catalog("sandstorm_empress", CatalogBossEntity::sand, 0.8f, 2.3f, false);

    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> VOID_REAPER = catalog("void_reaper", CatalogBossEntity::voidReaper, 0.9f, 2.5f, true);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> INFERNAL_BEHEMOTH = catalog("infernal_behemoth", CatalogBossEntity::behemoth, 1.4f, 3.0f, true);
    public static final DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> GLACIAL_SOVEREIGN = catalog("glacial_sovereign", CatalogBossEntity::glacial, 1.1f, 2.7f, false);

    public static final DeferredHolder<EntityType<?>, EntityType<Kv2BossEntity>> KV2 =
            ENTITIES.register("kv2", () -> EntityType.Builder.of(Kv2BossEntity::new, MobCategory.MONSTER)
                    .sized(2.2f, 2.6f).fireImmune().clientTrackingRange(16).build("kv2"));

    public static final DeferredHolder<EntityType<?>, EntityType<DynamiteProjectile>> DYNAMITE_PROJECTILE =
            ENTITIES.register("dynamite_projectile", () -> EntityType.Builder.<DynamiteProjectile>of(DynamiteProjectile::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f).clientTrackingRange(8).updateInterval(10).build("dynamite_projectile"));

    private static final Map<String, DeferredHolder<EntityType<?>, ? extends EntityType<?>>> BY_ALIAS = new LinkedHashMap<>();

    static {
        // aliases filled after holders exist — populated in registerAliases()
    }

    private ModEntities() {
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        registerAliases();
    }

    private static void registerAliases() {
        put("scout", SHADOW_SCOUT);
        put("shadow_scout", SHADOW_SCOUT);
        put("warden", RANK_WARDEN);
        put("rank_warden", RANK_WARDEN);
        put("monarch", GATE_MONARCH);
        put("gate_monarch", GATE_MONARCH);
        put("final", GATE_MONARCH);

        put("grove_stalker", GROVE_STALKER);
        put("grove", GROVE_STALKER);
        put("dune_scorpion", DUNE_SCORPION);
        put("dune", DUNE_SCORPION);
        put("scorpion", DUNE_SCORPION);
        put("frost_howler", FROST_HOWLER);
        put("frost", FROST_HOWLER);
        put("bog_witch", BOG_WITCH);
        put("bog", BOG_WITCH);
        put("witch", BOG_WITCH);
        put("reef_leviathan", REEF_LEVIATHAN);
        put("reef", REEF_LEVIATHAN);
        put("ash_imp_lord", ASH_IMP_LORD);
        put("ash", ASH_IMP_LORD);
        put("imp", ASH_IMP_LORD);

        put("thunder_colossus", THUNDER_COLOSSUS);
        put("thunder", THUNDER_COLOSSUS);
        put("crystal_oracle", CRYSTAL_ORACLE);
        put("crystal", CRYSTAL_ORACLE);
        put("oracle", CRYSTAL_ORACLE);
        put("crimson_summoner", CRIMSON_SUMMONER);
        put("crimson", CRIMSON_SUMMONER);
        put("abyss_knight", ABYSS_KNIGHT);
        put("abyss", ABYSS_KNIGHT);
        put("sandstorm_empress", SANDSTORM_EMPRESS);
        put("sandstorm", SANDSTORM_EMPRESS);
        put("empress", SANDSTORM_EMPRESS);

        put("void_reaper", VOID_REAPER);
        put("void", VOID_REAPER);
        put("reaper", VOID_REAPER);
        put("infernal_behemoth", INFERNAL_BEHEMOTH);
        put("behemoth", INFERNAL_BEHEMOTH);
        put("infernal", INFERNAL_BEHEMOTH);
        put("glacial_sovereign", GLACIAL_SOVEREIGN);
        put("glacial", GLACIAL_SOVEREIGN);
        put("sovereign", GLACIAL_SOVEREIGN);

        put("kv2", KV2);
        put("kv-2", KV2);
        put("kv_2", KV2);
        put("korben", KV2);
        put("tank", KV2);
    }

    private static void put(String alias, DeferredHolder<EntityType<?>, ? extends EntityType<?>> holder) {
        BY_ALIAS.put(alias, holder);
    }

    public static Optional<EntityType<?>> byAlias(String alias) {
        DeferredHolder<EntityType<?>, ? extends EntityType<?>> holder = BY_ALIAS.get(alias.toLowerCase());
        return holder == null ? Optional.empty() : Optional.of(holder.get());
    }

    public static Iterable<String> aliases() {
        return BY_ALIAS.keySet();
    }

    private static DeferredHolder<EntityType<?>, EntityType<CatalogBossEntity>> catalog(
            String id,
            EntityType.EntityFactory<CatalogBossEntity> factory,
            float width,
            float height,
            boolean fireImmune
    ) {
        return ENTITIES.register(id, () -> {
            EntityType.Builder<CatalogBossEntity> builder = EntityType.Builder.of(factory, MobCategory.MONSTER)
                    .sized(width, height)
                    .clientTrackingRange(14);
            if (fireImmune) {
                builder.fireImmune();
            }
            return builder.build(id);
        });
    }
}
