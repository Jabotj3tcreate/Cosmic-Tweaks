package com.cosmictweaks.screen;

import com.cosmictweaks.CosmicTweaksClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CosmicSettingsScreen extends Screen {
    private final Screen parent;

    public CosmicSettingsScreen(Screen parent) {
        super(Text.literal("Cosmic Tweaks Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = 30;

        addDrawableChild(ButtonWidget.builder(Text.literal("Toggle Fullbright"), button -> {
            CosmicTweaksClient.CONFIG.fullbright = !CosmicTweaksClient.CONFIG.fullbright;
        }).dimensions(centerX - 100, y, 200, 20).build());

        y += 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("Toggle HUD"), button -> {
            CosmicTweaksClient.CONFIG.moduleHudEditor = !CosmicTweaksClient.CONFIG.moduleHudEditor;
        }).dimensions(centerX - 100, y, 200, 20).build());

        y += 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("Coordinates: " + (CosmicTweaksClient.CONFIG.showCoordinates ? "ON" : "OFF")), button -> {
            CosmicTweaksClient.CONFIG.showCoordinates = !CosmicTweaksClient.CONFIG.showCoordinates;
            button.setMessage(Text.literal("Coordinates: " + (CosmicTweaksClient.CONFIG.showCoordinates ? "ON" : "OFF")));
        }).dimensions(centerX - 100, y, 200, 20).build());

        y += 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("FPS: " + (CosmicTweaksClient.CONFIG.showFps ? "ON" : "OFF")), button -> {
            CosmicTweaksClient.CONFIG.showFps = !CosmicTweaksClient.CONFIG.showFps;
            button.setMessage(Text.literal("FPS: " + (CosmicTweaksClient.CONFIG.showFps ? "ON" : "OFF")));
        }).dimensions(centerX - 100, y, 200, 20).build());

        y += 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> {
            if (this.client != null) {
                this.client.setScreen(parent);
            }
        }).dimensions(centerX - 100, y, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }
}
