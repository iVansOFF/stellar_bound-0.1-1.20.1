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

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) return;

        ItemStack mainHand = minecraft.player.getMainHandItem();
        if (!(mainHand.getItem() instanceof BaseGunItem gun)) return;

        // Obtener datos del NBT
        int ammo = BaseGunItem.getAmmo(mainHand);
        boolean reloading = BaseGunItem.isReloading(mainHand);

        long shootTime = mainHand.getOrCreateTag().getLong("ShootTimestamp");
        long reloadTime = mainHand.getOrCreateTag().getLong("ReloadStartTimestamp");
        long currentTime = System.currentTimeMillis();

        boolean shootActive = shootTime > 0 && (currentTime - shootTime) < 750;
        boolean reloadActive = reloadTime > 0 && (currentTime - reloadTime) < 2500;

        // Posición del debug HUD (esquina superior izquierda)
        int x = 10;
        int y = 10;
        int lineHeight = 10;

        // Fondo semi-transparente
        guiGraphics.fill(x - 2, y - 2, x + 200, y + 70, 0x80000000);

        // Información de estado
        guiGraphics.drawString(minecraft.font, "=== GUN DEBUG ===", x, y, 0xFFFF00);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, "Ammo: " + ammo + "/" + gun.getMaxAmmo(),
                x, y, ammo > 0 ? 0x00FF00 : 0xFF0000);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, "Reloading: " + reloading,
                x, y, reloading ? 0xFFFF00 : 0x888888);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, "Shoot Anim: " + (shootActive ? "ACTIVE" : "inactive"),
                x, y, shootActive ? 0x00FF00 : 0x888888);
        y += lineHeight;

        guiGraphics.drawString(minecraft.font, "Reload Anim: " + (reloadActive ? "ACTIVE" : "inactive"),
                x, y, reloadActive ? 0x00FF00 : 0x888888);
        y += lineHeight;

        // Determinar qué animación debería estar activa
        String currentAnim = "IDLE";
        if (shootActive) {
            currentAnim = "SHOOT";
        } else if (reloadActive) {
            currentAnim = "RELOAD";
        } else if (ammo <= 0 && !reloading) {
            currentAnim = "IDLE_UNLOADED";
        }

        guiGraphics.drawString(minecraft.font, "Animation: " + currentAnim,
                x, y, 0x00FFFF);
    }
}