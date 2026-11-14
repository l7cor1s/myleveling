package com.l7cor1s.myleveling.entity;

import com.l7cor1s.myleveling.boss.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * Thrown dynamite shell for the KV-2 howitzer loot weapon.
 */
public class DynamiteProjectile extends ThrowableItemProjectile {
    private float explosionPower = 2.4f;

    public DynamiteProjectile(EntityType<? extends DynamiteProjectile> type, Level level) {
        super(type, level);
    }

    public DynamiteProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.DYNAMITE_PROJECTILE.get(), shooter, level);
    }

    public void setExplosionPower(float power) {
        this.explosionPower = power;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.TNT;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0.02, 0);
            this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0, 0.01, 0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionPower, false, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }
}
