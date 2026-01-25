package net.cardboard.stellarbound.entity;

import net.cardboard.stellarbound.registry.ModEntities;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;

public class FireballEntity extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float damage = 6.0f;

    public FireballEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public FireballEntity(Level level, LivingEntity shooter) {
        this(ModEntities.FIREBALL.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    public void tick() {
        super.tick();

        // Actualizar rotación basándose en la dirección de movimiento
        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() > 0.0001) {
            // Calcular yaw (rotación horizontal)
            float yaw = (float) (Mth.atan2(motion.x, motion.z) * (180.0 / Math.PI));

            // Calcular pitch (rotación vertical)
            double horizontalDistance = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            float pitch = (float) (Mth.atan2(motion.y, horizontalDistance) * (180.0 / Math.PI));

            // Establecer la rotación
            this.setYRot(yaw);
            this.setXRot(pitch);
        }

        // Partículas de fuego en el cliente
        if (this.level().isClientSide()) {
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.3,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.3,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.3,
                        0, 0, 0);
            }
        }

        // Detección de colisiones
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS) {
            this.onHit(hitresult);
        }

        // Actualizar posición
        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);

        // Descartar después de 5 segundos
        if (this.tickCount > 100) {
            this.discard();
        }
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

                // Prender fuego a la entidad
                living.setSecondsOnFire(5);
            }

            // Efectos visuales y sonoros
            createExplosionEffect();
            this.discard();
        }
    }

    @Override
    protected void onHit(@Nonnull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide()) {
            createExplosionEffect();
            this.discard();
        }
    }

    private void createExplosionEffect() {
        // Sonido de explosión
        this.level().playSound(null,
                this.getX(), this.getY(), this.getZ(),
                net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                net.minecraft.sounds.SoundSource.NEUTRAL,
                0.3f, 1.5f);

        // Partículas de explosión
        if (this.level().isClientSide()) {
            for (int i = 0; i < 20; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                double distance = this.random.nextDouble() * 0.5;
                this.level().addParticle(ParticleTypes.FLAME,
                        this.getX() + Math.cos(angle) * distance,
                        this.getY() + this.random.nextDouble() * 0.5,
                        this.getZ() + Math.sin(angle) * distance,
                        0, 0.05, 0);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Puedes añadir animaciones de rotación aquí si quieres
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData() {
        // No necesitamos datos sincronizados especiales
    }
}