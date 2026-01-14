package net.cardboard.stellarbound.item.weapon.gun;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public abstract class BaseGunItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Propiedades del arma
    protected final int maxAmmo;
    protected final float damage;
    protected final float fireRate; // Ticks entre disparos
    protected final float reloadTime; // Ticks para recargar
    protected final float accuracy; // 0.0 - 1.0 (1.0 = perfecto)

    public BaseGunItem(Properties properties, int maxAmmo, float damage, float fireRate, float reloadTime, float accuracy) {
        super(properties); // Sin stacksTo
        this.maxAmmo = maxAmmo;
        this.damage = damage;
        this.fireRate = fireRate;
        this.reloadTime = reloadTime;
        this.accuracy = accuracy;
    }

    // ========== GECKOLIB ANIMATION ==========

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // Por defecto, animación idle
            return state.setAndContinue(RawAnimation.begin().thenLoop(getIdleAnimation()));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ========== MÉTODOS ABSTRACTOS (deben ser implementados por cada arma) ==========

    protected abstract String getIdleAnimation();

    protected abstract String getIdleUnloadedAnimation();

    protected abstract String getShootAnimation();

    protected abstract String getReloadAnimation();

    // ========== GETTERS PARA PROPIEDADES ==========

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public float getDamage() {
        return damage;
    }

    public float getFireRate() {
        return fireRate;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public float getAccuracy() {
        return accuracy;
    }

    // ========== MÉTODOS DE UTILIDAD PARA MUNICIÓN ==========

    public static int getAmmo(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Ammo");
    }

    public static void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateTag().putInt("Ammo", ammo);
    }

    public static boolean isOnCooldown(ItemStack stack) {
        return stack.getOrCreateTag().getLong("LastShot") + getFireRateTicks(stack) > System.currentTimeMillis();
    }

    public static void setLastShot(ItemStack stack) {
        stack.getOrCreateTag().putLong("LastShot", System.currentTimeMillis());
    }

    public static boolean isReloading(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Reloading");
    }

    public static void setReloading(ItemStack stack, boolean reloading) {
        stack.getOrCreateTag().putBoolean("Reloading", reloading);
    }

    private static long getFireRateTicks(ItemStack stack) {
        if (stack.getItem() instanceof BaseGunItem gun) {
            return (long)(gun.getFireRate() * 50); // Convertir ticks a ms
        }
        return 0;
    }

    // ========== RENDERER OVERRIDE ==========

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BaseGunRenderer<?> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = createRenderer();
                }
                return this.renderer;
            }
        });
    }

    protected abstract BaseGunRenderer<?> createRenderer();

    // ========== BUILDER PATTERN ==========

    public static class Builder {
        private int maxAmmo = 6;
        private float damage = 8.0f;
        private float fireRate = 20.0f; // 1 segundo
        private float reloadTime = 60.0f; // 3 segundos
        private float accuracy = 0.95f;

        public Builder maxAmmo(int ammo) {
            this.maxAmmo = ammo;
            return this;
        }

        public Builder damage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder fireRate(float ticks) {
            this.fireRate = ticks;
            return this;
        }

        public Builder reloadTime(float ticks) {
            this.reloadTime = ticks;
            return this;
        }

        public Builder accuracy(float accuracy) {
            this.accuracy = Math.min(1.0f, Math.max(0.0f, accuracy));
            return this;
        }

        public int getMaxAmmo() { return maxAmmo; }
        public float getDamage() { return damage; }
        public float getFireRate() { return fireRate; }
        public float getReloadTime() { return reloadTime; }
        public float getAccuracy() { return accuracy; }
    }
}