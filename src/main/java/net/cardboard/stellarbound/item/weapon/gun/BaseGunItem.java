package net.cardboard.stellarbound.item.weapon.gun;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;

public abstract class BaseGunItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Propiedades del arma
    protected final int maxAmmo;
    protected final float damage;
    protected final int fireRate; // Ticks entre disparos
    protected final int reloadTime; // Ticks para recargar
    protected final float accuracy; // 0.0 - 1.0 (1.0 = perfecto)

    // Estados de animación
    private boolean isShooting = false;
    private boolean isReloadingAnim = false;

    public BaseGunItem(Properties properties, int maxAmmo, float damage, int fireRate, int reloadTime, float accuracy) {
        super(properties.stacksTo(1));
        this.maxAmmo = maxAmmo;
        this.damage = damage;
        this.fireRate = fireRate;
        this.reloadTime = reloadTime;
        this.accuracy = accuracy;
    }

    // ========== MÉTODOS DE ESTADO ==========

    public static int getAmmo(ItemStack stack) {
        if (!stack.hasTag()) {
            if (stack.getItem() instanceof BaseGunItem gun) {
                setAmmo(stack, gun.maxAmmo);
            }
        }
        return stack.getOrCreateTag().getInt("Ammo");
    }

    public static void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateTag().putInt("Ammo", Math.min(ammo, 999));
    }

    public static boolean isReloading(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Reloading");
    }

    public static void setReloading(ItemStack stack, boolean reloading) {
        stack.getOrCreateTag().putBoolean("Reloading", reloading);
    }

    public static int getCooldown(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Cooldown");
    }

    public static void setCooldown(ItemStack stack, int cooldown) {
        stack.getOrCreateTag().putInt("Cooldown", cooldown);
    }

    // ========== MÉTODOS DE DISPARO ==========

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Verificar cooldown
        if (getCooldown(stack) > 0) {
            return InteractionResultHolder.fail(stack);
        }

        // Verificar recarga
        if (isReloading(stack)) {
            return InteractionResultHolder.fail(stack);
        }

        // Verificar munición
        int ammo = getAmmo(stack);
        if (ammo <= 0) {
            // Intentar recargar automáticamente
            startReload(player, stack);
            return InteractionResultHolder.fail(stack);
        }

        // Disparar
        if (!level.isClientSide()) {
            shoot(player, level, stack);
        } else {
            // En cliente: sonido y efectos visuales
            playShootEffects(level, player);
            // Activar animación de disparo
            setShooting(true);
        }

        // Reducir munición
        setAmmo(stack, ammo - 1);

        // Aplicar cooldown
        setCooldown(stack, fireRate);

        return InteractionResultHolder.success(stack);
    }

    protected abstract void shoot(Player player, Level level, ItemStack stack);

    protected void playShootEffects(Level level, Player player) {
        // Implementar efectos visuales y sonidos en el cliente
    }

    protected void startReload(Player player, ItemStack stack) {
        if (isReloading(stack)) return;

        setReloading(stack, true);
        setCooldown(stack, reloadTime);
        setGunReloading(true); // Activar animación de recarga

        // Efectos de sonido de recarga
        if (!player.level().isClientSide()) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_IRON,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.5f, 1.2f);
        }
    }

    // ========== ANIMACIONES GECKOLIB ==========

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Controlador para disparo
        controllers.add(new AnimationController<>(this, "shoot_controller", 0, this::shootPredicate));

        // Controlador para recarga
        controllers.add(new AnimationController<>(this, "reload_controller", 0, this::reloadPredicate));

        // Controlador para idle
        controllers.add(new AnimationController<>(this, "idle_controller", 0, this::idlePredicate));
    }

    private PlayState shootPredicate(AnimationState<BaseGunItem> state) {
        if (isShooting) {
            isShooting = false;
            return state.setAndContinue(getShootAnimation());
        }
        return PlayState.STOP;
    }

    private PlayState reloadPredicate(AnimationState<BaseGunItem> state) {
        if (isReloadingAnim) {
            isReloadingAnim = false;
            return state.setAndContinue(getReloadAnimation());
        }
        return PlayState.STOP;
    }

    private PlayState idlePredicate(AnimationState<BaseGunItem> state) {
        // Siempre reproducir idle
        return state.setAndContinue(getIdleAnimation());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ========== MÉTODOS DE ANIMACIÓN ABSTRACTOS ==========

    protected abstract RawAnimation getIdleAnimation();
    protected abstract RawAnimation getShootAnimation();
    protected abstract RawAnimation getReloadAnimation();

    // ========== INVENTORY TICK ==========

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        // Actualizar cooldown
        int cooldown = getCooldown(stack);
        if (cooldown > 0) {
            setCooldown(stack, cooldown - 1);

            // Si era una recarga y terminó el cooldown
            if (cooldown == 1 && isReloading(stack)) {
                setReloading(stack, false);
                setAmmo(stack, maxAmmo);

                // Sonido de recarga completada
                if (!level.isClientSide() && entity instanceof Player player) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            net.minecraft.sounds.SoundEvents.ARROW_HIT_PLAYER,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            0.3f, 1.5f);
                }
            }
        }
    }

    // ========== GETTERS ==========

    public int getMaxAmmo() { return maxAmmo; }
    public float getDamage() { return damage; }
    public int getFireRate() { return fireRate; }
    public int getReloadTime() { return reloadTime; }
    public float getAccuracy() { return accuracy; }

    // Métodos para controlar animaciones
    public void setShooting(boolean shooting) { this.isShooting = shooting; }
    public void setGunReloading(boolean reloading) { this.isReloadingAnim = reloading; }
}