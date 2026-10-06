package com.cosmictweaks.screen;

import com.cosmictweaks.CosmicTweaksClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class CosmicSettingsScreen extends Screen {
    private final Screen parent;
    private static final int PANEL_WIDTH = 360;
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 28;
    private static final int GAP = 8;

    public CosmicSettingsScreen(Screen parent) {
        super(Text.literal("Cosmic Tweaks"));
        this.parent = parent;
    }

    @Override
    protected void init() {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xE90A0D12);
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(12, (this.height - 280) / 2);

        context.fill(left, top, left + PANEL_WIDTH, top + 280, 0xF0121720);
        context.fill(left, top, left + PANEL_WIDTH, top + 2, 0xFFB9C7D9);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("COSMIC TWEAKS"),
                this.width / 2, top + 16, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Client Settings"),
                this.width / 2, top + 32, 0xFF9AA7B8);

        int y = top + 52;
        y = drawToggle(context, mouseX, mouseY, left, y, "Fullbright", CosmicTweaksClient.CONFIG.fullbright);
        y = drawToggle(context, mouseX, mouseY, left, y, "FPS HUD", CosmicTweaksClient.CONFIG.showFps);
        y = drawToggle(context, mouseX, mouseY, left, y, "Coordinates", CosmicTweaksClient.CONFIG.showCoordinates);
        y = drawToggle(context, mouseX, mouseY, left, y, "HUD Editor", CosmicTweaksClient.CONFIG.moduleHudEditor);
        y = drawToggle(context, mouseX, mouseY, left, y, "Zoom", CosmicTweaksClient.CONFIG.showZoom);
        drawButton(context, mouseX, mouseY, left + (PANEL_WIDTH - BUTTON_WIDTH) / 2, y, "BACK");

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Cosmic Tweaks 1.0.0"),
                this.width / 2, top + 260, 0xFF778395);
    }

    private int drawToggle(DrawContext context, int mouseX, int mouseY, int panelLeft, int y,
                           String name, boolean enabled) {
        int x = panelLeft + (PANEL_WIDTH - BUTTON_WIDTH) / 2;
        drawButton(context, mouseX, mouseY, x, y, name + "  [" + (enabled ? "ON" : "OFF") + "]");
        return y + BUTTON_HEIGHT + GAP;
    }

    private void drawButton(DrawContext context, int mouseX, int mouseY, int x, int y, String label) {
        boolean hovered = mouseX >= x && mouseX < x + BUTTON_WIDTH
                && mouseY >= y && mouseY < y + BUTTON_HEIGHT;
        int fill = hovered ? 0xFF26313F : 0xFF1A222D;
        int border = hovered ? 0xFFB9C7D9 : 0xFF3B4655;
        context.fill(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, fill);
        context.fill(x, y, x + BUTTON_WIDTH, y + 1, border);
        context.fill(x, y + BUTTON_HEIGHT - 1, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, border);
        context.fill(x, y, x + 1, y + BUTTON_HEIGHT, border);
        context.fill(x + BUTTON_WIDTH - 1, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, border);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(label),
                x + BUTTON_WIDTH / 2, y + 10, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(12, (this.height - 280) / 2);
        int x = left + (PANEL_WIDTH - BUTTON_WIDTH) / 2;
        int y = top + 52;

        if (inside(mouseX, mouseY, x, y)) {
            CosmicTweaksClient.CONFIG.fullbright = !CosmicTweaksClient.CONFIG.fullbright; return true;
        }
        y += BUTTON_HEIGHT + GAP;
        if (inside(mouseX, mouseY, x, y)) {
            CosmicTweaksClient.CONFIG.showFps = !CosmicTweaksClient.CONFIG.showFps; return true;
        }
        y += BUTTON_HEIGHT + GAP;
        if (inside(mouseX, mouseY, x, y)) {
            CosmicTweaksClient.CONFIG.showCoordinates = !CosmicTweaksClient.CONFIG.showCoordinates; return true;
        }
        y += BUTTON_HEIGHT + GAP;
        if (inside(mouseX, mouseY, x, y)) {
            CosmicTweaksClient.CONFIG.moduleHudEditor = !CosmicTweaksClient.CONFIG.moduleHudEditor; return true;
        }
        y += BUTTON_HEIGHT + GAP;
        if (inside(mouseX, mouseY, x, y)) {
            CosmicTweaksClient.CONFIG.showZoom = !CosmicTweaksClient.CONFIG.showZoom; return true;
        }
        y += BUTTON_HEIGHT + GAP;
        if (inside(mouseX, mouseY, x, y)) {
            close(); return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void close() {
        if (this.client != null) this.client.setScreen(parent);
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + BUTTON_WIDTH
                && mouseY >= y && mouseY < y + BUTTON_HEIGHT;
    }
}
