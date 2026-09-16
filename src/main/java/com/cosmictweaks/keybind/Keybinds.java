package com.cosmictweaks.keybind;

import com.cosmictweaks.CosmicTweaks;
import com.cosmictweaks.screen.CosmicSettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {
    public static KeyBinding toggleFullbright;
    public static KeyBinding toggleZoom;
    public static KeyBinding openSettings;

    private Keybinds() {}

    public static void register() {
        toggleFullbright = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.fullbright",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F,
                "category.cosmictweaks"
        ));

        toggleZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "category.cosmictweaks"
        ));

        openSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.settings",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.cosmictweaks"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleFullbright.wasPressed()) {
                CosmicTweaks.LOGGER.info("Toggle fullbright pressed");
                client.options.getGamma().setValue(client.options.getGamma().getValue() > 1.0 ? 1.0 : 10.0);
            }

            if (toggleZoom.wasPressed()) {
                CosmicTweaks.LOGGER.info("Toggle zoom pressed");
            }

            if (openSettings.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new CosmicSettingsScreen(MinecraftClient.getInstance().currentScreen));
            }
        });
    }
}
