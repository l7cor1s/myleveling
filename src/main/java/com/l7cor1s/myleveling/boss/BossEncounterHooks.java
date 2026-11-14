package com.l7cor1s.myleveling.boss;

import com.l7cor1s.myleveling.MyLeveling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class BossEncounterHooks {
    private BossEncounterHooks() {
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (level.getGameTime() % (20L * 120L) != 0L || level.players().isEmpty()) {
            return;
        }
        if (level.random.nextFloat() > 0.35f) {
            return;
        }

        ServerPlayer player = level.players().get(level.random.nextInt(level.players().size()));
        if (!level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(64.0),
                m -> m instanceof RankedBoss || m instanceof Kv2BossEntity).isEmpty()) {
            return;
        }

        Holder<Biome> biome = level.getBiome(player.blockPosition());
        BlockPos pos = player.blockPosition().offset(level.random.nextInt(24) - 12, 0, level.random.nextInt(24) - 12);
        pos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);

        if (level.dimension() == ServerLevel.OVERWORLD && !biome.is(BiomeTags.IS_OCEAN) && level.random.nextFloat() < 0.12f) {
            spawnMob(level, ModEntities.KV2.get(), pos);
            return;
        }

        BossId pick = pickForBiome(biome, level);
        spawnMob(level, ModEntities.typeFor(pick), pos);
        MyLeveling.LOGGER.debug("Spawned ranked boss {} near {}", pick.id(), player.getGameProfile().getName());
    }

    private static void spawnMob(ServerLevel level, EntityType<? extends Mob> type, BlockPos pos) {
        Mob mob = type.create(level);
        if (mob == null) {
            return;
        }
        mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.random.nextFloat() * 360f, 0);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null);
        level.addFreshEntity(mob);
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
        if (level.random.nextFloat() < 0.25f) {
            return BossId.BOG_WITCH;
        }
        if (level.random.nextFloat() < 0.35f) {
            return BossId.CRYSTAL_ORACLE;
        }
        return BossId.THUNDER_COLOSSUS;
    }
}
