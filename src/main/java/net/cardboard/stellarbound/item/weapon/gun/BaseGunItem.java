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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseGunItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Propiedades del arma
    protected final int maxAmmo;
    protected final float damage;
    protected final int fireRate; // Ticks entre disparos
    protected final int reloadTime; // Ticks para recargar
    protected final float accuracy; // 0.0 - 1.0 (1.0 = perfecto)

    // Estados de animación (almacenados por UUID del item)
    private static final Map<UUID, Boolean> shootingStates = new HashMap<>();
    private static final Map<UUID, Boolean> reloadingAnimStates = new HashMap<>();
    private static final Map<UUID, Boolean> emptyStates = new HashMap<>();

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

    // ========== MÉTODOS PARA GENERAR/MANEJAR UUID DEL ITEM ==========

    private static UUID getOrCreateItemUUID(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("ItemUUID")) {
            UUID uuid = UUID.randomUUID();
            stack.getOrCreateTag().putUUID("ItemUUID", uuid);
            return uuid;
        }
        return stack.getTag().getUUID("ItemUUID");
    }

    // ========== MÉTODOS DE DISPARO CON ITEM COOLDOWN ==========

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Verificar si está en cooldown (usando ItemCooldownManager)
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
            setShooting(stack, true);
        }

        // Reducir munición
        setAmmo(stack, ammo - 1);
        updateEmptyState(stack);

        // Aplicar cooldown usando ItemCooldownManager
        player.getCooldowns().addCooldown(this, fireRate);

        return InteractionResultHolder.success(stack);
    }

    protected abstract void shoot(Player player, Level level, ItemStack stack);

    protected void playShootEffects(Level level, Player player) {
        // Implementar efectos visuales y sonidos en el cliente
    }

    protected void startReload(Player player, ItemStack stack) {
        if (isReloading(stack)) return;

        setReloading(stack, true);
        setReloadingAnim(stack, true); // Activar animación de recarga

        // Aplicar cooldown para la recarga usando ItemCooldownManager
        player.getCooldowns().addCooldown(this, reloadTime);

        // Efectos de sonido de recarga
        if (!player.level().isClientSide()) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_IRON,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.5f, 1.2f);
        }
    }

    // ========== ANIMACIONES GECKOLIB MEJORADAS ==========

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Controlador para disparo
        controllers.add(new AnimationController<>(this, "shoot_controller", 0, this::shootPredicate));

        // Controlador para recarga
        controllers.add(new AnimationController<>(this, "reload_controller", 0, this::reloadPredicate));

        // Controlador para idle (dinámico basado en estado del arma)
        controllers.add(new AnimationController<>(this, "idle_controller", 0, this::idlePredicate));
    }

    private PlayState shootPredicate(AnimationState<BaseGunItem> state) {
        // Buscar si algún item de este tipo está disparando
        for (Map.Entry<UUID, Boolean> entry : shootingStates.entrySet()) {
            if (entry.getValue()) {
                shootingStates.put(entry.getKey(), false);
                return state.setAndContinue(getShootAnimation());
            }
        }
        return PlayState.STOP;
    }

    private PlayState reloadPredicate(AnimationState<BaseGunItem> state) {
        // Buscar si algún item de este tipo está recargando
        for (Map.Entry<UUID, Boolean> entry : reloadingAnimStates.entrySet()) {
            if (entry.getValue()) {
                reloadingAnimStates.put(entry.getKey(), false);
                return state.setAndContinue(getReloadAnimation());
            }
        }
        return PlayState.STOP;
    }

    private PlayState idlePredicate(AnimationState<BaseGunItem> state) {
        // Verificar si este item específico está vacío
        ItemStack currentStack = getCurrentItemStack();
        if (currentStack != null && currentStack.getItem() instanceof BaseGunItem) {
            UUID itemUUID = getOrCreateItemUUID(currentStack);
            if (emptyStates.getOrDefault(itemUUID, false)) {
                return state.setAndContinue(getIdleUnloadedAnimation());
            }
        }

        // Si no está vacío, reproducir animación idle normal
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

        // Verificar si terminó la recarga (basado en el cooldown del jugador)
        if (entity instanceof Player player) {
            if (isReloading(stack) && !player.getCooldowns().isOnCooldown(this)) {
                setReloading(stack, false);
                setAmmo(stack, maxAmmo);
                updateEmptyState(stack);

                // Sonido de recarga completada
                if (!level.isClientSide()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            net.minecraft.sounds.SoundEvents.ARROW_HIT_PLAYER,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            0.3f, 1.5f);
                }
            }
        }

        // Actualizar estado vacío
        updateEmptyState(stack);
    }

    // ========== MÉTODOS AUXILIARES PARA ANIMACIONES ==========

    private void updateEmptyState(ItemStack stack) {
        UUID itemUUID = getOrCreateItemUUID(stack);
        boolean isEmpty = getAmmo(stack) <= 0 && !isReloading(stack);
        emptyStates.put(itemUUID, isEmpty);
    }

    private void setShooting(ItemStack stack, boolean shooting) {
        shootingStates.put(getOrCreateItemUUID(stack), shooting);
    }

    private void setReloadingAnim(ItemStack stack, boolean reloading) {
        reloadingAnimStates.put(getOrCreateItemUUID(stack), reloading);
    }

    // Método auxiliar para obtener el item stack actual (debe ser sobrescrito en el cliente)
    protected ItemStack getCurrentItemStack() {
        return null; // Se sobrescribe en la clase del cliente
    }

    // ========== GETTERS ==========

    public int getMaxAmmo() { return maxAmmo; }
    public float getDamage() { return damage; }
    public int getFireRate() { return fireRate; }
    public int getReloadTime() { return reloadTime; }
    public float getAccuracy() { return accuracy; }
}