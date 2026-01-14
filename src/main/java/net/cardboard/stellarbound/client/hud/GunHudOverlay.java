package net.cardboard.stellarbound.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.cardboard.stellarbound.item.weapon.gun.BaseGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class GunHudOverlay implements IGuiOverlay {

    private static final ResourceLocation HUD_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("stellarbound", "textures/gui/gun_hud.png");

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

        if (!gunStack.isEmpty() && minecraft.player.isUsingItem()) {
            BaseGunItem gun = (BaseGunItem) gunStack.getItem();
            int ammo = BaseGunItem.getAmmo(gunStack);
            boolean reloading = BaseGunItem.isReloading(gunStack);
            int cooldown = BaseGunItem.getCooldown(gunStack);

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

                // Barra de progreso
                float progress = 1.0f - ((float)cooldown / gun.getReloadTime());
                int barWidth = (int)(40 * progress);
                guiGraphics.fill(x, y + 22, x + barWidth, y + 24, 0xFF00FF00);
            } else if (cooldown > 0) {
                String cooldownText = "Cooldown: " + cooldown;
                guiGraphics.drawString(minecraft.font, cooldownText, x, y + 12, 0xFF5555);
            }
        }
    }
}