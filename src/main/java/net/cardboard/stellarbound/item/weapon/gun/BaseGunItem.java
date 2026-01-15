package net.cardboard.stellarbound.item.weapon.gun;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    // ========== MÉTODOS DE ESTADO (Sin cambios innecesarios de NBT) ==========

    public static int getAmmo(ItemStack stack) {
        if (!stack.hasTag()) {
            return 0; // Retornar 0 sin inicializar NBT
        }
        return stack.getTag().getInt("Ammo");
    }

    public static void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateTag().putInt("Ammo", Math.min(ammo, 999));
    }

    public static boolean isReloading(ItemStack stack) {
        if (!stack.hasTag()) return false;
        return stack.getTag().getBoolean("Reloading");
    }

    private static void setReloading(ItemStack stack, boolean reloading) {
        if (reloading) {
            stack.getOrCreateTag().putBoolean("Reloading", true);
        } else if (stack.hasTag()) {
            stack.getTag().remove("Reloading");
        }
    }

    // USAR TICKS en lugar de milisegundos
    private static void setShootTick(ItemStack stack, int tick) {
        if (tick > 0) {
            stack.getOrCreateTag().putInt("ShootTick", tick);
        } else if (stack.hasTag()) {
            stack.getTag().remove("ShootTick");
        }
    }

    private static int getShootTick(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        return stack.getTag().getInt("ShootTick");
    }

    private static void setReloadStartTick(ItemStack stack, int tick) {
        if (tick > 0) {
            stack.getOrCreateTag().putInt("ReloadStartTick", tick);
        } else if (stack.hasTag()) {
            stack.getTag().remove("ReloadStartTick");
        }
    }

    private static int getReloadStartTick(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        return stack.getTag().getInt("ReloadStartTick");
    }

    // Tick global del juego (lado cliente)
    private static int clientTickCount = 0;

    // ========== DISPARO CON CLICK IZQUIERDO ==========

    @Override
    public boolean onEntitySwing(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) {
        // Evitar animación de swing normal
        return true;
    }

    @Override
    public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        // Disparar cuando atacas a una entidad (click izquierdo)
        if (attacker instanceof Player player) {
            tryShoot(player, stack);
        }
        return false; // No dañar con melee
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        // Disparar cuando haces click izquierdo en una entidad
        tryShoot(player, stack);
        return true; // Cancelar ataque normal
    }

    // Método público para disparar (llamado desde KeyBinding o desde click izquierdo)
    public void tryShoot(Player player, ItemStack stack) {
        Level level = player.level();

        // Verificar cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        // Verificar recarga
        if (isReloading(stack)) {
            return;
        }

        // Verificar munición
        int ammo = getAmmo(stack);
        if (ammo <= 0) {
            // No disparar, debe recargar manualmente con R
            return;
        }

        // Disparar
        if (!level.isClientSide()) {
            shoot(player, level, stack);
            setAmmo(stack, ammo - 1);
        } else {
            // En cliente: efectos y marcar tick de disparo
            playShootEffects(level, player);
            setShootTick(stack, clientTickCount);
            setReloadStartTick(stack, 0);
            setAmmo(stack, ammo - 1);
        }

        // Aplicar cooldown
        player.getCooldowns().addCooldown(this, fireRate);
    }

    // ========== RECARGA MANUAL (llamado desde KeyBinding) ==========

    public void startReload(Player player, ItemStack stack) {
        if (isReloading(stack)) return;
        if (getAmmo(stack) >= maxAmmo) return; // Ya está lleno

        setReloading(stack, true);

        if (player.level().isClientSide()) {
            setReloadStartTick(stack, clientTickCount);
            setShootTick(stack, 0);
        }

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

    // ========== CLICK DERECHO NO HACE NADA ==========

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        // Click derecho no hace nada ahora
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    protected abstract void shoot(Player player, Level level, ItemStack stack);

    protected void playShootEffects(Level level, Player player) {
        // Implementar efectos visuales y sonidos
    }

    // ========== ANIMACIONES GECKOLIB ==========

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<BaseGunItem> state) {
        ItemStack stack = lastRenderedStack;

        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BaseGunItem)) {
            return PlayState.STOP;
        }

        // PRIORIDAD 1: Verificar si acabamos de disparar (15 ticks = 0.75s)
        int shootTick = getShootTick(stack);
        if (shootTick > 0 && (clientTickCount - shootTick) < 15) {
            return state.setAndContinue(getShootAnimation());
        }

        // PRIORIDAD 2: Verificar si estamos recargando (50 ticks = 2.5s)
        int reloadStart = getReloadStartTick(stack);
        boolean isCurrentlyReloading = isReloading(stack);

        if (isCurrentlyReloading && reloadStart > 0 && (clientTickCount - reloadStart) < 50) {
            return state.setAndContinue(getReloadAnimation());
        }

        // PRIORIDAD 3: Idle según munición
        int ammo = getAmmo(stack);

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

        // Incrementar contador de ticks (solo cliente)
        if (level.isClientSide()) {
            clientTickCount++;
        }

        if (entity instanceof Player player) {
            // Verificar si terminó la recarga
            if (isReloading(stack) && !player.getCooldowns().isOnCooldown(this)) {
                setReloading(stack, false);
                setAmmo(stack, maxAmmo);
                setReloadStartTick(stack, 0);

                // Sonido de recarga completada
                if (!level.isClientSide()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            net.minecraft.sounds.SoundEvents.ARROW_HIT_PLAYER,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            0.3f, 1.5f);
                }
            }

            // Limpiar tick de disparo después de la animación
            int shootTick = getShootTick(stack);
            if (level.isClientSide() && shootTick > 0 && (clientTickCount - shootTick) > 15) {
                setShootTick(stack, 0);
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

    public static void setLastRenderedStack(ItemStack stack) {
        lastRenderedStack = stack;
    }

    // ========== INICIALIZACIÓN DE MUNICIÓN ==========

    @Override
    public void onCraftedBy(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Player player) {
        super.onCraftedBy(stack, level, player);
        setAmmo(stack, maxAmmo);
    }
}