package com.l7cor1s.myleveling.gate;

import com.l7cor1s.myleveling.boss.WitherSkeletonRankBoss;
import com.l7cor1s.myleveling.boss.ModEntities;
import com.l7cor1s.myleveling.item.ModItems;
import com.l7cor1s.myleveling.rank.PlayerRankData;
import com.l7cor1s.myleveling.rank.Rank;
import com.l7cor1s.myleveling.skill.PlayerSkillData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Solo Leveling style gate: requires Rank S + Gate Key to summon the Monarch.
 */
public class RankGateBlock extends Block {
    public RankGateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return ItemInteractionResult.FAIL;
        }

        if (PlayerSkillData.get(serverPlayer).monarchDefeated()) {
            serverPlayer.displayClientMessage(Component.translatable("message.myleveling.gate_already_cleared"), true);
            return ItemInteractionResult.SUCCESS;
        }

        Rank rank = PlayerRankData.getRank(serverPlayer);
        if (!rank.isAtLeast(Rank.S)) {
            serverPlayer.displayClientMessage(Component.translatable("message.myleveling.gate_need_rank_s"), true);
            return ItemInteractionResult.FAIL;
        }
        if (!stack.is(ModItems.GATE_KEY.get())) {
            serverPlayer.displayClientMessage(Component.translatable("message.myleveling.gate_need_key"), true);
            return ItemInteractionResult.FAIL;
        }

        stack.shrink(1);
        if (level instanceof ServerLevel serverLevel) {
            WitherSkeletonRankBoss boss = ModEntities.GATE_MONARCH.get().create(serverLevel);
            if (boss != null) {
                boss.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, player.getYRot(), 0);
                serverLevel.addFreshEntity(boss);
                serverLevel.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        80, 1.0, 1.5, 1.0, 0.2);
            }
        }
        serverPlayer.sendSystemMessage(Component.translatable("message.myleveling.gate_opened"));
        return ItemInteractionResult.CONSUME;
    }
}
