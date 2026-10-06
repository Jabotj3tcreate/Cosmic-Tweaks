package com.cosmictweaks.screen;

import com.cosmictweaks.CosmicTweaksClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class CosmicSettingsScreen extends Screen {
    private final Screen parent;

    public CosmicSettingsScreen(Screen parent) {
        super(Text.literal("Cosmic Tweaks"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int y = 36;
        int w = 220;
        int h = 20;

        addDrawableChild(ButtonWidget.builder(toggleText("Fullbright", CosmicTweaksClient.CONFIG.fullbright), b -> {
            CosmicTweaksClient.CONFIG.fullbright = !CosmicTweaksClient.CONFIG.fullbright;
            b.setMessage(toggleText("Fullbright", CosmicTweaksClient.CONFIG.fullbright));
        }).dimensions(center - w / 2, y, w, h).build());

        y += 25;
        addDrawableChild(ButtonWidget.builder(toggleText("FPS HUD", CosmicTweaksClient.CONFIG.showFps), b -> {
            CosmicTweaksClient.CONFIG.showFps = !CosmicTweaksClient.CONFIG.showFps;
            b.setMessage(toggleText("FPS HUD", CosmicTweaksClient.CONFIG.showFps));
        }).dimensions(center - w / 2, y, w, h).build());

        y += 25;
        addDrawableChild(ButtonWidget.builder(toggleText("Coordinates", CosmicTweaksClient.CONFIG.showCoordinates), b -> {
            CosmicTweaksClient.CONFIG.showCoordinates = !CosmicTweaksClient.CONFIG.showCoordinates;
            b.setMessage(toggleText("Coordinates", CosmicTweaksClient.CONFIG.showCoordinates));
        }).dimensions(center - w / 2, y, w, h).build());

        y += 25;
        addDrawableChild(ButtonWidget.builder(toggleText("HUD Editor", CosmicTweaksClient.CONFIG.moduleHudEditor), b -> {
            CosmicTweaksClient.CONFIG.moduleHudEditor = !CosmicTweaksClient.CONFIG.moduleHudEditor;
            b.setMessage(toggleText("HUD Editor", CosmicTweaksClient.CONFIG.moduleHudEditor));
        }).dimensions(center - w / 2, y, w, h).build());

        y += 25;
        addDrawableChild(ButtonWidget.builder(toggleText("Zoom", CosmicTweaksClient.CONFIG.showZoom), b -> {
            CosmicTweaksClient.CONFIG.showZoom = !CosmicTweaksClient.CONFIG.showZoom;
            b.setMessage(toggleText("Zoom", CosmicTweaksClient.CONFIG.showZoom));
        }).dimensions(center - w / 2, y, w, h).build());

        y += 25;
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> {
            if (this.client != null) this.client.setScreen(parent);
        }).dimensions(center - w / 2, y, w, h).build());
    }

    private static Text toggleText(String name, boolean enabled) {
        return Text.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
