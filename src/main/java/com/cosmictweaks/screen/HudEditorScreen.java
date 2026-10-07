package com.cosmictweaks.screen;

import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.hud.HudLayout;
import com.cosmictweaks.hud.HudLayout.ModulePosition;
import com.cosmictweaks.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class HudEditorScreen extends Screen {
    private final Screen parent;
    private String dragging;
    private double dragOffsetX;
    private double dragOffsetY;
    private String selected;
    private boolean snapToGrid = true;

    private static final int GRID = 8;
    private static final int HEADER = 52;
    private static final int FOOTER = 34;

    private record Entry(String id, String label, String preview, int defaultX, int defaultY, int width, int height) {}

    private List<Entry> entries() {
        int right = Math.max(180, width - 210);
        int bottom = Math.max(120, height - 110);
        return List.of(
                new Entry("FPS", "FPS", "FPS  144", 18, 70, 84, 22),
                new Entry("PING", "PING", "PING  42ms", 18, 98, 100, 22),
                new Entry("COORDINATES", "COORDINATES", "XYZ  120 64 -32", 18, 126, 154, 22),
                new Entry("KEYSTROKES", "KEYSTROKES", "W  A  S  D", 18, 154, 118, 42),
                new Entry("CPS", "CPS", "LMB  0  /  RMB  0", 18, 204, 148, 36),
                new Entry("EQUIPMENT", "EQUIPMENT", "ARMOR / DURABILITY", right, bottom, 164, 88),
                new Entry("FACING", "FACING", "FACING  N", 18, 248, 108, 22),
                new Entry("CLOCK", "CLOCK", "WORLD  12000", 18, 276, 116, 22)
        );
    }

    public HudEditorScreen(Screen parent) {
        super(Text.literal("Cosmic Tweaks HUD Editor"));
        this.parent = parent;
    }

    @Override
    protected void init() {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xF20A0D12);

        // Subtle editor grid.
        for (int x = 0; x < width; x += GRID) context.fill(x, HEADER, x + 1, height - FOOTER, 0x181E2833);
        for (int y = HEADER; y < height - FOOTER; y += GRID) context.fill(0, y, width, y + 1, 0x181E2833);

        context.fill(0, 0, width, HEADER, 0xFF10151D);
        context.fill(0, HEADER - 1, width, HEADER, 0xFF34404F);
        context.drawTextWithShadow(textRenderer, Text.literal("COSMIC TWEAKS"), 16, 12, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("HUD EDITOR"), 16, 27, 0xFF9AA7B8);
        context.drawTextWithShadow(textRenderer,
                Text.literal(snapToGrid ? "GRID 8  •  SNAP ON" : "GRID 8  •  SNAP OFF"),
                width - 150, 20, 0xFF9AA7B8);

        for (Entry e : entries()) {
            drawModule(context, e, mouseX, mouseY);
        }

        // Right-side inspector.
        int panelX = Math.max(0, width - 198);
        context.fill(panelX, HEADER, width, height - FOOTER, 0xE8121720);
        context.fill(panelX, HEADER, panelX + 2, height - FOOTER, 0xFF3B4655);
        context.drawTextWithShadow(textRenderer, Text.literal("MODULE"), panelX + 14, HEADER + 14, 0xFF9AA7B8);

        if (selected != null) {
            Entry e = find(selected);
            ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
            context.drawTextWithShadow(textRenderer, Text.literal(e.label()), panelX + 14, HEADER + 32, 0xFFFFFFFF);
            context.drawTextWithShadow(textRenderer, Text.literal(String.format("X  %.0f", p.x)), panelX + 14, HEADER + 52, 0xFFB7C1CE);
            context.drawTextWithShadow(textRenderer, Text.literal(String.format("Y  %.0f", p.y)), panelX + 14, HEADER + 67, 0xFFB7C1CE);
            context.drawTextWithShadow(textRenderer, Text.literal(String.format("SCALE  %.2fx", p.scale)), panelX + 14, HEADER + 82, 0xFFB7C1CE);
            context.drawTextWithShadow(textRenderer, Text.literal("Right-click: hide/show"), panelX + 14, HEADER + 104, 0xFF778395);
            context.drawTextWithShadow(textRenderer, Text.literal("Wheel: scale"), panelX + 14, HEADER + 119, 0xFF778395);
        } else {
            context.drawTextWithShadow(textRenderer, Text.literal("Select a module"), panelX + 14, HEADER + 34, 0xFFB7C1CE);
            context.drawTextWithShadow(textRenderer, Text.literal("Drag to move"), panelX + 14, HEADER + 52, 0xFF778395);
        }

        context.fill(0, height - FOOTER, width, height, 0xFF10151D);
        context.drawTextWithShadow(textRenderer, Text.literal("LEFT-DRAG  MOVE"), 16, height - 22, 0xFFB7C1CE);
        context.drawTextWithShadow(textRenderer, Text.literal("RIGHT-CLICK  VISIBILITY"), 138, height - 22, 0xFFB7C1CE);
        context.drawTextWithShadow(textRenderer, Text.literal("ESC  SAVE & EXIT"), width - 122, height - 22, 0xFFFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawModule(DrawContext context, Entry e, int mouseX, int mouseY) {
        ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
        double scale = Math.max(0.5, Math.min(2.0, p.scale));
        int x = (int) Math.round(p.x);
        int y = (int) Math.round(p.y);
        int w = (int) Math.round(e.width() * scale);
        int h = (int) Math.round(e.height() * scale);
        boolean hovered = inside(mouseX, mouseY, x, y, w, h);
        boolean active = e.id().equals(selected);
        boolean enabled = isEnabled(e.id());

        int fill = !enabled ? 0x70141A22 : (active ? 0xFF263746 : (hovered ? 0xFF202C39 : 0xCC141A22));
        context.fill(x, y, x + w, y + h, fill);
        context.fill(x, y, x + w, y + 2, active ? 0xFFB9C7D9 : (enabled ? 0xFF3B4655 : 0xFF59616B));
        context.drawTextWithShadow(textRenderer, Text.literal(enabled ? e.preview() : e.label() + "  [HIDDEN]"),
                x + 8, y + 7, enabled ? 0xFFFFFFFF : 0xFF778395);
    }

    private boolean isEnabled(String id) {
        return switch (id) {
            case "FPS" -> ModuleManager.fps();
            case "PING" -> ModuleManager.ping();
            case "COORDINATES" -> ModuleManager.coordinates();
            case "KEYSTROKES" -> ModuleManager.keystrokes();
            case "CPS" -> ModuleManager.cps();
            case "EQUIPMENT" -> ModuleManager.armor() || ModuleManager.durability();
            case "FACING" -> CosmicTweaksClient.CONFIG.showCompass;
            case "CLOCK" -> CosmicTweaksClient.CONFIG.showClock;
            default -> true;
        };
    }

    private Entry find(String id) {
        return entries().stream().filter(e -> e.id().equals(id)).findFirst().orElse(entries().get(0));
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        Entry hit = hit(click.x(), click.y());
        if (hit == null) return super.mouseClicked(click, doubled);

        selected = hit.id();
        ModulePosition p = HudLayout.get(hit.id(), hit.defaultX(), hit.defaultY());

        if (click.button() == 0) {
            dragging = hit.id();
            dragOffsetX = click.x() - p.x;
            dragOffsetY = click.y() - p.y;
            return true;
        }

        if (click.button() == 1) {
            p.visible = !p.visible;
            HudLayout.save();
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double offsetX, double offsetY) {
        if (dragging != null) {
            Entry e = find(dragging);
            ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
            double x = click.x() - dragOffsetX;
            double y = click.y() - dragOffsetY;
            if (snapToGrid) {
                x = Math.round(x / GRID) * GRID;
                y = Math.round(y / GRID) * GRID;
            }
            p.x = Math.max(4, Math.min(width - e.width() - 4, x));
            p.y = Math.max(HEADER + 4, Math.min(height - FOOTER - e.height(), y));
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
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        Entry e = hit(mouseX, mouseY);
        if (e != null) {
            selected = e.id();
            ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
            p.scale = Math.max(0.5, Math.min(2.0, p.scale + (verticalAmount > 0 ? 0.1 : -0.1)));
            HudLayout.save();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (input.key() == 256) {
            HudLayout.save();
            if (client != null) client.setScreen(parent);
            return true;
        }
        if (input.key() == 71) { // G
            snapToGrid = !snapToGrid;
            return true;
        }
        return super.keyPressed(input);
    }

    private Entry hit(double mouseX, double mouseY) {
        List<Entry> all = new ArrayList<>(entries());
        for (int i = all.size() - 1; i >= 0; i--) {
            Entry e = all.get(i);
            ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
            double s = Math.max(0.5, Math.min(2.0, p.scale));
            if (inside(mouseX, mouseY, p.x, p.y, e.width() * s, e.height() * s)) return e;
        }
        return null;
    }

    private static boolean inside(double mouseX, double mouseY, double x, double y, double w, double h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }
}
