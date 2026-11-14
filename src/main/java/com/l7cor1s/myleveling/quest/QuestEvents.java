package com.l7cor1s.myleveling.quest;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.boss.AbstractRankedBoss;
import com.l7cor1s.myleveling.boss.GateMonarchEntity;
import com.l7cor1s.myleveling.boss.RankWardenEntity;
import com.l7cor1s.myleveling.boss.ShadowScoutEntity;
import com.l7cor1s.myleveling.tag.ModEntityTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class QuestEvents {
    private QuestEvents() {
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) {
            return;
        }
        PlayerQuestData.onMobKilled(player, victim);
        boolean rankedBoss = victim instanceof AbstractRankedBoss
                || victim instanceof ShadowScoutEntity
                || victim instanceof RankWardenEntity
                || victim instanceof GateMonarchEntity
                || victim.getType().is(ModEntityTags.BOSSES);
        if (rankedBoss) {
            PlayerQuestData.onBossKilled(player, BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()));
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        PlayerQuestData.onBlockMined(player, BuiltInRegistries.BLOCK.getKey(event.getState().getBlock()));
    }

    @SubscribeEvent
    public static void onVillager(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getTarget() instanceof AbstractVillager) {
            PlayerQuestData.onVillagerInteract(player);
        }
    }
}
