package com.l7cor1s.myleveling.command;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.boss.ModEntities;
import com.l7cor1s.myleveling.item.ModItems;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.rank.Rank;
import com.l7cor1s.myleveling.xp.MobTier;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
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
        dispatcher.register(Commands.literal("myleveling")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("spawn")
                        .then(Commands.argument("boss", StringArgumentType.word())
                                .executes(ctx -> spawnBoss(ctx.getSource(), StringArgumentType.getString(ctx, "boss")))))
                .then(Commands.literal("givekey")
                        .executes(ctx -> giveKey(ctx.getSource()))));
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

    private static int spawnBoss(CommandSourceStack source, String boss) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.myleveling.rank.players_only"));
            return 0;
        }
        ServerLevel level = player.serverLevel();
        Entity entity = switch (boss.toLowerCase()) {
            case "scout", "shadow_scout", "d", "c" -> ModEntities.SHADOW_SCOUT.get().create(level);
            case "warden", "rank_warden", "b", "a" -> ModEntities.RANK_WARDEN.get().create(level);
            case "monarch", "gate_monarch", "s", "final" -> ModEntities.GATE_MONARCH.get().create(level);
            default -> null;
        };
        if (entity == null) {
            source.sendFailure(Component.translatable("command.myleveling.unknown_boss"));
            return 0;
        }
        entity.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0);
        level.addFreshEntity(entity);
        EntityType<?> type = entity.getType();
        source.sendSuccess(() -> Component.translatable("command.myleveling.spawned", type.getDescription()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int giveKey(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.myleveling.rank.players_only"));
            return 0;
        }
        player.addItem(new ItemStack(ModItems.GATE_KEY.get()));
        source.sendSuccess(() -> Component.translatable("command.myleveling.gave_key"), true);
        return Command.SINGLE_SUCCESS;
    }
}
