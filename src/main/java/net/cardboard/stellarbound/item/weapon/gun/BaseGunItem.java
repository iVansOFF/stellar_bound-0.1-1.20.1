package net.cardboard.stellarbound.item.weapon.gun;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
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
import software.bernie.geckolib.core.animation.*;
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

    // Estados de animación por ItemStack
    // AJUSTA ESTOS VALORES SEGÚN LA DURACIÓN REAL DE TUS ANIMACIONES
    private static final int SHOOT_ANIMATION_DURATION = 20;  // 1 segundo
    private static final int RELOAD_ANIMATION_DURATION = 60; // 3 segundos

    // Campo estático para el último ItemStack renderizado
    private static ItemStack lastRenderedStack = ItemStack.EMPTY;

    public BaseGunItem(Properties properties, int maxAmmo, float damage, int fireRate, int reloadTime, float accuracy) {
        super(properties.stacksTo(1));
        this.maxAmmo = maxAmmo;
        this.damage = damage;
        this.fireRate = fireRate;
        this.reloadTime = reloadTime;
        this.accuracy = accuracy;
    }

    public static ItemStack getLastRenderedStack() {
        return lastRenderedStack;
    }

    // ========== MÉTODOS DE ESTADO ==========

    public int getAmmo(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Ammo")) {
            return 0;
        }
        return tag.getInt("Ammo");
    }

    public void setAmmo(ItemStack stack, int ammo) {
        stack.getOrCreateTag().putInt("Ammo", Math.min(ammo, this.maxAmmo));
    }

    public boolean isReloading(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        return tag.getBoolean("Reloading");
    }

    private void setReloading(ItemStack stack, boolean reloading) {
        stack.getOrCreateTag().putBoolean("Reloading", reloading);
    }

    private void setShootTick(ItemStack stack, long tick) {
        if (tick > 0) {
            stack.getOrCreateTag().putLong("ShootTick", tick);
        } else {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove("ShootTick");
            }
        }
    }

    private long getShootTick(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("ShootTick")) {
            return 0;
        }
        return tag.getLong("ShootTick");
    }

    private void setReloadStartTick(ItemStack stack, long tick) {
        if (tick > 0) {
            stack.getOrCreateTag().putLong("ReloadStartTick", tick);
        } else {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove("ReloadStartTick");
            }
        }
    }

    private long getReloadStartTick(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("ReloadStartTick")) {
            return 0;
        }
        return tag.getLong("ReloadStartTick");
    }

    // ========== DISPARO CON CLICK IZQUIERDO ==========

    @Override
    public boolean onEntitySwing(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) {
        return true;
    }

    @Override
    public boolean hurtEnemy(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        if (attacker instanceof Player player) {
            tryShoot(player, stack);
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        tryShoot(player, stack);
        return true;
    }

    public void tryShoot(Player player, ItemStack stack) {
        Level level = player.level();

        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        if (isReloading(stack)) {
            return;
        }

        int ammo = getAmmo(stack);
        if (ammo <= 0) {
            return;
        }

        // Marcar el tick de disparo SIEMPRE (cliente y servidor)
        setShootTick(stack, level.getGameTime());
        setReloadStartTick(stack, 0);

        if (!level.isClientSide()) {
            shoot(player, level, stack);
            setAmmo(stack, ammo - 1);
        } else {
            playShootEffects(level, player);
            setAmmo(stack, ammo - 1);
        }

        player.getCooldowns().addCooldown(this, fireRate);
    }

    // ========== RECARGA MANUAL ==========

    public void startReload(Player player, ItemStack stack) {
        if (isReloading(stack)) return;
        if (getAmmo(stack) >= maxAmmo) return;

        setReloading(stack, true);

        // Marcar el tick de recarga SIEMPRE (cliente y servidor)
        setReloadStartTick(stack, player.level().getGameTime());
        setShootTick(stack, 0);

        player.getCooldowns().addCooldown(this, reloadTime);

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

    private PlayState animationPredicate(AnimationState<BaseGunItem> event) {
        ItemStack stack = lastRenderedStack;

        // Verificar que el stack sea válido y sea de este tipo de arma
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BaseGunItem)) {
            event.getController().setAnimation(getIdleAnimation());
            return PlayState.CONTINUE;
        }

        // Obtener el tiempo actual del cliente
        long currentTime = 0;
        if (Minecraft.getInstance().level != null) {
            currentTime = Minecraft.getInstance().level.getGameTime();
        }

        long shootTick = getShootTick(stack);
        long reloadStart = getReloadStartTick(stack);
        boolean isCurrentlyReloading = isReloading(stack);

        // Debug - descomentar para ver qué está pasando
        // System.out.println("Current: " + currentTime + " Shoot: " + shootTick + " Reload: " + reloadStart + " IsReloading: " + isCurrentlyReloading);

        // PRIORIDAD 1: Disparo reciente
        if (shootTick > 0 && currentTime > 0) {
            long timeSinceShot = currentTime - shootTick;
            if (timeSinceShot < SHOOT_ANIMATION_DURATION) {
                event.getController().setAnimation(getShootAnimation());
                return PlayState.CONTINUE;
            }
        }

        // PRIORIDAD 2: Recarga en progreso
        if (isCurrentlyReloading && reloadStart > 0 && currentTime > 0) {
            long timeSinceReload = currentTime - reloadStart;
            if (timeSinceReload < RELOAD_ANIMATION_DURATION) {
                event.getController().setAnimation(getReloadAnimation());
                return PlayState.CONTINUE;
            }
        }

        // PRIORIDAD 3: Idle según munición
        int ammo = getAmmo(stack);
        if (ammo <= 0 && !isCurrentlyReloading) {
            event.getController().setAnimation(getIdleUnloadedAnimation());
            return PlayState.CONTINUE;
        }

        // PRIORIDAD 4: Idle normal
        event.getController().setAnimation(getIdleAnimation());
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ========== MÉTODOS DE ANIMACIÓN ABSTRACTOS ==========

    protected abstract RawAnimation getShootAnimation();
    protected abstract RawAnimation getReloadAnimation();
    protected abstract RawAnimation getIdleAnimation();
    protected abstract RawAnimation getIdleUnloadedAnimation();

    // ========== INVENTORY TICK ==========

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide()) {
            if (isSelected) {
                lastRenderedStack = stack;
            } else if (lastRenderedStack == stack) {
                // Limpiar si este stack ya no está seleccionado
                lastRenderedStack = ItemStack.EMPTY;
            }
        }

        // Inicializar munición si no existe
        if (!stack.hasTag()) {
            setAmmo(stack, maxAmmo);
        }

        if (entity instanceof Player player) {
            long currentTime = level.getGameTime();

            // Limpiar el tick de disparo después de la duración
            long shootTick = getShootTick(stack);
            if (shootTick > 0 && (currentTime - shootTick) >= SHOOT_ANIMATION_DURATION) {
                setShootTick(stack, 0);
            }

            // Limpiar el tick de recarga después de la duración
            long reloadStartTick = getReloadStartTick(stack);
            if (reloadStartTick > 0 && (currentTime - reloadStartTick) >= RELOAD_ANIMATION_DURATION) {
                setReloadStartTick(stack, 0);
            }

            // Verificar si terminó la recarga
            if (isReloading(stack) && !player.getCooldowns().isOnCooldown(this)) {
                setReloading(stack, false);
                setAmmo(stack, maxAmmo);
                setReloadStartTick(stack, 0);

                if (!level.isClientSide()) {
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