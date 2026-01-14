package net.cardboard.stellarbound.entity;

import net.cardboard.stellarbound.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.cardboard.stellarbound.registry.ModItems;

import javax.annotation.Nonnull;

public class BulletEntity extends ThrowableItemProjectile {

    private float damage = 6.0f;

    public BulletEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
        // Hacer invisible
        this.noCulling = true;
    }

    public BulletEntity(Level level, LivingEntity shooter) {
        super(ModEntities.BULLET.get(), shooter, level);
        // Hacer invisible
        this.noCulling = true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return false; // Don't render at any distance
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    @Nonnull
    protected Item getDefaultItem() {
        return ModItems.PAPER_CARTRIDGE.get();
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleTo(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    protected void onHitEntity(@Nonnull EntityHitResult result) {
        super.onHitEntity(result);

        if (!this.level().isClientSide()) {
            if (result.getEntity() instanceof LivingEntity living) {
                // Aplicar daño
                if (this.getOwner() instanceof LivingEntity owner) {
                    living.hurt(this.damageSources().thrown(this, owner), damage);
                } else {
                    living.hurt(this.damageSources().thrown(this, null), damage);
                }

                // Efectos de sonido
                this.level().playSound(null,
                        this.getX(), this.getY(), this.getZ(),
                        net.minecraft.sounds.SoundEvents.ARROW_HIT_PLAYER,
                        net.minecraft.sounds.SoundSource.NEUTRAL,
                        0.5f, 1.2f);
            }

            this.discard();
        }
    }

    @Override
    protected void onHit(@Nonnull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide()) {
            // Efecto al golpear algo
            this.level().playSound(null,
                    this.getX(), this.getY(), this.getZ(),
                    net.minecraft.sounds.SoundEvents.GLASS_BREAK,
                    net.minecraft.sounds.SoundSource.NEUTRAL,
                    0.3f, 1.5f);

            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Optional: Still show particles but no entity render
        if (this.level().isClientSide()) {
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0.01, 0);
        }

        if (this.tickCount > 100) {
            this.discard();
        }
    }
}