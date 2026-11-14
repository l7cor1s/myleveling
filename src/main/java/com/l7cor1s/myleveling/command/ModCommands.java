package com.l7cor1s.myleveling.command;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.rank.Rank;
import com.l7cor1s.myleveling.xp.MobTier;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class ModCommands {
    private ModCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("rank").executes(ctx -> showRank(ctx.getSource())));
        dispatcher.register(Commands.literal("skills").executes(ctx -> {
            ctx.getSource().sendSuccess(() -> Component.translatable("command.myleveling.skills.hint"), false);
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static int showRank(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.myleveling.rank.players_only"));
            return 0;
        }

        PlayerProgress progress = PlayerRankData.getProgress(player);
        int xp = progress.xp();
        Rank rank = Rank.fromXp(xp);
        int toNext = Rank.xpToNextRank(xp);

        if (rank.isMax()) {
            source.sendSuccess(() -> Component.translatable(
                    "command.myleveling.rank.max",
                    rank.getDisplayName(), xp, progress.skillPoints(), progress.passivePoints()
            ), false);
        } else {
            source.sendSuccess(() -> Component.translatable(
                    "command.myleveling.rank.info",
                    rank.getDisplayName(), xp, rank.next().getDisplayName(), toNext,
                    progress.skillPoints(), progress.passivePoints()
            ), false);
        }
        source.sendSuccess(() -> Component.translatable(
                "command.myleveling.rank.tiers",
                MobTier.NORMAL.getBaseXp(), MobTier.STRONG.getBaseXp(), MobTier.BOSS.getBaseXp()
        ), false);
        return Command.SINGLE_SUCCESS;
    }
}
