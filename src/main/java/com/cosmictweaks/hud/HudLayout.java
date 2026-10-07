package com.cosmictweaks.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.cosmictweaks.CosmicTweaks;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;

public final class HudLayout {
    public static final class ModulePosition {
        public double x;
        public double y;
        public double scale = 1.0;
        public boolean visible = true;

        public ModulePosition() {}

        public ModulePosition(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("cosmictweaks-hud.json");
    private static final Map<String, ModulePosition> MODULES = load();

    private HudLayout() {}

    public static ModulePosition get(String id, double defaultX, double defaultY) {
        return MODULES.computeIfAbsent(id, key -> new ModulePosition(defaultX, defaultY));
    }

    public static Map<String, ModulePosition> modules() {
        return MODULES;
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(MODULES));
        } catch (IOException e) {
            CosmicTweaks.LOGGER.warn("Could not save HUD layout.", e);
        }
    }

    private static Map<String, ModulePosition> load() {
        if (!Files.exists(FILE)) return new LinkedHashMap<>();
        try {
            Map<String, ModulePosition> loaded = GSON.fromJson(Files.readString(FILE), LinkedHashMap.class);
            if (loaded != null) {
                // Gson's generic map cannot safely restore nested objects, so reload
                // through a typed map below.
            }
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<LinkedHashMap<String, ModulePosition>>() {}.getType();
            Map<String, ModulePosition> typed = GSON.fromJson(Files.readString(FILE), type);
            return typed != null ? typed : new LinkedHashMap<>();
        } catch (Exception e) {
            CosmicTweaks.LOGGER.warn("Could not load HUD layout; using defaults.", e);
            return new LinkedHashMap<>();
        }
    }
}
