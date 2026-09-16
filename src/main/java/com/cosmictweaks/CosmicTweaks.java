package com.cosmictweaks;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CosmicTweaks implements ModInitializer {
    public static final String MOD_ID = "cosmictweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Cosmic Tweaks initialized.");
    }
}
