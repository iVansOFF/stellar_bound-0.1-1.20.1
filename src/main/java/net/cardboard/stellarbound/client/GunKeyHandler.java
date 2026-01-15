package net.cardboard.stellarbound.client;

import net.cardboard.stellarbound.item.weapon.gun.BaseGunItem;
import net.cardboard.stellarbound.network.GunActionPacket;
import net.cardboard.stellarbound.network.ModPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "stellarbound", value = Dist.CLIENT)
public class GunKeyHandler {

    private static boolean wasReloadPressed = false;
    private static boolean wasShootPressed = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Player player = mc.player;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        // Buscar arma en las manos
        ItemStack gunStack = ItemStack.EMPTY;
        boolean isMainHand = false;

        if (mainHand.getItem() instanceof BaseGunItem) {
            gunStack = mainHand;
            isMainHand = true;
        } else if (offHand.getItem() instanceof BaseGunItem) {
            gunStack = offHand;
            isMainHand = false;
        }

        if (!gunStack.isEmpty() && gunStack.getItem() instanceof BaseGunItem gun) {
            // ========== RELOAD (R) ==========
            boolean reloadPressed = ModKeyBindings.RELOAD_KEY.isDown();
            if (reloadPressed && !wasReloadPressed) {
                // Acción de recarga (lado cliente)
                gun.startReload(player, gunStack);

                // Enviar packet al servidor
                ModPackets.sendToServer(new GunActionPacket(GunActionPacket.Action.RELOAD, isMainHand));
            }
            wasReloadPressed = reloadPressed;

            // ========== SHOOT (Click Izquierdo en el aire) ==========
            // Nota: Cuando atacas entidades, onLeftClickEntity lo maneja
            // Esto es para disparar al aire
            boolean shootPressed = ModKeyBindings.SHOOT_KEY.isDown();
            if (shootPressed && !wasShootPressed && mc.hitResult != null &&
                    mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.MISS) {
                // Solo disparar si no estás apuntando a nada (click en el aire)
                gun.tryShoot(player, gunStack);

                // Enviar packet al servidor
                ModPackets.sendToServer(new GunActionPacket(GunActionPacket.Action.SHOOT, isMainHand));
            }
            wasShootPressed = shootPressed;
        } else {
            // Resetear estados si no hay arma
            wasReloadPressed = false;
            wasShootPressed = false;
        }
    }
}