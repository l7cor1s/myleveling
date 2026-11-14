package com.l7cor1s.myleveling.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/**
 * Ranked boss reskin of vanilla Zombie.
 */
public class ZombieRankBoss extends Zombie implements RankedBoss {
    private final BossId bossId;
    private final ServerBossEvent bossEvent;
    private final boolean[] announced = {false};
    private final boolean[] enraged = {false};

    public ZombieRankBoss(EntityType<? extends Zombie> type, Level level, BossId bossId) {
        super(type, level);
        this.bossId = bossId;
        this.bossEvent = VanillaBossSupport.createBar(bossId);
        VanillaBossSupport.applyIdentity(this, bossId);
        this.xpReward = bossId.xpReward();
    }

    public static ZombieRankBoss grove(EntityType<ZombieRankBoss> type, Level level) {
        return new ZombieRankBoss(type, level, BossId.GROVE_STALKER);
    }

    public static AttributeSupplier.Builder createAttributes(BossId id) {
        return VanillaBossSupport.attributes(id);
    }

    @Override
    public BossId bossId() {
        return bossId;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        VanillaBossSupport.onSeen(bossEvent, player, this, bossId, announced);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        VanillaBossSupport.onUnseen(bossEvent, player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        VanillaBossSupport.tickBar(bossEvent, this);
        VanillaBossSupport.tickCombat(this, bossId, enraged);
    }

    @Override
    protected void dropCustomDeathLoot(net.minecraft.server.level.ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        VanillaBossSupport.drop(this, bossId, source, recentlyHit);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("MlAnn", announced[0]);
        tag.putBoolean("MlEnr", enraged[0]);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        announced[0] = tag.getBoolean("MlAnn");
        enraged[0] = tag.getBoolean("MlEnr");
        VanillaBossSupport.applyIdentity(this, bossId);
        this.xpReward = bossId.xpReward();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        VanillaBossSupport.clearBar(bossEvent);
    }
}
