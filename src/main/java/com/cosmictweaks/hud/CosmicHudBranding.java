package com.cosmictweaks.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public final class CosmicHudBranding {
    private CosmicHudBranding() {}

    public static void render(DrawContext context, MinecraftClient client) {
        if (client == null) return;
        String label = "COSMIC TWEAKS";
        int width = client.textRenderer.getWidth(label);
        int x = client.getWindow().getScaledWidth() - width - 8;
        int y = client.getWindow().getScaledHeight() - 16;
        context.drawText(client.textRenderer, Text.literal(label), x, y, 0xFFB9C7D9, true);
    }
}
