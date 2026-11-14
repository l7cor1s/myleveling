package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.MyLeveling;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Rare soft encounters near players in matching biomes (no hard structure deps).
 */
@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class BossEncounterHooks {
    private BossEncounterHooks() {
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        // Every ~2 minutes per dimension tick alignment
        if (level.getGameTime() % (20L * 120L) != 0L || level.players().isEmpty()) {
            return;
        }
        if (level.random.nextFloat() > 0.35f) {
            return;
        }

        ServerPlayer player = level.players().get(level.random.nextInt(level.players().size()));
        Holder<Biome> biome = level.getBiome(player.blockPosition());
        BossId pick = pickForBiome(biome, level);
        if (pick == null) {
            return;
        }

        // Avoid stacking bosses near the player
        if (!level.getEntitiesOfClass(AbstractRankedBoss.class, player.getBoundingBox().inflate(64.0)).isEmpty()) {
            return;
        }

        BlockPos pos = player.blockPosition().offset(level.random.nextInt(24) - 12, 0, level.random.nextInt(24) - 12);
        pos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
        EntityType<CatalogBossEntity> type = typeFor(pick);
        CatalogBossEntity boss = type.create(level);
        if (boss == null) {
            return;
        }
        boss.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360f, 0);
        boss.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null);
        level.addFreshEntity(boss);
        MyLeveling.LOGGER.debug("Spawned ranked boss {} near {}", pick.id(), player.getGameProfile().getName());
    }

    private static BossId pickForBiome(Holder<Biome> biome, ServerLevel level) {
        boolean nether = level.dimension() == ServerLevel.NETHER;
        boolean end = level.dimension() == ServerLevel.END;

        if (end) {
            return BossId.VOID_REAPER;
        }
        if (nether) {
            BossId[] pool = {BossId.ASH_IMP_LORD, BossId.CRIMSON_SUMMONER, BossId.INFERNAL_BEHEMOTH};
            return pool[level.random.nextInt(pool.length)];
        }
        if (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_DEEP_OCEAN)) {
            return level.random.nextBoolean() ? BossId.REEF_LEVIATHAN : BossId.ABYSS_KNIGHT;
        }
        if (biome.is(BiomeTags.IS_BADLANDS) || biome.is(BiomeTags.IS_SAVANNA)) {
            return level.random.nextBoolean() ? BossId.DUNE_SCORPION : BossId.SANDSTORM_EMPRESS;
        }
        if (biome.is(BiomeTags.IS_FOREST) || biome.is(BiomeTags.IS_TAIGA) || biome.is(BiomeTags.IS_JUNGLE)) {
            return BossId.GROVE_STALKER;
        }
        if (biome.is(BiomeTags.IS_MOUNTAIN) || biome.is(BiomeTags.IS_HILL)) {
            BossId[] pool = {BossId.FROST_HOWLER, BossId.GLACIAL_SOVEREIGN, BossId.THUNDER_COLOSSUS};
            return pool[level.random.nextInt(pool.length)];
        }
        // swamp-ish / river: bog witch; plains/default mid/mini mix
        if (level.random.nextFloat() < 0.25f) {
            return BossId.BOG_WITCH;
        }
        if (level.random.nextFloat() < 0.35f) {
            return BossId.CRYSTAL_ORACLE;
        }
        return BossId.THUNDER_COLOSSUS;
    }

    private static EntityType<CatalogBossEntity> typeFor(BossId id) {
        return switch (id) {
            case GROVE_STALKER -> ModEntities.GROVE_STALKER.get();
            case DUNE_SCORPION -> ModEntities.DUNE_SCORPION.get();
            case FROST_HOWLER -> ModEntities.FROST_HOWLER.get();
            case BOG_WITCH -> ModEntities.BOG_WITCH.get();
            case REEF_LEVIATHAN -> ModEntities.REEF_LEVIATHAN.get();
            case ASH_IMP_LORD -> ModEntities.ASH_IMP_LORD.get();
            case THUNDER_COLOSSUS -> ModEntities.THUNDER_COLOSSUS.get();
            case CRYSTAL_ORACLE -> ModEntities.CRYSTAL_ORACLE.get();
            case CRIMSON_SUMMONER -> ModEntities.CRIMSON_SUMMONER.get();
            case ABYSS_KNIGHT -> ModEntities.ABYSS_KNIGHT.get();
            case SANDSTORM_EMPRESS -> ModEntities.SANDSTORM_EMPRESS.get();
            case VOID_REAPER -> ModEntities.VOID_REAPER.get();
            case INFERNAL_BEHEMOTH -> ModEntities.INFERNAL_BEHEMOTH.get();
            case GLACIAL_SOVEREIGN -> ModEntities.GLACIAL_SOVEREIGN.get();
        };
    }
}
