package com.cosmictweaks.module;

import com.cosmictweaks.CosmicTweaksClient;

public final class ModuleManager {
    private ModuleManager() {}

    public static boolean hudEditor() { return CosmicTweaksClient.CONFIG.moduleHudEditor; }
    public static boolean fps() { return CosmicTweaksClient.CONFIG.showFps; }
    public static boolean ping() { return CosmicTweaksClient.CONFIG.showPing; }
    public static boolean coordinates() { return CosmicTweaksClient.CONFIG.showCoordinates; }
    public static boolean keystrokes() { return CosmicTweaksClient.CONFIG.showKeystrokes; }
    public static boolean cps() { return CosmicTweaksClient.CONFIG.showCps; }
    public static boolean armor() { return CosmicTweaksClient.CONFIG.showArmor; }
    public static boolean durability() { return CosmicTweaksClient.CONFIG.showDurability; }
    public static boolean potions() { return CosmicTweaksClient.CONFIG.showPotions; }
    public static boolean crosshair() { return CosmicTweaksClient.CONFIG.showCrosshair; }
    public static boolean itemHud() { return CosmicTweaksClient.CONFIG.showItemHud; }
    public static boolean scoreboard() { return CosmicTweaksClient.CONFIG.showScoreboard; }
    public static boolean chat() { return CosmicTweaksClient.CONFIG.showChat; }
    public static boolean waypoints() { return CosmicTweaksClient.CONFIG.showWaypoints; }
    public static boolean freelook() { return CosmicTweaksClient.CONFIG.freelook; }
}
