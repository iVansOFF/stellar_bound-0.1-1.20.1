package net.cardboard.stellarbound.client.hud;

import net.cardboard.stellarbound.client.ClientManaData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ManaOverlay implements IGuiOverlay {
    private static final ResourceLocation MANA_BAR = ResourceLocation.fromNamespaceAndPath(
            "stellarbound", "textures/gui/mana_bar.png"
    );

    // CONFIGURACIÓN DE LA TEXTURA - AJUSTA ESTOS VALORES
    private static final int TEXTURE_WIDTH = 98;
    private static final int TEXTURE_HEIGHT = 38;

    // Coordenadas del FONDO en la textura
    private static final int BACKGROUND_X = 0;
    private static final int BACKGROUND_Y = 0;
    private static final int BACKGROUND_WIDTH = 98;
    private static final int BACKGROUND_HEIGHT = 17;

    // Coordenadas del RELLENO en la textura
    private static final int FILL_X = 0;
    private static final int FILL_Y = 21;  // Cambia este valor para mover el relleno verticalmente
    private static final int FILL_WIDTH = 98;
    private static final int FILL_HEIGHT = 17;

    // Tamaño en pantalla
    private static final int SCREEN_WIDTH = 98;
    private static final int SCREEN_HEIGHT = 17;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.getConnection() == null) return;

        float mana = ClientManaData.getMana();
        float maxMana = ClientManaData.getMaxMana();

        if (maxMana <= 0) return;

        // Posición en pantalla
        int x = (screenWidth - SCREEN_WIDTH) / 2;
        int y = screenHeight - 58; // Puedes ajustar este valor

        // 1. Dibujar el FONDO
        guiGraphics.blit(MANA_BAR,
                x, y,
                BACKGROUND_X, BACKGROUND_Y,
                SCREEN_WIDTH, SCREEN_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // 2. Calcular y dibujar el RELLENO
        float manaPercentage = mana / maxMana;
        int fillWidth = (int) (SCREEN_WIDTH * manaPercentage);

        if (fillWidth > 0) {
            // Cambia FILL_X si quieres que el relleno empiece en otra posición horizontal
            guiGraphics.blit(MANA_BAR,
                    x, y,
                    FILL_X, FILL_Y,  // ← Estas son las coordenadas importantes
                    fillWidth, SCREEN_HEIGHT,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT
            );
        }

        // 3. Texto
        String manaText = String.format("%.0f/%.0f", mana, maxMana);
        int textWidth = mc.font.width(manaText);
        int textX = x + (SCREEN_WIDTH - textWidth) / 2;
        int textY = y - 7;

        guiGraphics.drawString(mc.font, manaText, textX + 1, textY + 1, 0x000000, false);
        guiGraphics.drawString(mc.font, manaText, textX, textY, 0x00AAFF, false);
    }
}