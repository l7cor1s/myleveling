package com.l7cor1s.myleveling.event;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.xp.MobXpCalculator;
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

        // PvP and MCA/villager kills are excluded from leveling XP.
        if (victim instanceof Player) {
            return;
        }

        int xp = MobXpCalculator.calculate(victim);
        if (xp <= 0) {
            return;
        }

        PlayerRankData.addXp(killer, xp);
    }
}
