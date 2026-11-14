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

    // Legacy names → vanilla reskins
    public static final DeferredHolder<EntityType<?>, EntityType<VindicatorRankBoss>> SHADOW_SCOUT =
            ENTITIES.register("shadow_scout", () -> EntityType.Builder.of(VindicatorRankBoss::scout, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(10).build("shadow_scout"));

    public static final DeferredHolder<EntityType<?>, EntityType<RavagerRankBoss>> RANK_WARDEN =
            ENTITIES.register("rank_warden", () -> EntityType.Builder.of(RavagerRankBoss::warden, MobCategory.MONSTER)
                    .sized(1.95f, 2.2f).clientTrackingRange(12).build("rank_warden"));

    public static final DeferredHolder<EntityType<?>, EntityType<WitherSkeletonRankBoss>> GATE_MONARCH =
            ENTITIES.register("gate_monarch", () -> EntityType.Builder.of(WitherSkeletonRankBoss::monarch, MobCategory.MONSTER)
                    .sized(0.7f, 2.4f).fireImmune().clientTrackingRange(16).build("gate_monarch"));

    public static final DeferredHolder<EntityType<?>, EntityType<ShadowMinionEntity>> SHADOW_MINION =
            ENTITIES.register("shadow_minion", () -> EntityType.Builder.of(ShadowMinionEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(8).build("shadow_minion"));

    // Catalog → vanilla reskins
    public static final DeferredHolder<EntityType<?>, EntityType<ZombieRankBoss>> GROVE_STALKER =
            ENTITIES.register("grove_stalker", () -> EntityType.Builder.of(ZombieRankBoss::grove, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(10).build("grove_stalker"));

    public static final DeferredHolder<EntityType<?>, EntityType<CaveSpiderRankBoss>> DUNE_SCORPION =
            ENTITIES.register("dune_scorpion", () -> EntityType.Builder.of(CaveSpiderRankBoss::dune, MobCategory.MONSTER)
                    .sized(0.7f, 0.5f).clientTrackingRange(10).build("dune_scorpion"));

    public static final DeferredHolder<EntityType<?>, EntityType<StrayRankBoss>> FROST_HOWLER =
            ENTITIES.register("frost_howler", () -> EntityType.Builder.of(StrayRankBoss::frost, MobCategory.MONSTER)
                    .sized(0.6f, 1.99f).clientTrackingRange(10).build("frost_howler"));

    public static final DeferredHolder<EntityType<?>, EntityType<WitchRankBoss>> BOG_WITCH =
            ENTITIES.register("bog_witch", () -> EntityType.Builder.of(WitchRankBoss::bog, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(10).build("bog_witch"));

    public static final DeferredHolder<EntityType<?>, EntityType<ElderGuardianRankBoss>> REEF_LEVIATHAN =
            ENTITIES.register("reef_leviathan", () -> EntityType.Builder.of(ElderGuardianRankBoss::reef, MobCategory.MONSTER)
                    .sized(1.9975f, 1.9975f).clientTrackingRange(12).build("reef_leviathan"));

    public static final DeferredHolder<EntityType<?>, EntityType<BlazeRankBoss>> ASH_IMP_LORD =
            ENTITIES.register("ash_imp_lord", () -> EntityType.Builder.of(BlazeRankBoss::ash, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f).fireImmune().clientTrackingRange(10).build("ash_imp_lord"));

    public static final DeferredHolder<EntityType<?>, EntityType<RavagerRankBoss>> THUNDER_COLOSSUS =
            ENTITIES.register("thunder_colossus", () -> EntityType.Builder.of(RavagerRankBoss::thunder, MobCategory.MONSTER)
                    .sized(1.95f, 2.2f).clientTrackingRange(12).build("thunder_colossus"));

    public static final DeferredHolder<EntityType<?>, EntityType<EvokerRankBoss>> CRYSTAL_ORACLE =
            ENTITIES.register("crystal_oracle", () -> EntityType.Builder.of(EvokerRankBoss::crystal, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(12).build("crystal_oracle"));

    public static final DeferredHolder<EntityType<?>, EntityType<PiglinBruteRankBoss>> CRIMSON_SUMMONER =
            ENTITIES.register("crimson_summoner", () -> EntityType.Builder.of(PiglinBruteRankBoss::crimson, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(12).build("crimson_summoner"));

    public static final DeferredHolder<EntityType<?>, EntityType<DrownedRankBoss>> ABYSS_KNIGHT =
            ENTITIES.register("abyss_knight", () -> EntityType.Builder.of(DrownedRankBoss::abyss, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(12).build("abyss_knight"));

    public static final DeferredHolder<EntityType<?>, EntityType<HuskRankBoss>> SANDSTORM_EMPRESS =
            ENTITIES.register("sandstorm_empress", () -> EntityType.Builder.of(HuskRankBoss::sand, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(12).build("sandstorm_empress"));

    public static final DeferredHolder<EntityType<?>, EntityType<EndermanRankBoss>> VOID_REAPER =
            ENTITIES.register("void_reaper", () -> EntityType.Builder.of(EndermanRankBoss::voidReaper, MobCategory.MONSTER)
                    .sized(0.6f, 2.9f).clientTrackingRange(12).build("void_reaper"));

    public static final DeferredHolder<EntityType<?>, EntityType<RavagerRankBoss>> INFERNAL_BEHEMOTH =
            ENTITIES.register("infernal_behemoth", () -> EntityType.Builder.of(RavagerRankBoss::behemoth, MobCategory.MONSTER)
                    .sized(1.95f, 2.2f).fireImmune().clientTrackingRange(14).build("infernal_behemoth"));

    public static final DeferredHolder<EntityType<?>, EntityType<EvokerRankBoss>> GLACIAL_SOVEREIGN =
            ENTITIES.register("glacial_sovereign", () -> EntityType.Builder.of(EvokerRankBoss::glacial, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(14).build("glacial_sovereign"));

    public static final DeferredHolder<EntityType<?>, EntityType<Kv2BossEntity>> KV2 =
            ENTITIES.register("kv2", () -> EntityType.Builder.of(Kv2BossEntity::new, MobCategory.MONSTER)
                    .sized(2.8f, 3.0f).fireImmune().clientTrackingRange(16).build("kv2"));

    public static final DeferredHolder<EntityType<?>, EntityType<DynamiteProjectile>> DYNAMITE_PROJECTILE =
            ENTITIES.register("dynamite_projectile", () -> EntityType.Builder.<DynamiteProjectile>of(DynamiteProjectile::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f).clientTrackingRange(8).updateInterval(10).build("dynamite_projectile"));

    private static final Map<String, DeferredHolder<EntityType<?>, ? extends EntityType<?>>> BY_ALIAS = new LinkedHashMap<>();

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

    @SuppressWarnings("unchecked")
    public static EntityType<? extends net.minecraft.world.entity.Mob> typeFor(BossId id) {
        return (EntityType<? extends net.minecraft.world.entity.Mob>) switch (id) {
            case SHADOW_SCOUT -> SHADOW_SCOUT.get();
            case RANK_WARDEN -> RANK_WARDEN.get();
            case GATE_MONARCH -> GATE_MONARCH.get();
            case GROVE_STALKER -> GROVE_STALKER.get();
            case DUNE_SCORPION -> DUNE_SCORPION.get();
            case FROST_HOWLER -> FROST_HOWLER.get();
            case BOG_WITCH -> BOG_WITCH.get();
            case REEF_LEVIATHAN -> REEF_LEVIATHAN.get();
            case ASH_IMP_LORD -> ASH_IMP_LORD.get();
            case THUNDER_COLOSSUS -> THUNDER_COLOSSUS.get();
            case CRYSTAL_ORACLE -> CRYSTAL_ORACLE.get();
            case CRIMSON_SUMMONER -> CRIMSON_SUMMONER.get();
            case ABYSS_KNIGHT -> ABYSS_KNIGHT.get();
            case SANDSTORM_EMPRESS -> SANDSTORM_EMPRESS.get();
            case VOID_REAPER -> VOID_REAPER.get();
            case INFERNAL_BEHEMOTH -> INFERNAL_BEHEMOTH.get();
            case GLACIAL_SOVEREIGN -> GLACIAL_SOVEREIGN.get();
        };
    }
}
