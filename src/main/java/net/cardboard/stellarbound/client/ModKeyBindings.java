package net.cardboard.stellarbound.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    public static final String KEY_CATEGORY_STELLARBOUND = "key.categories.stellarbound";

    public static final KeyMapping RELOAD_KEY = new KeyMapping(
            "key.stellarbound.reload",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KEY_CATEGORY_STELLARBOUND
    );

    public static final KeyMapping SHOOT_KEY = new KeyMapping(
            "key.stellarbound.shoot",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            KEY_CATEGORY_STELLARBOUND
    );
}