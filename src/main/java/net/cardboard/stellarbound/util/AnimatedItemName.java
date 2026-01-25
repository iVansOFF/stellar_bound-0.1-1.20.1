package net.cardboard.stellarbound.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.awt.Color;

public class AnimatedItemName {

    /**
     * Crea un nombre con gradiente de olas animado
     * @param text El texto del nombre
     * @param time Tiempo en ticks (usa System.currentTimeMillis() / 50 para efecto suave)
     * @param colors Array de colores para el gradiente (mínimo 2)
     * @return Component con el nombre animado
     */
    public static Component createWaveGradient(String text, long time, Color... colors) {
        if (colors.length < 2) {
            colors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA};
        }

        Component result = Component.empty();

        for (int i = 0; i < text.length(); i++) {
            // Calcula la posición de la ola para esta letra
            double wave = Math.sin((time + i * 0.5) * 0.1) * 0.5 + 0.5; // 0-1

            // Extiende el rango para ciclar entre todos los colores
            double colorPos = (wave + (time * 0.01)) % 1.0;
            double scaledPos = colorPos * (colors.length - 1);

            int colorIndex = (int) scaledPos;
            double blend = scaledPos - colorIndex;

            // Asegura que no se salga del array
            int nextIndex = Math.min(colorIndex + 1, colors.length - 1);

            // Interpola entre dos colores
            Color color1 = colors[colorIndex];
            Color color2 = colors[nextIndex];

            int r = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * blend);
            int g = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * blend);
            int b = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * blend);

            // Convierte a formato hexadecimal
            String hexColor = String.format("#%02x%02x%02x", r, g, b);

            // Añade la letra con su color
            result = result.copy().append(
                    Component.literal(String.valueOf(text.charAt(i)))
                            .setStyle(Style.EMPTY.withColor(net.minecraft.network.chat.TextColor.parseColor(hexColor)))
            );
        }

        return result;
    }

    /**
     * Gradiente de fuego (rojo -> naranja -> amarillo)
     */
    public static Component fireGradient(String text, long time) {
        return createWaveGradient(text, time,
                new Color(255, 0, 0),      // Rojo
                new Color(255, 100, 0),    // Naranja rojizo
                new Color(255, 165, 0),    // Naranja
                new Color(255, 215, 0),    // Dorado
                new Color(255, 255, 100)   // Amarillo claro
        );
    }

    /**
     * Gradiente de agua (azul -> cyan -> blanco)
     */
    public static Component waterGradient(String text, long time) {
        return createWaveGradient(text, time,
                new Color(0, 0, 139),      // Azul oscuro
                new Color(0, 100, 200),    // Azul
                new Color(0, 191, 255),    // Azul claro
                new Color(64, 224, 208),   // Turquesa
                new Color(175, 238, 238)   // Azul pálido
        );
    }

    /**
     * Gradiente arcoíris completo
     */
    public static Component rainbowGradient(String text, long time) {
        return createWaveGradient(text, time,
                new Color(255, 0, 0),      // Rojo
                new Color(255, 127, 0),    // Naranja
                new Color(255, 255, 0),    // Amarillo
                new Color(0, 255, 0),      // Verde
                new Color(0, 191, 255),    // Azul
                new Color(75, 0, 130),     // Índigo
                new Color(148, 0, 211)     // Violeta
        );
    }

    /**
     * Gradiente mágico (púrpura -> rosa -> azul)
     */
    public static Component magicGradient(String text, long time) {
        return createWaveGradient(text, time,
                new Color(138, 43, 226),   // Púrpura
                new Color(186, 85, 211),   // Orquídea
                new Color(218, 112, 214),  // Rosa orquídea
                new Color(147, 112, 219),  // Púrpura medio
                new Color(123, 104, 238)   // Azul pizarra medio
        );
    }

    /**
     * Gradiente de veneno (verde -> lima -> amarillo verdoso)
     */
    public static Component poisonGradient(String text, long time) {
        return createWaveGradient(text, time,
                new Color(34, 139, 34),    // Verde bosque
                new Color(50, 205, 50),    // Verde lima
                new Color(124, 252, 0),    // Verde césped
                new Color(173, 255, 47),   // Verde amarillo
                new Color(154, 205, 50)    // Amarillo verde
        );
    }

    /**
     * Gradiente de sangre (rojo oscuro -> rojo -> carmesí)
     */
    public static Component bloodGradient(String text, long time) {
        return createWaveGradient(text, time,
                new Color(139, 0, 0),      // Rojo oscuro
                new Color(178, 34, 34),    // Rojo ladrillo
                new Color(220, 20, 60),    // Carmesí
                new Color(255, 0, 0),      // Rojo
                new Color(205, 92, 92)     // Rojo indio
        );
    }

    /**
     * Gradiente celestial (blanco -> dorado -> azul cielo)
     */
    public static Component celestialGradient(String text, long time) {
        return createWaveGradient(text, time,
                new Color(255, 255, 255),  // Blanco
                new Color(255, 250, 205),  // Amarillo limón
                new Color(255, 215, 0),    // Dorado
                new Color(135, 206, 235),  // Azul cielo
                new Color(176, 224, 230)   // Azul polvo
        );
    }
}