package com.l7cor1s.myleveling.event;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class MobKillXpHandler {
    private MobKillXpHandler() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide()) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) {
            return;
        }

        // Players do not grant leveling XP (PvP out of scope for stage 1).
        if (victim instanceof Player) {
            return;
        }

        int xp = calculateXp(victim);
        if (xp <= 0) {
            return;
        }

        PlayerRankData.addXp(killer, xp);
    }

    /**
     * Scales with mob toughness: roughly half of max health, at least 1.
     * Bosses and tanky mobs therefore award more XP.
     */
    private static int calculateXp(LivingEntity victim) {
        return Math.max(1, Math.round(victim.getMaxHealth() / 2.0f));
    }
}
