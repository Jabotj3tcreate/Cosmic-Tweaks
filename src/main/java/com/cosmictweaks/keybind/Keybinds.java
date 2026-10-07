package com.cosmictweaks.keybind;

import com.cosmictweaks.CosmicTweaks;
import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.config.CosmicConfig;
import com.cosmictweaks.screen.CosmicSettingsScreen;
import com.cosmictweaks.screen.HudEditorScreen;
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
    public static KeyBinding openHudEditor;
    public static KeyBinding freelook;

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of(CosmicTweaks.MOD_ID, "main"));
    private static int savedFov = -1;

    private Keybinds() {}

    public static void register() {
        toggleFullbright = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F, CATEGORY));
        toggleZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY));
        openSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, CATEGORY));
        openHudEditor = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.hud_editor", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY));
        freelook = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cosmictweaks.freelook", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleFullbright.wasPressed()) {
                CosmicTweaksClient.CONFIG.fullbright = !CosmicTweaksClient.CONFIG.fullbright;
                applyFullbright(client);
                CosmicConfig.save();
            }

            boolean zooming = CosmicTweaksClient.CONFIG.showZoom && toggleZoom.isPressed();
            if (zooming && client.currentScreen == null) {
                if (savedFov < 0) savedFov = client.options.getFov().getValue();
                int targetFov = (int) Math.round(CosmicTweaksClient.CONFIG.zoomFov);
                targetFov = Math.max(20, Math.min(savedFov, targetFov));
                client.options.getFov().setValue(targetFov);
            } else if (savedFov >= 0) {
                client.options.getFov().setValue(savedFov);
                savedFov = -1;
            }

            if (openSettings.wasPressed() && client.currentScreen == null) {
                client.setScreen(new CosmicSettingsScreen(null));
            }

            if (openHudEditor.wasPressed() && client.currentScreen == null) {
                client.setScreen(new HudEditorScreen(null));
            }
        });
    }

    private static void applyFullbright(MinecraftClient client) {
        client.options.getGamma().setValue(CosmicTweaksClient.CONFIG.fullbright ? 10.0 : 1.0);
    }
}
