package com.l7cor1s.myleveling.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Short-lived summoned helper — vanilla zombie model. */
public class ShadowMinionEntity extends Zombie {
    private static final EntityDataAccessor<Integer> KIND =
            SynchedEntityData.defineId(ShadowMinionEntity.class, EntityDataSerializers.INT);

    public enum Kind {
        SHADOW, FROST, ASH, ABYSS;
        public static Kind byId(int id) {
            Kind[] values = values();
            return id < 0 || id >= values.length ? SHADOW : values[id];
        }
    }

    private int lifeTicks = 20 * 25;

    public ShadowMinionEntity(EntityType<? extends ShadowMinionEntity> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    public void configure(Kind kind, LivingEntity target) {
        this.entityData.set(KIND, kind.ordinal());
        if (target instanceof Player) {
            this.setTarget(target);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(KIND, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && --lifeTicks <= 0) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Life", lifeTicks);
        tag.putInt("Kind", this.entityData.get(KIND));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        lifeTicks = tag.getInt("Life");
        this.entityData.set(KIND, tag.getInt("Kind"));
    }
}
