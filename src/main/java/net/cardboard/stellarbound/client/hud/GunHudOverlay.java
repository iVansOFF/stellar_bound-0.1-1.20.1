package net.cardboard.stellarbound.client.hud;

import net.cardboard.stellarbound.item.weapon.gun.BaseGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class GunHudOverlay implements IGuiOverlay {

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) return;

        ItemStack mainHand = minecraft.player.getMainHandItem();
        ItemStack offHand = minecraft.player.getOffhandItem();

        // Buscar arma en las manos
        ItemStack gunStack = ItemStack.EMPTY;
        if (mainHand.getItem() instanceof BaseGunItem) {
            gunStack = mainHand;
        } else if (offHand.getItem() instanceof BaseGunItem) {
            gunStack = offHand;
        }

        if (!gunStack.isEmpty()) {
            BaseGunItem gun = (BaseGunItem) gunStack.getItem();

            // Usar métodos de instancia en lugar de estáticos
            int ammo = gun.getAmmo(gunStack);
            boolean reloading = gun.isReloading(gunStack);

            // Usar el cooldown del ItemCooldownManager
            boolean isOnCooldown = minecraft.player.getCooldowns().isOnCooldown(gun);
            float cooldownPercent = 0.0f;

            if (isOnCooldown) {
                // Calcular el porcentaje del cooldown restante
                cooldownPercent = minecraft.player.getCooldowns().getCooldownPercent(gun, partialTick);
            }

            // Posición del HUD
            int x = screenWidth / 2 + 10;
            int y = screenHeight - 60;

            // Fondo
            guiGraphics.fill(x - 5, y - 5, x + 45, y + 25, 0x80000000);

            // Texto de munición
            String ammoText = ammo + "/" + gun.getMaxAmmo();
            guiGraphics.drawString(minecraft.font, ammoText, x, y,
                    ammo > 0 ? 0xFFFFFF : 0xFF0000);

            // Estado
            if (reloading) {
                String reloadText = "Reloading...";
                guiGraphics.drawString(minecraft.font, reloadText, x, y + 12, 0xFFFF00);

                // Barra de progreso basada en el cooldown
                float progress = 1.0f - cooldownPercent;
                int barWidth = (int)(40 * progress);
                guiGraphics.fill(x, y + 22, x + barWidth, y + 24, 0xFF00FF00);

                // Barra de fondo
                guiGraphics.fill(x, y + 22, x + 40, y + 24, 0xFF555555);
            } else if (isOnCooldown) {
                // Mostrar cooldown de disparo
                String cooldownText = String.format("Cooldown: %.1fs",
                        (cooldownPercent * gun.getFireRate()) / 20.0f);
                guiGraphics.drawString(minecraft.font, cooldownText, x, y + 12, 0xFF5555);

                // Barra de cooldown
                float progress = 1.0f - cooldownPercent;
                int barWidth = (int)(40 * progress);
                guiGraphics.fill(x, y + 22, x + barWidth, y + 24, 0xFF5555FF);

                // Barra de fondo
                guiGraphics.fill(x, y + 22, x + 40, y + 24, 0xFF555555);
            }
        }
    }
}