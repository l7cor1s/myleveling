package com.l7cor1s.myleveling.town;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.compat.CompatMods;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * Soft town enrichment near existing villagers: extra beds / villagers.
 * More frequent when MCA is loaded.
 */
@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class TownHooks {
    private TownHooks() {
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof Villager villager)) {
            return;
        }
        if (CompatMods.isLoaded(CompatMods.MCA) && level.random.nextFloat() < 0.25f) {
            villager.setAge(Math.min(villager.getAge(), 0));
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (level.getGameTime() % (20L * 180L) != 0 || level.players().isEmpty()) {
            return;
        }

        var player = level.players().get(level.random.nextInt(level.players().size()));
        var nearby = level.getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(48.0));
        if (nearby.size() < 2 || nearby.size() > 24) {
            return;
        }

        Villager anchor = nearby.get(level.random.nextInt(nearby.size()));
        BlockPos ground = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                anchor.blockPosition().offset(level.random.nextInt(8) - 4, 0, level.random.nextInt(8) - 4)
        );
        if (!level.getBlockState(ground).isAir() || !level.getBlockState(ground.below()).isSolid()) {
            return;
        }

        float chance = CompatMods.isLoaded(CompatMods.MCA) ? 0.65f : 0.30f;
        if (level.random.nextFloat() < chance) {
            level.setBlock(ground, Blocks.RED_BED.defaultBlockState(), 3);
        }
        if (level.random.nextFloat() < chance * 0.7f) {
            Villager villager = EntityType.VILLAGER.create(level);
            if (villager != null) {
                villager.moveTo(ground.getX() + 0.5, ground.getY() + 1, ground.getZ() + 0.5, 0, 0);
                level.addFreshEntity(villager);
            }
        }
    }
}
