package com.cosmictweaks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cosmictweaks.CosmicTweaks;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

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

    // Performance profile toggles. These are deliberately opt-in until their
    // client-specific render hooks are installed, so enabling them cannot
    // unexpectedly change vanilla rendering.
    public boolean performanceProfile = false;
    public boolean reduceParticles = false;
    public boolean reduceEntityRenderDistance = false;
    public boolean hideCosmetics = false;

    public double zoomFov = 30.0;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("cosmictweaks.json");
    private static final CosmicConfig INSTANCE = load();

    public static void applyDefaults() {
        save();
    }

    public static CosmicConfig get() {
        return INSTANCE;
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            CosmicTweaks.LOGGER.warn("Could not save Cosmic Tweaks config.", e);
        }
    }

    private static CosmicConfig load() {
        if (!Files.exists(FILE)) {
            return new CosmicConfig();
        }
        try {
            CosmicConfig config = GSON.fromJson(Files.readString(FILE), CosmicConfig.class);
            return config != null ? config : new CosmicConfig();
        } catch (Exception e) {
            CosmicTweaks.LOGGER.warn("Could not load Cosmic Tweaks config; using defaults.", e);
            return new CosmicConfig();
        }
    }
}
