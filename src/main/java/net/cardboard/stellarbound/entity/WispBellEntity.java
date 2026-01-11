package net.cardboard.stellarbound.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class WispBellEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Player followingPlayer = null;
    private int nightVisionCooldown = 0;
    private int searchCooldown = 0;

    public WispBellEntity(EntityType<? extends PathfinderMob> type, @NotNull Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @SuppressWarnings("unused")
    public static boolean checkWispSpawnRules(
            EntityType<WispBellEntity> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getRawBrightness(pos, 0) <= 15; // Spawn nocturno
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        // Primero: Flotar
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // Segundo: Seguir al jugador más cercano
        this.goalSelector.addGoal(1, new FollowPlayerGoal());
        // Tercero: Volar aleatoriamente si no hay jugador
        this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        // Mirar al jugador
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        // Mirar alrededor aleatoriamente
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        // Solo en el servidor
        if (!this.level().isClientSide) {
            // Evitar que caiga al suelo
            Vec3 motion = this.getDeltaMovement();
            if (motion.y < 0 && !this.onGround()) {
                this.setDeltaMovement(motion.x, motion.y * 0.6, motion.z);
            }

            // Buscar jugador más cercano (cada 10 ticks)
            if (searchCooldown <= 0) {
                findNearestPlayer();
                searchCooldown = 10;
            } else {
                searchCooldown--;
            }

            // Aplicar visión nocturna a jugadores cercanos (cada 40 ticks = 2 segundos)
            if (nightVisionCooldown <= 0) {
                applyNightVisionToNearbyPlayers();
                nightVisionCooldown = 40;
            } else {
                nightVisionCooldown--;
            }

            // Si tenemos un jugador para seguir, actualizar la navegación
            if (followingPlayer != null && !followingPlayer.isRemoved()) {
                // Verificar si el jugador está demasiado lejos
                if (this.distanceToSqr(followingPlayer) > 256) { // 16 bloques al cuadrado
                    followingPlayer = null;
                    this.getNavigation().stop();
                }
            } else {
                followingPlayer = null;
            }
        }
    }

    private void findNearestPlayer() {
        if (this.level().isClientSide) return;

        // Buscar jugadores en un radio de 16 bloques
        AABB searchArea = this.getBoundingBox().inflate(16.0D);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, searchArea);

        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        for (Player player : players) {
            if (!player.isSpectator() && !player.isCreative() && player.isAlive()) {
                double distance = this.distanceToSqr(player);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }
        }

        this.followingPlayer = closestPlayer;
    }

    private void applyNightVisionToNearbyPlayers() {
        if (this.level().isClientSide) return;

        // Buscar jugadores en un radio de 10 bloques
        AABB area = this.getBoundingBox().inflate(10.0D);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, area);

        for (Player player : players) {
            if (!player.isSpectator() && !player.isCreative() && player.isAlive()) {
                // Aplicar efecto de visión nocturna (15 segundos)
                MobEffectInstance currentEffect = player.getEffect(MobEffects.NIGHT_VISION);
                if (currentEffect == null || currentEffect.getDuration() < 100) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.NIGHT_VISION,
                            300,  // 15 segundos (300 ticks)
                            0,    // Nivel 1
                            true, // Mostrar partículas
                            true  // Mostrar icono
                    ));
                }

                // Efecto adicional: Regeneración leve (5 segundos)
                player.addEffect(new MobEffectInstance(
                        MobEffects.REGENERATION,
                        100,  // 5 segundos
                        0,
                        true,
                        true
                ));
            }
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        // Si es atacado, dejar de seguir temporalmente
        if (source.getEntity() instanceof Player) {
            followingPlayer = null;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state ->
                state.setAndContinue(RawAnimation.begin().thenLoop("animation.wisp_bell.fly"))
        ));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.FLYING_SPEED, 1.5D) // Aumentado para seguir mejor
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ========== VUELO ==========

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false; // No recibe daño de caída
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull BlockState state, @NotNull BlockPos pos) {
        // No procesa daño de caída
    }

    // ========== SONIDOS ==========

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BELL_RESONATE;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.BELL_BLOCK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F; // Más silencioso
    }

    @Override
    public float getVoicePitch() {
        return 1.5F; // Más agudo y etéreo
    }

    // ========== CLASE INTERNA PARA SEGUIR AL JUGADOR ==========

    private class FollowPlayerGoal extends Goal {
        private final double speedModifier = 1.0D;
        private final float stopDistance = 3.0F;
        private int timeToRecalcPath = 0;

        public FollowPlayerGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return WispBellEntity.this.followingPlayer != null &&
                    WispBellEntity.this.followingPlayer.isAlive() &&
                    !WispBellEntity.this.followingPlayer.isSpectator() &&
                    WispBellEntity.this.distanceToSqr(WispBellEntity.this.followingPlayer) > (stopDistance * stopDistance);
        }

        @Override
        public boolean canContinueToUse() {
            return canUse() && !WispBellEntity.this.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            WispBellEntity.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (WispBellEntity.this.followingPlayer == null) return;

            // Mirar al jugador
            WispBellEntity.this.getLookControl().setLookAt(
                    WispBellEntity.this.followingPlayer,
                    10.0F,
                    (float)WispBellEntity.this.getMaxHeadXRot()
            );

            // Actualizar el camino periódicamente
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);

                // Calcular camino hacia el jugador
                WispBellEntity.this.getNavigation().moveTo(
                        WispBellEntity.this.followingPlayer,
                        this.speedModifier
                );

                // Si está muy cerca, detenerse
                double distanceSqr = WispBellEntity.this.distanceToSqr(WispBellEntity.this.followingPlayer);
                if (distanceSqr <= (stopDistance * stopDistance)) {
                    WispBellEntity.this.getNavigation().stop();
                    // Mantenerse flotando cerca del jugador
                    Vec3 playerPos = WispBellEntity.this.followingPlayer.position();
                    Vec3 offset = new Vec3(
                            playerPos.x + (WispBellEntity.this.random.nextDouble() - 0.5) * 2,
                            playerPos.y + 1.5 + (WispBellEntity.this.random.nextDouble() - 0.5),
                            playerPos.z + (WispBellEntity.this.random.nextDouble() - 0.5) * 2
                    );

                    // Moverse suavemente hacia la posición
                    Vec3 direction = offset.subtract(WispBellEntity.this.position()).normalize().scale(0.05);
                    WispBellEntity.this.setDeltaMovement(
                            WispBellEntity.this.getDeltaMovement().scale(0.9).add(direction)
                    );
                }
            }
        }
    }

    // Método para debug
    public void debugInfo() {
        System.out.println("WispBell Debug:");
        System.out.println("  - Following player: " + (followingPlayer != null ? followingPlayer.getName().getString() : "none"));
        System.out.println("  - Position: " + this.position());
        System.out.println("  - Navigation active: " + this.getNavigation().isDone());
    }
}