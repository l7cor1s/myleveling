package com.l7cor1s.myleveling.command;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.rank.Rank;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import com.l7cor1s.myleveling.skill.Skill;
import com.l7cor1s.myleveling.skill.SkillBranch;
import com.l7cor1s.myleveling.skill.Skills;
import com.l7cor1s.myleveling.xp.MobTier;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = MyLeveling.MOD_ID)
public final class ModCommands {
    private static final SuggestionProvider<CommandSourceStack> SKILL_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggest(Skills.all().stream().map(Skill::id), builder);

    private ModCommands() {
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

        dispatcher.register(
                Commands.literal("skills")
                        .executes(ctx -> listSkills(ctx.getSource(), null))
                        .then(Commands.literal("list")
                                .executes(ctx -> listSkills(ctx.getSource(), null))
                                .then(Commands.argument("branch", StringArgumentType.word())
                                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                                java.util.Arrays.stream(SkillBranch.values()).map(b -> b.getId()),
                                                builder
                                        ))
                                        .executes(ctx -> listSkills(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "branch")
                                        ))))
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .suggests(SKILL_SUGGESTIONS)
                                        .executes(ctx -> unlockSkill(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "skill")
                                        ))))
                        .then(Commands.literal("info")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .suggests(SKILL_SUGGESTIONS)
                                        .executes(ctx -> skillInfo(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "skill")
                                        ))))
        );
    }

    private static int showRank(CommandSourceStack source) {
        ServerPlayer player = requirePlayer(source);
        if (player == null) {
            return 0;
        }

        int xp = PlayerRankData.getXp(player);
        Rank rank = Rank.fromXp(xp);
        int toNext = Rank.xpToNextRank(xp);
        int points = PlayerSkillData.getSkillPoints(player);

        if (rank.isMax()) {
            source.sendSuccess(
                    () -> Component.translatable(
                            "command.myleveling.rank.max",
                            rank.getDisplayName(),
                            xp,
                            points
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
                            toNext,
                            points
                    ),
                    false
            );
        }

        source.sendSuccess(
                () -> Component.translatable(
                        "command.myleveling.rank.tiers",
                        MobTier.NORMAL.getBaseXp(),
                        MobTier.STRONG.getBaseXp(),
                        MobTier.BOSS.getBaseXp()
                ),
                false
        );

        return Command.SINGLE_SUCCESS;
    }

    private static int listSkills(CommandSourceStack source, String branchId) {
        ServerPlayer player = requirePlayer(source);
        if (player == null) {
            return 0;
        }

        source.sendSuccess(
                () -> Component.translatable(
                        "command.myleveling.skills.header",
                        PlayerSkillData.getSkillPoints(player)
                ),
                false
        );

        for (SkillBranch branch : SkillBranch.values()) {
            if (branchId != null && !branch.getId().equalsIgnoreCase(branchId)) {
                continue;
            }

            source.sendSuccess(() -> Component.literal("").append(branch.getDisplayName()).append(":"), false);
            for (Skill skill : Skills.byBranch(branch)) {
                boolean unlocked = PlayerSkillData.hasSkill(player, skill.id());
                String status = unlocked ? "✔" : "·";
                source.sendSuccess(
                        () -> Component.translatable(
                                "command.myleveling.skills.entry",
                                status,
                                skill.id(),
                                skill.cost(),
                                skill.minRank().getDisplayName()
                        ),
                        false
                );
            }
        }

        source.sendSuccess(() -> Component.translatable("command.myleveling.skills.hint"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int unlockSkill(CommandSourceStack source, String skillId) {
        ServerPlayer player = requirePlayer(source);
        if (player == null) {
            return 0;
        }

        var skillOpt = Skills.byId(skillId);
        if (skillOpt.isEmpty()) {
            source.sendFailure(Component.translatable("command.myleveling.skills.unknown", skillId));
            return 0;
        }

        Skill skill = skillOpt.get();
        return switch (PlayerSkillData.tryUnlock(player, skill)) {
            case SUCCESS -> Command.SINGLE_SUCCESS;
            case ALREADY_UNLOCKED -> {
                source.sendFailure(Component.translatable("command.myleveling.skills.already", skill.id()));
                yield 0;
            }
            case NOT_ENOUGH_POINTS -> {
                source.sendFailure(Component.translatable(
                        "command.myleveling.skills.no_points",
                        skill.cost(),
                        PlayerSkillData.getSkillPoints(player)
                ));
                yield 0;
            }
            case RANK_TOO_LOW -> {
                source.sendFailure(Component.translatable(
                        "command.myleveling.skills.rank_low",
                        skill.minRank().getDisplayName()
                ));
                yield 0;
            }
            case MISSING_PREREQUISITE -> {
                source.sendFailure(Component.translatable(
                        "command.myleveling.skills.need_prereq",
                        skill.prerequisite().orElse("?")
                ));
                yield 0;
            }
        };
    }

    private static int skillInfo(CommandSourceStack source, String skillId) {
        ServerPlayer player = requirePlayer(source);
        if (player == null) {
            return 0;
        }

        var skillOpt = Skills.byId(skillId);
        if (skillOpt.isEmpty()) {
            source.sendFailure(Component.translatable("command.myleveling.skills.unknown", skillId));
            return 0;
        }

        Skill skill = skillOpt.get();
        boolean unlocked = PlayerSkillData.hasSkill(player, skill.id());
        source.sendSuccess(() -> skill.getDisplayName(), false);
        source.sendSuccess(() -> skill.getDescription(), false);
        source.sendSuccess(
                () -> Component.translatable(
                        "command.myleveling.skills.info_meta",
                        skill.branch().getDisplayName(),
                        skill.cost(),
                        skill.minRank().getDisplayName(),
                        skill.prerequisite().orElse("-"),
                        unlocked
                ),
                false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static ServerPlayer requirePlayer(CommandSourceStack source) {
        try {
            return source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.myleveling.rank.players_only"));
            return null;
        }
    }
}
