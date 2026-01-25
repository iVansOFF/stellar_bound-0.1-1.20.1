package net.cardboard.stellarbound.item;

import net.cardboard.stellarbound.capability.ManaProvider;
import net.cardboard.stellarbound.client.renderer.FirewandRenderer;
import net.cardboard.stellarbound.entity.FireballEntity;
import net.cardboard.stellarbound.network.ManaSyncPacket;
import net.cardboard.stellarbound.network.ModPackets;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class FirewandItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final float MANA_COST = 25.0f; // Costo de mana por disparo

    public FirewandItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .durability(325));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            // Verificar y consumir mana
            boolean hasEnoughMana = player.getCapability(ManaProvider.MANA).map(mana -> {
                if (mana.getMana() >= MANA_COST) {
                    mana.consumeMana(MANA_COST);

                    // Sincronizar mana con el cliente
                    if (player instanceof ServerPlayer serverPlayer) {
                        ModPackets.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() -> serverPlayer),
                                new ManaSyncPacket(mana.getMana(), mana.getMaxMana())
                        );
                    }
                    return true;
                }
                return false;
            }).orElse(false);

            // Si no hay suficiente mana, no disparar
            if (!hasEnoughMana) {
                // Reproducir sonido de error
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.3f, 2.0f);

                // Mensaje al jugador
                player.displayClientMessage(
                        Component.literal("§c¡No tienes suficiente mana!"),
                        true
                );

                return InteractionResultHolder.fail(itemstack);
            }

            // Crear y disparar la bola de fuego
            FireballEntity fireball = new FireballEntity(level, player);

            // Posicionar la bola de fuego frente al jugador
            Vec3 lookAngle = player.getLookAngle();
            fireball.setPos(
                    player.getX() + lookAngle.x * 0.5,
                    player.getEyeY() - 0.1,
                    player.getZ() + lookAngle.z * 0.5
            );

            // Establecer velocidad en la dirección que mira el jugador
            double speed = 1.5;
            fireball.setDeltaMovement(
                    lookAngle.x * speed,
                    lookAngle.y * speed,
                    lookAngle.z * speed
            );

            // Añadir la entidad al mundo
            level.addFreshEntity(fireball);

            // Reproducir sonido
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.5f, 1.0f);

            // Trigger animación de disparo
            triggerAnim(player, GeoItem.getOrAssignId(itemstack, (ServerLevel) level), "shoot", "shoot");

            // Reducir durabilidad
            itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }

        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Animación de idle
        controllers.add(new AnimationController<>(this, "idle", 0, state ->
                state.setAndContinue(RawAnimation.begin().thenLoop("animation.firewand.idle"))));

        // Animación de disparo
        controllers.add(new AnimationController<>(this, "shoot", 0, state -> {
            if (state.getController().hasAnimationFinished()) {
                return PlayState.STOP;
            }
            return state.setAndContinue(RawAnimation.begin().thenPlay("animation.firewand.shoot"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new FirewandRenderer();
            }
        });
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level,
                                @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(Component.literal("§7Class: §eArcanist"));
        tooltip.add(Component.literal("§cDamage: §f6"));
        tooltip.add(Component.literal("§bMana Cost: §f" + (int)MANA_COST));
        tooltip.add(Component.literal("§6Cooldown: §f1 Sec"));
    }
}