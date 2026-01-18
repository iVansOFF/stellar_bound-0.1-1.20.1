package net.cardboard.stellarbound.client.hud;

import net.cardboard.stellarbound.item.weapon.gun.BaseGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Overlay de debugging para verificar estados de animación del arma.
 * Eliminar o comentar en producción.
 */
public class GunDebugOverlay implements IGuiOverlay {

    private static boolean SHOW_DEBUG = true; // Cambiar a false para desactivar

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!SHOW_DEBUG) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) return;

        ItemStack mainHand = minecraft.player.getMainHandItem();
        if (!(mainHand.getItem() instanceof BaseGunItem gun)) return;

        // Obtener datos del NBT usando métodos de instancia
        int ammo = gun.getAmmo(mainHand);
        boolean reloading = gun.isReloading(mainHand);

        // Obtener el ID del arma para estado de animación
        String gunId = "unknown";
        if (mainHand.hasTag()) {
            var tag = mainHand.getTag();
            if (tag != null && tag.contains("GunID")) {
                gunId = String.valueOf(tag.getInt("GunID"));
            }
        }

        // Obtener datos de animación del NBT (si existen)
        boolean shootActive = false;
        boolean reloadActive = false;

        // Para disparo, usar cooldown como referencia
        shootActive = minecraft.player.getCooldowns().isOnCooldown(gun);

        // Para recarga, usar estado de reloading y cooldown
        reloadActive = reloading && minecraft.player.getCooldowns().isOnCooldown(gun);

        // Determinar qué animación debería estar activa
        String currentAnim;
        int animColor;

        if (shootActive) {
            currentAnim = "SHOOT (active)";
            animColor = 0x00FF00;
        } else if (reloadActive) {
            currentAnim = "RELOAD (active)";
            animColor = 0xFFFF00;
        } else if (ammo <= 0 && !reloading) {
            currentAnim = "IDLE_UNLOADED";
            animColor = 0xFF6600;
        } else if (ammo <= 0) {
            currentAnim = "EMPTY";
            animColor = 0xFF0000;
        } else {
            currentAnim = "IDLE";
            animColor = 0x888888;
        }

        // Posición del debug HUD (esquina superior izquierda)
        int x = 10;
        int y = 10;
        int lineHeight = 10;

        // Fondo semi-transparente (ajustado para más líneas)
        int lines = 12;
        guiGraphics.fill(x - 2, y - 2, x + 250, y + (lines * lineHeight) + 2, 0x80000000);

        // Información de estado
        guiGraphics.drawString(minecraft.font, "=== GUN DEBUG OVERLAY ===", x, y, 0xFFFF00);
        y += lineHeight;

        // ID del arma
        guiGraphics.drawString(minecraft.font, "Gun ID: " + gunId, x, y, 0xAAAAAA);
        y += lineHeight;

        // Munición
        guiGraphics.drawString(minecraft.font, String.format("Ammo: %d/%d", ammo, gun.getMaxAmmo()),
                x, y, ammo > 0 ? 0x00FF00 : (ammo == 0 ? 0xFF0000 : 0xFF8800));
        y += lineHeight;

        // Estados booleanos
        guiGraphics.drawString(minecraft.font, "Reloading: " + reloading,
                x, y, reloading ? 0xFFFF00 : 0x888888);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, "Cooldown Active: " + minecraft.player.getCooldowns().isOnCooldown(gun),
                x, y, minecraft.player.getCooldowns().isOnCooldown(gun) ? 0xFF0000 : 0x00FF00);
        y += lineHeight;

        // Animaciones activas
        guiGraphics.drawString(minecraft.font, "Shoot Anim: " + (shootActive ? "ACTIVE" : "inactive"),
                x, y, shootActive ? 0x00FF00 : 0x888888);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, "Reload Anim: " + (reloadActive ? "ACTIVE" : "inactive"),
                x, y, reloadActive ? 0xFFFF00 : 0x888888);
        y += lineHeight;

        // Parámetros del arma
        guiGraphics.drawString(minecraft.font, String.format("Fire Rate: %d ticks (%.1fs)",
                        gun.getFireRate(), gun.getFireRate() / 20.0f),
                x, y, 0xAAAAAA);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, String.format("Reload Time: %d ticks (%.1fs)",
                        gun.getReloadTime(), gun.getReloadTime() / 20.0f),
                x, y, 0xAAAAAA);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, String.format("Damage: %.1f", gun.getDamage()),
                x, y, 0xAAAAAA);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, String.format("Accuracy: %.2f", gun.getAccuracy()),
                x, y, 0xAAAAAA);
        y += lineHeight;

        // Animación actual (lo más importante)
        guiGraphics.drawString(minecraft.font, "Current Animation: " + currentAnim,
                x, y, animColor);
    }

    // Reservado para futuros Keys
    public static void toggleDebug() {
        SHOW_DEBUG = !SHOW_DEBUG;
    }
}