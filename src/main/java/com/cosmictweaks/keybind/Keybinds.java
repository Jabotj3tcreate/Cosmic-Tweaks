package com.cosmictweaks.keybind;

import com.cosmictweaks.CosmicTweaks;
import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.config.CosmicConfig;
import com.cosmictweaks.screen.CosmicSettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
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
    private static double savedFov = -1.0;

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
                CosmicTweaksClient.CONFIG.fullbright = !CosmicTweaksClient.CONFIG.fullbright;
                applyFullbright(client);
                CosmicConfig.save();
            }

            boolean zooming = CosmicTweaksClient.CONFIG.showZoom && toggleZoom.isPressed();
            if (zooming && client.currentScreen == null) {
                if (savedFov < 0.0) savedFov = client.options.getFov().getValue();
                client.options.getFov().setValue(Math.max(20.0, Math.min(savedFov, CosmicTweaksClient.CONFIG.zoomFov)));
            } else if (savedFov >= 0.0) {
                client.options.getFov().setValue(savedFov);
                savedFov = -1.0;
            }

            if (openSettings.wasPressed() && !(client.currentScreen instanceof CosmicSettingsScreen)) {
                ScreenOpener.open(client);
            }
        });
    }

    private static void applyFullbright(MinecraftClient client) {
        client.options.getGamma().setValue(CosmicTweaksClient.CONFIG.fullbright ? 10.0 : 1.0);
    }

    private static final class ScreenOpener {
        private ScreenOpener() {}
        private static void open(MinecraftClient client) {
            Screen previous = client.currentScreen;
            client.setScreen(new CosmicSettingsScreen(previous));
        }
    }
}
