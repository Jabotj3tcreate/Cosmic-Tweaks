package com.cosmictweaks.config;

public class CosmicConfig {
    public boolean modularHud = true;
    public boolean showKeystrokes = true;
    public boolean showCps = true;
    public boolean showFps = true;
    public boolean showPing = true;
    public boolean showCoordinates = true;
    public boolean showArmor = true;
    public boolean showPotions = true;
    public boolean showDurability = true;
    public boolean showClock = true;
    public boolean showCompass = true;
    public boolean showItemHud = true;
    public boolean showCrosshair = true;
    public boolean showFullbright = false;
    public boolean toggleSprint = true;
    public boolean toggleSneak = true;
    public boolean showWaypoints = false;

    public static CosmicConfig INSTANCE = new CosmicConfig();

    public static void applyDefaults() {
        INSTANCE = new CosmicConfig();
    }

    public static CosmicConfig get() {
        return INSTANCE;
    }
}
