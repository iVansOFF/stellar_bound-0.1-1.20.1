package net.cardboard.stellarbound.event;

import net.cardboard.stellarbound.capability.ManaProvider;
import net.cardboard.stellarbound.capability.PlayerMana;
import net.cardboard.stellarbound.network.ManaSyncPacket;
import net.cardboard.stellarbound.network.ModPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = net.cardboard.stellarbound.Stellarbound.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ManaEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    net.cardboard.stellarbound.Stellarbound.id("mana"),
                    new ManaProvider()
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            event.player.getCapability(ManaProvider.MANA).ifPresent(mana -> {
                // Regenerar mana (0.1 por tick = 2 por segundo)
                mana.tickRegen();

                // Sincronizar con cliente cada 10 ticks
                if (event.player.tickCount % 10 == 0) {
                    syncMana(event.player, mana);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(ManaProvider.MANA).ifPresent(oldMana ->
                    event.getEntity().getCapability(ManaProvider.MANA).ifPresent(newMana ->
                            newMana.deserializeNBT(oldMana.serializeNBT())
                    )
            );
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            event.getEntity().getCapability(ManaProvider.MANA).ifPresent(mana -> {
                // Inicializar mana al máximo al entrar
                mana.setMana(mana.getMaxMana());
                syncMana(event.getEntity(), mana);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            event.getEntity().getCapability(ManaProvider.MANA).ifPresent(mana -> {
                // Restaurar mana al respawn
                mana.setMana(mana.getMaxMana());
                syncMana(event.getEntity(), mana);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            event.getEntity().getCapability(ManaProvider.MANA).ifPresent(mana -> {
                syncMana(event.getEntity(), mana);
            });
        }
    }

    private static void syncMana(Player player, PlayerMana mana) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (ModPackets.INSTANCE != null) {
                ModPackets.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new ManaSyncPacket(mana.getMana(), mana.getMaxMana())
                );
            }
        }
    }
}