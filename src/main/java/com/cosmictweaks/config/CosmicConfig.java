package com.cosmictweaks.config;

public class CosmicConfig {
    public boolean moduleHudEditor = true;
    public boolean showKeystrokes = true;
    public boolean showCps = true;
    public boolean showFps = true;
    public boolean showPing = true;
    public boolean showCoordinates = true;
    public boolean showArmor = true;
    public boolean showPotions = true;
    public boolean showDurability = true;
    public boolean showClock = true;
    public boolean showZoom = true;
    public boolean showCrosshair = true;
    public boolean showCompass = true;
    public boolean showItemHud = true;
    public boolean showScoreboard = true;
    public boolean showChat = true;
    public boolean showWaypoints = false;
    public boolean fullbright = false;
    public boolean sprintToggle = true;
    public boolean sneakToggle = true;
    public boolean freelook = false;

    public static final CosmicConfig INSTANCE = new CosmicConfig();

    public static void applyDefaults() {
        // Defaults are already defined in the instance.
    }

    public static CosmicConfig get() {
        return INSTANCE;
    }
}
