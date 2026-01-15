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

        // Obtener los ticks de animación (ahora son long)
        long shootTick = mainHand.getOrCreateTag().getLong("ShootTick");
        long reloadStartTick = mainHand.getOrCreateTag().getLong("ReloadStartTick");
        long currentTick = minecraft.level != null ? minecraft.level.getGameTime() : 0;

        // Constantes de duración (deben coincidir con las de BaseGunItem)
        final int SHOOT_ANIMATION_DURATION = 5;  // 0.25 segundos
        final int RELOAD_ANIMATION_DURATION = 40; // 2 segundos

        boolean shootActive = shootTick > 0 && (currentTick - shootTick) < SHOOT_ANIMATION_DURATION;
        boolean reloadActive = reloadStartTick > 0 && reloading && (currentTick - reloadStartTick) < RELOAD_ANIMATION_DURATION;

        // Calcular tiempos restantes (en segundos)
        float shootTimeLeft = shootActive ? (SHOOT_ANIMATION_DURATION - (currentTick - shootTick)) / 20.0f : 0;
        float reloadTimeLeft = reloadActive ? (RELOAD_ANIMATION_DURATION - (currentTick - reloadStartTick)) / 20.0f : 0;

        // Determinar qué animación debería estar activa
        String currentAnim = "IDLE";
        int animColor = 0x888888;

        if (shootActive) {
            currentAnim = "SHOOT (" + String.format("%.2f", shootTimeLeft) + "s)";
            animColor = 0x00FF00;
        } else if (reloadActive) {
            currentAnim = "RELOAD (" + String.format("%.2f", reloadTimeLeft) + "s)";
            animColor = 0xFFFF00;
        } else if (ammo <= 0 && !reloading) {
            currentAnim = "IDLE_UNLOADED";
            animColor = 0xFF6600;
        } else if (ammo <= 0) {
            currentAnim = "EMPTY";
            animColor = 0xFF0000;
        }

        // Posición del debug HUD (esquina superior izquierda)
        int x = 10;
        int y = 10;
        int lineHeight = 10;

        // Fondo semi-transparente (ajustado para más líneas)
        int lines = 13;
        guiGraphics.fill(x - 2, y - 2, x + 250, y + (lines * lineHeight) + 2, 0x80000000);

        // Información de estado
        guiGraphics.drawString(minecraft.font, "=== GUN DEBUG OVERLAY ===", x, y, 0xFFFF00);
        y += lineHeight;

        // Munición
        guiGraphics.drawString(minecraft.font, String.format("Ammo: %d/%d", ammo, gun.getMaxAmmo()),
                x, y, ammo > 0 ? 0x00FF00 : (ammo == 0 ? 0xFF0000 : 0xFF8800));
        y += lineHeight;

        // Estados booleanos
        guiGraphics.drawString(minecraft.font, "Reloading State: " + reloading,
                x, y, reloading ? 0xFFFF00 : 0x888888);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, "Cooldown Active: " + minecraft.player.getCooldowns().isOnCooldown(gun),
                x, y, minecraft.player.getCooldowns().isOnCooldown(gun) ? 0xFF0000 : 0x00FF00);
        y += lineHeight;

        // Ticks de animación
        guiGraphics.drawString(minecraft.font, String.format("Shoot Tick: %d (Current: %d)", shootTick, currentTick),
                x, y, shootActive ? 0x00FF00 : 0x888888);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, String.format("Reload Tick: %d", reloadStartTick),
                x, y, reloadActive ? 0xFFFF00 : 0x888888);
        y += lineHeight;

        // Animaciones activas
        guiGraphics.drawString(minecraft.font, "Shoot Anim Active: " + (shootActive ? "YES" : "no"),
                x, y, shootActive ? 0x00FF00 : 0x888888);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, "Reload Anim Active: " + (reloadActive ? "YES" : "no"),
                x, y, reloadActive ? 0xFFFF00 : 0x888888);
        y += lineHeight;

        // Duración restante
        if (shootActive) {
            guiGraphics.drawString(minecraft.font, String.format("Shoot ends in: %.2fs", shootTimeLeft),
                    x, y, 0x00FF00);
            y += lineHeight;
        }

        if (reloadActive) {
            guiGraphics.drawString(minecraft.font, String.format("Reload ends in: %.2fs", reloadTimeLeft),
                    x, y, 0xFFFF00);
            y += lineHeight;
        }

        // Parámetros del arma
        guiGraphics.drawString(minecraft.font, String.format("Fire Rate: %d ticks (%.1fs)",
                        gun.getFireRate(), gun.getFireRate() / 20.0f),
                x, y, 0xAAAAAA);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, String.format("Reload Time: %d ticks (%.1fs)",
                        gun.getReloadTime(), gun.getReloadTime() / 20.0f),
                x, y, 0xAAAAAA);
        y += lineHeight;

        // Animación actual (lo más importante)
        guiGraphics.drawString(minecraft.font, "Current Animation: " + currentAnim,
                x, y, animColor);
        y += lineHeight;

        // Último ItemStack renderizado
        ItemStack lastRendered = BaseGunItem.getLastRenderedStack();
        boolean hasLastRendered = lastRendered != null && !lastRendered.isEmpty();
        guiGraphics.drawString(minecraft.font, "Render Stack: " + (hasLastRendered ? "SET" : "empty"),
                x, y, hasLastRendered ? 0x00FFFF : 0x888888);
    }

    // Método para activar/desactivar desde tecla
    public static void toggleDebug() {
        SHOW_DEBUG = !SHOW_DEBUG;
    }
}