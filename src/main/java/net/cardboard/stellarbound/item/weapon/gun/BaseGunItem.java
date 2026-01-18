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

    public BaseGunItem(Properties properties, int maxAmmo, float damage, int fireRate, int reloadTime, float accuracy) {
        super(properties.stacksTo(1));
        this.maxAmmo = maxAmmo;
        this.damage = damage;
        this.fireRate = fireRate;
        this.reloadTime = reloadTime;
        this.accuracy = accuracy;
    }

    // ========== MÉTODOS DE ESTADO ==========

    public int getAmmo(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Ammo")) {
            return maxAmmo; // Retorna munición completa por defecto
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

    // ========== MÉTODOS PARA MUNICIÓN ==========

    /**
     * Obtiene el tipo de munición requerido por esta arma.
     * Debe ser sobrescrito por cada arma concreta.
     */
    public abstract ItemStack getRequiredAmmo();

    /**
     * Verifica si el jugador tiene la munición necesaria para recargar.
     */
    public boolean hasRequiredAmmo(Player player) {
        return player.getInventory().contains(getRequiredAmmo());
    }

    /**
     * Consume la munición del inventario del jugador.
     * @return true si se pudo consumir la munición, false si no había suficiente.
     */
    public boolean consumeAmmo(Player player) {
        ItemStack requiredAmmo = getRequiredAmmo();
        if (!player.getInventory().contains(requiredAmmo)) {
            return false;
        }

        // Buscar y consumir el item de munición
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stackInSlot = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(stackInSlot, requiredAmmo)) {
                stackInSlot.shrink(1);
                if (stackInSlot.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
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
            // No disparar si no hay munición
            if (level.isClientSide()) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("¡El arma está vacía!"), true);
            }
            return;
        }

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

        // Verificar si el arma ya está llena
        if (getAmmo(stack) >= maxAmmo) {
            if (player.level().isClientSide()) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("¡El arma ya está cargada!"), true);
            }
            return;
        }

        // Verificar si tiene munición
        if (!hasRequiredAmmo(player)) {
            if (player.level().isClientSide()) {
                ItemStack ammo = getRequiredAmmo();
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("¡Necesitas " + ammo.getDisplayName().getString() + "!"),
                        true
                );
            }
            return;
        }

        setReloading(stack, true);
        player.getCooldowns().addCooldown(this, reloadTime);

        if (!player.level().isClientSide()) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_IRON,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.5f, 1.2f);
        }
    }

    // ========== CLICK DERECHO PARA RECARGAR ==========

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Click derecho para recargar manualmente
        if (!isReloading(stack) && getAmmo(stack) < maxAmmo) {
            startReload(player, stack);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    protected abstract void shoot(Player player, Level level, ItemStack stack);

    protected void playShootEffects(Level level, Player player) {
        if (level.isClientSide()) {
            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    0.5f, 1.5f);
        }
    }

    // ========== ANIMACIONES GECKOLIB ==========

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, this::predicate));
    }

    private PlayState predicate(AnimationState<BaseGunItem> event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return PlayState.STOP;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ItemStack currentStack = null;

        if (mainHand.getItem() == this) {
            currentStack = mainHand;
        } else if (offHand.getItem() == this) {
            currentStack = offHand;
        }

        if (currentStack == null || currentStack.isEmpty()) {
            return PlayState.STOP;
        }

        boolean isReloading = isReloading(currentStack);
        int ammo = getAmmo(currentStack);
        boolean hasShootCooldown = player.getCooldowns().isOnCooldown(this);

        // PRIORIDAD 1: Recarga
        if (isReloading && hasShootCooldown) {
            event.getController().setAnimation(getReloadAnimation());
            return PlayState.CONTINUE;
        }

        // PRIORIDAD 2: Disparo
        if (!isReloading && hasShootCooldown) {
            event.getController().setAnimation(getShootAnimation());
            return PlayState.CONTINUE;
        }

        // PRIORIDAD 3: Idle sin munición
        if (ammo <= 0 && !isReloading) {
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

        // Inicializar tags si no existen
        if (!stack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Ammo", maxAmmo);
            tag.putBoolean("Reloading", false);
            stack.setTag(tag);
        }

        if (entity instanceof Player player) {
            // Verificar si terminó la recarga
            if (isReloading(stack) && !player.getCooldowns().isOnCooldown(this)) {
                setReloading(stack, false);

                // Consumir la munición y recargar
                if (consumeAmmo(player)) {
                    // Calcular cuánta munición agregar (depende del arma)
                    int ammoToAdd = getAmmoToAddOnReload();
                    int currentAmmo = getAmmo(stack);
                    int newAmmo = Math.min(currentAmmo + ammoToAdd, maxAmmo);
                    setAmmo(stack, newAmmo);

                    // Verificar si necesita otra recarga (para armas con múltiples balas por recarga)
                    if (newAmmo < maxAmmo && hasRequiredAmmo(player)) {
                        // Si aún hay espacio y tiene munición, iniciar otra recarga automáticamente
                        startReload(player, stack);
                    }

                    if (!level.isClientSide()) {
                        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                net.minecraft.sounds.SoundEvents.ARROW_HIT_PLAYER,
                                net.minecraft.sounds.SoundSource.PLAYERS,
                                0.3f, 1.5f);
                    }
                } else {
                    // No tenía munición para consumir
                    if (level.isClientSide()) {
                        player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal("¡No tienes munición para recargar!"),
                                true
                        );
                    }
                }
            }
        }
    }

    /**
     * Cantidad de munición que se agrega por cada recarga.
     * Por defecto es 1 (como la flintlock), pero puede sobrescribirse para armas con cargadores.
     */
    protected int getAmmoToAddOnReload() {
        return 1;
    }

    // ========== GETTERS ==========

    public int getMaxAmmo() { return maxAmmo; }
    public float getDamage() { return damage; }
    public int getFireRate() { return fireRate; }
    public int getReloadTime() { return reloadTime; }
    public float getAccuracy() { return accuracy; }
}