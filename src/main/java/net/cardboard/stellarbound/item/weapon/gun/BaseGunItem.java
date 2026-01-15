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
    protected final int fireRate;
    protected final int reloadTime;
    protected final float accuracy;

    // ItemStack actual siendo renderizado (solo lado cliente)
    private static ItemStack lastRenderedStack = ItemStack.EMPTY;

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

    // Timestamp para controlar cuándo se disparó (solo cliente)
    private static void setShootTimestamp(ItemStack stack, long timestamp) {
        stack.getOrCreateTag().putLong("ShootTimestamp", timestamp);
    }

    private static long getShootTimestamp(ItemStack stack) {
        return stack.getOrCreateTag().getLong("ShootTimestamp");
    }

    // Timestamp para controlar cuándo empezó la recarga (cliente y servidor)
    private static void setReloadStartTimestamp(ItemStack stack, long timestamp) {
        stack.getOrCreateTag().putLong("ReloadStartTimestamp", timestamp);
    }

    private static long getReloadStartTimestamp(ItemStack stack) {
        return stack.getOrCreateTag().getLong("ReloadStartTimestamp");
    }

    // ========== MÉTODOS DE DISPARO ==========

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Verificar cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        // Verificar recarga
        if (isReloading(stack)) {
            return InteractionResultHolder.fail(stack);
        }

        // Verificar munición
        int ammo = getAmmo(stack);
        if (ammo <= 0) {
            startReload(player, stack);
            return InteractionResultHolder.fail(stack);
        }

        // Disparar
        if (!level.isClientSide()) {
            shoot(player, level, stack);
        } else {
            // En cliente: efectos y marcar timestamp de disparo
            playShootEffects(level, player);
            setShootTimestamp(stack, System.currentTimeMillis());
            // IMPORTANTE: Limpiar timestamp de recarga cuando disparamos
            setReloadStartTimestamp(stack, 0);
        }

        // Reducir munición
        setAmmo(stack, ammo - 1);

        // Aplicar cooldown
        player.getCooldowns().addCooldown(this, fireRate);

        return InteractionResultHolder.success(stack);
    }

    protected abstract void shoot(Player player, Level level, ItemStack stack);

    protected void playShootEffects(Level level, Player player) {
        // Implementar efectos visuales y sonidos
    }

    protected void startReload(Player player, ItemStack stack) {
        if (isReloading(stack)) return;

        setReloading(stack, true);

        // Marcar timestamp en ambos lados
        long currentTime = System.currentTimeMillis();
        setReloadStartTimestamp(stack, currentTime);

        // IMPORTANTE: Limpiar timestamp de disparo cuando empezamos a recargar
        setShootTimestamp(stack, 0);

        // Aplicar cooldown
        player.getCooldowns().addCooldown(this, reloadTime);

        // Sonido de recarga
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
        controllers.add(new AnimationController<>(this, "controller", 0, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<BaseGunItem> state) {
        // Obtener el ItemStack actual
        ItemStack stack = lastRenderedStack;

        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BaseGunItem)) {
            return PlayState.STOP;
        }

        long currentTime = System.currentTimeMillis();

        // PRIORIDAD 1: Verificar si acabamos de disparar (750ms)
        long shootTime = getShootTimestamp(stack);
        if (shootTime > 0 && (currentTime - shootTime) < 750) {
            return state.setAndContinue(getShootAnimation());
        }

        // PRIORIDAD 2: Verificar si estamos recargando (2500ms)
        long reloadStart = getReloadStartTimestamp(stack);
        boolean isCurrentlyReloading = isReloading(stack);

        if (isCurrentlyReloading && reloadStart > 0 && (currentTime - reloadStart) < 2500) {
            return state.setAndContinue(getReloadAnimation());
        }

        // PRIORIDAD 3: Idle según munición
        int ammo = getAmmo(stack);

        // Si no hay munición y NO estamos recargando, mostrar idle_unloaded
        if (ammo <= 0 && !isCurrentlyReloading) {
            return state.setAndContinue(getIdleUnloadedAnimation());
        }

        // PRIORIDAD 4: Idle normal
        return state.setAndContinue(getIdleAnimation());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ========== MÉTODOS DE ANIMACIÓN ABSTRACTOS ==========

    protected abstract RawAnimation getIdleAnimation();
    protected abstract RawAnimation getIdleUnloadedAnimation();
    protected abstract RawAnimation getShootAnimation();
    protected abstract RawAnimation getReloadAnimation();

    // ========== INVENTORY TICK ==========

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (entity instanceof Player player) {
            // Verificar si terminó la recarga
            if (isReloading(stack) && !player.getCooldowns().isOnCooldown(this)) {
                setReloading(stack, false);
                setAmmo(stack, maxAmmo);

                // Limpiar timestamp de recarga
                setReloadStartTimestamp(stack, 0);

                // Sonido de recarga completada
                if (!level.isClientSide()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            net.minecraft.sounds.SoundEvents.ARROW_HIT_PLAYER,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            0.3f, 1.5f);
                }
            }

            // Limpiar timestamp de disparo después de que pase el tiempo de animación
            long shootTime = getShootTimestamp(stack);
            if (shootTime > 0 && (System.currentTimeMillis() - shootTime) > 750) {
                setShootTimestamp(stack, 0);
            }
        }
    }

    // ========== GETTERS ==========

    public int getMaxAmmo() { return maxAmmo; }
    public float getDamage() { return damage; }
    public int getFireRate() { return fireRate; }
    public int getReloadTime() { return reloadTime; }
    public float getAccuracy() { return accuracy; }

    // ========== MÉTODO PARA RENDERER ==========

    /**
     * Actualiza el último ItemStack renderizado.
     * Debe ser llamado desde el renderer antes de renderizar.
     */
    public static void setLastRenderedStack(ItemStack stack) {
        lastRenderedStack = stack;
    }
}