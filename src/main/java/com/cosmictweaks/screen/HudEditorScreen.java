package com.cosmictweaks.screen;

import com.cosmictweaks.hud.HudLayout;
import com.cosmictweaks.hud.HudLayout.ModulePosition;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class HudEditorScreen extends Screen {
    private final Screen parent;
    private String dragging;
    private double dragOffsetX;
    private double dragOffsetY;

    public HudEditorScreen(Screen parent) {
        super(Text.literal("Cosmic Tweaks HUD Editor"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xF20A0D12);
        context.drawTextWithShadow(textRenderer, Text.literal("COSMIC TWEAKS  /  HUD EDITOR"), 14, 14, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Drag modules to reposition  •  ESC to save"), 14, 28, 0xFF9AA7B8);

        drawModule(context, "FPS", "FPS  000", mouseX, mouseY, 12, 54);
        drawModule(context, "PING", "PING  0ms", mouseX, mouseY, 12, 78);
        drawModule(context, "COORDINATES", "XYZ  0  0  0", mouseX, mouseY, 12, 102);
        drawModule(context, "KEYSTROKES", "W  A S D", mouseX, mouseY, 12, 126);
        drawModule(context, "CPS", "LMB  0  /  RMB  0", mouseX, mouseY, 12, 150);
        drawModule(context, "EQUIPMENT", "ARMOR / DURABILITY", mouseX, mouseY, width - 150, height - 110);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawModule(DrawContext context, String id, String label, int mouseX, int mouseY, int defaultX, int defaultY) {
        ModulePosition pos = HudLayout.get(id, defaultX, defaultY);
        int x = (int) Math.round(pos.x);
        int y = (int) Math.round(pos.y);
        int w = Math.max(76, textRenderer.getWidth(label) + 16);
        int h = 20;
        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
        context.fill(x, y, x + w, y + h, hovered ? 0xFF263746 : 0xCC141A22);
        context.fill(x, y, x + w, y + 1, hovered ? 0xFF78C7E8 : 0xFF3B4655);
        context.drawTextWithShadow(textRenderer, Text.literal(label), x + 8, y + 6, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);
        String[] ids = {"FPS", "PING", "COORDINATES", "KEYSTROKES", "CPS", "EQUIPMENT"};
        int[] xs = {12, 12, 12, 12, 12, width - 150};
        int[] ys = {54, 78, 102, 126, 150, height - 110};
        String[] labels = {"FPS  000", "PING  0ms", "XYZ  0  0  0", "W  A S D", "LMB  0  /  RMB  0", "ARMOR / DURABILITY"};
        for (int i = 0; i < ids.length; i++) {
            ModulePosition pos = HudLayout.get(ids[i], xs[i], ys[i]);
            int x = (int) Math.round(pos.x);
            int y = (int) Math.round(pos.y);
            int w = Math.max(76, textRenderer.getWidth(labels[i]) + 16);
            if (inside(click.x(), click.y(), x, y, w, 20)) {
                dragging = ids[i];
                dragOffsetX = click.x() - pos.x;
                dragOffsetY = click.y() - pos.y;
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double offsetX, double offsetY) {
        if (dragging != null) {
            ModulePosition pos = HudLayout.modules().get(dragging);
            if (pos != null) {
                pos.x = Math.max(4, Math.min(width - 8, click.x() - dragOffsetX));
                pos.y = Math.max(42, Math.min(height - 24, click.y() - dragOffsetY));
            }
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        if (click.button() == 0 && dragging != null) {
            dragging = null;
            HudLayout.save();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (input.key() == 256) {
            HudLayout.save();
            if (client != null) client.setScreen(parent);
            return true;
        }
        return super.keyPressed(input);
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }
}
