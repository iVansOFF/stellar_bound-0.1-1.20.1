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
    private static final Map<UUID, GunAnimationStateData> animationStates = new HashMap<>();

    // Almacenar temporalmente el último item stack renderizado (para cliente)
    private static ItemStack lastRenderedStack = null;

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
        if (!stack.hasTag()) {
            stack.getOrCreateTag();
        }
        if (!stack.getTag().contains("ItemUUID")) {
            UUID uuid = UUID.randomUUID();
            stack.getTag().putUUID("ItemUUID", uuid);
            return uuid;
        }
        return stack.getTag().getUUID("ItemUUID");
    }

    // ========== MÉTODOS DE DISPARO CON ITEM COOLDOWN ==========

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        UUID itemUUID = getOrCreateItemUUID(stack);

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
            setAnimationState(itemUUID, GunAnimation.SHOOTING);
        }

        // Reducir munición
        setAmmo(stack, ammo - 1);

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

        UUID itemUUID = getOrCreateItemUUID(stack);

        setReloading(stack, true);
        setAnimationState(itemUUID, GunAnimation.RELOADING); // Activar animación de recarga

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
        // Controlador principal que maneja todas las animaciones
        controllers.add(new AnimationController<>(this, "main_controller", 5, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<BaseGunItem> state) {
        // Usar el último item stack renderizado
        ItemStack currentStack = lastRenderedStack;
        if (currentStack != null && currentStack.getItem() instanceof BaseGunItem) {
            UUID itemUUID = getOrCreateItemUUID(currentStack);
            GunAnimationStateData stateData = animationStates.get(itemUUID);

            if (stateData != null) {
                // Procesar estado de animación actual
                switch (stateData.currentState) {
                    case SHOOTING:
                        stateData.currentState = GunAnimation.IDLE; // Resetear después de disparar
                        return state.setAndContinue(getShootAnimation());

                    case RELOADING:
                        stateData.currentState = GunAnimation.IDLE; // Resetear después de recargar
                        return state.setAndContinue(getReloadAnimation());

                    case IDLE:
                        // Verificar si está vacío
                        int ammo = getAmmo(currentStack);
                        boolean reloading = isReloading(currentStack);
                        if (ammo <= 0 && !reloading) {
                            return state.setAndContinue(getIdleUnloadedAnimation());
                        } else {
                            return state.setAndContinue(getIdleAnimation());
                        }
                }
            }
        }

        // Animación por defecto (idle cargado)
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

                // Sonido de recarga completada
                if (!level.isClientSide()) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            net.minecraft.sounds.SoundEvents.ARROW_HIT_PLAYER,
                            net.minecraft.sounds.SoundSource.PLAYERS,
                            0.3f, 1.5f);
                }
            }
        }
    }

    // ========== MÉTODOS AUXILIARES PARA ANIMACIONES ==========

    private void setAnimationState(UUID itemUUID, GunAnimation state) {
        GunAnimationStateData stateData = animationStates.computeIfAbsent(itemUUID, k -> new GunAnimationStateData());
        stateData.currentState = state;
        stateData.lastUpdateTime = System.currentTimeMillis();
    }

    // Método para actualizar el último item stack renderizado
    public static void setLastRenderedStack(ItemStack stack) {
        lastRenderedStack = stack;
    }

    // ========== GETTERS ==========

    public int getMaxAmmo() { return maxAmmo; }
    public float getDamage() { return damage; }
    public int getFireRate() { return fireRate; }
    public int getReloadTime() { return reloadTime; }
    public float getAccuracy() { return accuracy; }

    // ========== CLASES AUXILIARES ==========

    // Renombrado para evitar conflicto con GeckoLib's AnimationState
    public enum GunAnimation {
        IDLE,
        SHOOTING,
        RELOADING
    }

    private static class GunAnimationStateData {
        GunAnimation currentState = GunAnimation.IDLE;
        long lastUpdateTime = System.currentTimeMillis();
    }
}