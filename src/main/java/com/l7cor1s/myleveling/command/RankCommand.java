package com.l7cor1s.myleveling.command;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.rank.Rank;
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
public final class RankCommand {
    private RankCommand() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("rank")
                        .executes(ctx -> showRank(ctx.getSource()))
        );
    }

    private static int showRank(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.myleveling.rank.players_only"));
            return 0;
        }

        int xp = PlayerRankData.getXp(player);
        Rank rank = Rank.fromXp(xp);
        int toNext = Rank.xpToNextRank(xp);

        if (rank.isMax()) {
            source.sendSuccess(
                    () -> Component.translatable(
                            "command.myleveling.rank.max",
                            rank.getDisplayName(),
                            xp
                    ),
                    false
            );
        } else {
            Rank next = rank.next();
            source.sendSuccess(
                    () -> Component.translatable(
                            "command.myleveling.rank.info",
                            rank.getDisplayName(),
                            xp,
                            next.getDisplayName(),
                            toNext
                    ),
                    false
            );
        }

        return Command.SINGLE_SUCCESS;
    }
}
