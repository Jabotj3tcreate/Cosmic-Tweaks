package com.cosmictweaks;

import com.cosmictweaks.config.CosmicConfig;
import com.cosmictweaks.hud.HudRenderer;
import com.cosmictweaks.keybind.Keybinds;
import net.fabricmc.api.ClientModInitializer;

public class CosmicTweaksClient implements ClientModInitializer {
    public static final CosmicConfig CONFIG = CosmicConfig.get();

    @Override
    public void onInitializeClient() {
        CosmicConfig.applyDefaults();
        Keybinds.register();
        HudRenderer.register();
        CosmicTweaks.LOGGER.info("Cosmic Tweaks client initialized.");
    }
}
