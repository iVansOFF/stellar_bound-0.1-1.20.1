package net.cardboard.stellarbound.screen;

import net.cardboard.stellarbound.base.BaseProcessingScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InfuseForgeryScreen extends BaseProcessingScreen<InfuseForgeryMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("stellarbound", "textures/gui/infuse_forgery_gui.png");

    public InfuseForgeryScreen(InfuseForgeryMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected void renderProgressBar(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            // Barra de progreso: 4x27 píxeles
            guiGraphics.blit(TEXTURE, x + 146, y + 30, 176, 0, 4, menu.getScaledProgress(27));
        }
    }
}