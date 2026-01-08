package net.cardboard.stellarbound.entity;

import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WimpEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean isRunning = false;
    private int runningTicks = 0;

    public WimpEntity(EntityType<? extends PathfinderMob> type, @NotNull Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        // Huye cuando es atacado
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.3D, 1.5D));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            this.isRunning = true;
            this.runningTicks = 200; // 10 segundos (200 ticks)
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        // Manejar el estado de correr
        if (!this.level().isClientSide && this.isRunning) {
            this.runningTicks--;
            if (this.runningTicks <= 0) {
                this.isRunning = false;
                this.runningTicks = 0;
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "controller",
                5,
                state -> {
                    // Si está corriendo asustado
                    if (isRunning || (this.getDeltaMovement().horizontalDistanceSqr() > 0.01)) {
                        if (isRunning) {
                            return state.setAndContinue(
                                    RawAnimation.begin().thenLoop("animation.wimp.run")
                            );
                        }
                        // Caminando normal
                        return state.setAndContinue(
                                RawAnimation.begin().thenLoop("animation.wimp.walk")
                        );
                    }
                    // Idle
                    return state.setAndContinue(
                            RawAnimation.begin().thenLoop("animation.wimp.idle")
                    );
                }
        ));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}