package com.cosmictweaks.keybind;

import com.cosmictweaks.CosmicTweaks;
import com.cosmictweaks.screen.CosmicSettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {
    public static KeyBinding toggleFullbright;
    public static KeyBinding toggleZoom;
    public static KeyBinding openSettings;

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of(CosmicTweaks.MOD_ID, "main"));

    private Keybinds() {}

    public static void register() {
        toggleFullbright = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F, CATEGORY));

        toggleZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY));

        openSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleFullbright.wasPressed()) {
                client.options.getGamma().setValue(
                        client.options.getGamma().getValue() > 1.0 ? 1.0 : 10.0);
            }

            if (toggleZoom.wasPressed()) {
                CosmicTweaks.LOGGER.info("Zoom key pressed");
            }

            if (openSettings.wasPressed() && !(client.currentScreen instanceof CosmicSettingsScreen)) {
                ScreenOpener.open(client);
            }
        });
    }

    private static final class ScreenOpener {
        private ScreenOpener() {}

        private static void open(MinecraftClient client) {
            Screen previous = client.currentScreen;
            client.setScreen(new CosmicSettingsScreen(previous));
        }
    }
}
