package com.cosmictweaks;

import com.cosmictweaks.config.CosmicConfig;
import com.cosmictweaks.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;

public class CosmicTweaksClient implements ClientModInitializer {
    public static final CosmicConfig CONFIG = new CosmicConfig();

    @Override
    public void onInitializeClient() {
        CosmicConfig.applyDefaults();
        HudRenderer.register();
        CosmicTweaks.LOGGER.info("Cosmic Tweaks client initialized.");
    }
}
