package com.cosmictweaks.screen;

import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.hud.HudLayout;
import com.cosmictweaks.hud.HudLayout.ModulePosition;
import com.cosmictweaks.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.List;

public final class HudEditorScreen extends Screen {
    private final Screen parent;
    private String dragging;
    private String selected;
    private double dragOffsetX, dragOffsetY;
    private boolean snap = true;

    private static final int SIDE = 250;
    private static final int TOP = 62;
    private static final int BOTTOM = 52;
    private static final int GRID = 8;

    private record Entry(String id, String label, int w, int h, int defaultX, int defaultY) {}

    public HudEditorScreen(Screen parent) {
        super(Text.literal("Cosmic Tweaks HUD Editor"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        c.fill(0, 0, width, height, 0xFF080B10);

        int canvasRight = width - SIDE;
        c.fill(0, TOP, canvasRight, height - BOTTOM, 0xFF0B1017);

        for (int x = 0; x <= canvasRight; x += GRID)
            c.fill(x, TOP, x + 1, height - BOTTOM, 0x142B3442);
        for (int y = TOP; y <= height - BOTTOM; y += GRID)
            c.fill(0, y, canvasRight, y + 1, 0x142B3442);

        // Screen-center crosshair makes the coordinate system obvious.
        int cx = canvasRight / 2, cy = (TOP + height - BOTTOM) / 2;
        c.fill(cx, TOP, cx + 1, height - BOTTOM, 0x284B596B);
        c.fill(0, cy, canvasRight, cy + 1, 0x284B596B);

        c.fill(0, 0, width, TOP, 0xFF0D121A);
        c.fill(0, TOP - 1, width, TOP, 0xFF364252);
        c.drawTextWithShadow(textRenderer, Text.literal("COSMIC TWEAKS"), 18, 12, 0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer, Text.literal("HUD EDITOR  •  LIVE LAYOUT"), 18, 31, 0xFF8A96A7);
        c.drawTextWithShadow(textRenderer, Text.literal(snap ? "SNAP 8PX" : "FREE MOVE"), canvasRight - 94, 23, 0xFFB9C4D1);

        for (Entry e : entries()) drawModule(c, e, mx, my);
        drawSidebar(c, mx, my, canvasRight);

        c.fill(0, height - BOTTOM, width, height, 0xFF0D121A);
        c.drawTextWithShadow(textRenderer, Text.literal("LMB  MOVE"), 16, height - 32, 0xFF8A96A7);
        c.drawTextWithShadow(textRenderer, Text.literal("RMB  VISIBILITY"), 105, height - 32, 0xFF8A96A7);
        c.drawTextWithShadow(textRenderer, Text.literal("WHEEL  SCALE"), 230, height - 32, 0xFF8A96A7);
        c.drawTextWithShadow(textRenderer, Text.literal("G  SNAP"), 360, height - 32, 0xFF8A96A7);
        c.drawTextWithShadow(textRenderer, Text.literal("ESC  SAVE"), width - 82, height - 32, 0xFFFFFFFF);
    }

    private void drawSidebar(DrawContext c, int mx, int my, int canvasRight) {
        int x = canvasRight;
        c.fill(x, TOP, width, height - BOTTOM, 0xFF101620);
        c.fill(x, TOP, x + 2, height - BOTTOM, 0xFF313C4B);

        c.drawTextWithShadow(textRenderer, Text.literal("HUD MODULES"), x + 18, TOP + 16, 0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer, Text.literal("Live positions • right click to hide"), x + 18, TOP + 34, 0xFF707D90);

        int y = TOP + 52;
        for (Entry e : entries()) {
            boolean active = e.id().equals(selected);
            boolean visible = isVisible(e.id());
            int rowH = 34;
            c.fill(x + 12, y, width - 12, y + rowH, active ? 0xFF293747 : 0xFF171E28);
            if (active) c.fill(x + 12, y, x + 15, y + rowH, 0xFFFFFFFF);
            c.drawTextWithShadow(textRenderer, Text.literal(e.label()), x + 24, y + 7,
                    visible ? 0xFFFFFFFF : 0xFF697689);
            c.drawTextWithShadow(textRenderer, Text.literal(visible ? "VISIBLE" : "HIDDEN"),
                    width - 68, y + 7, visible ? 0xFFB8C4D1 : 0xFF697689);
            y += 40;
        }

        y += 5;
        c.drawTextWithShadow(textRenderer, Text.literal("INSPECTOR"), x + 18, y, 0xFF8794A6);
        if (selected == null) {
            c.drawTextWithShadow(textRenderer, Text.literal("Select a module"), x + 18, y + 24, 0xFF687587);
            c.drawTextWithShadow(textRenderer, Text.literal("Drag it directly on the canvas"), x + 18, y + 40, 0xFF687587);
            return;
        }

        Entry e = find(selected);
        ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
        c.drawTextWithShadow(textRenderer, Text.literal(e.label()), x + 18, y + 23, 0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer, Text.literal(String.format("X  %.0f     Y  %.0f", p.x, p.y)),
                x + 18, y + 42, 0xFFB1BCC9);
        c.drawTextWithShadow(textRenderer, Text.literal(String.format("SCALE  %.1fx", p.scale)),
                x + 18, y + 59, 0xFFB1BCC9);
        c.drawTextWithShadow(textRenderer, Text.literal("Right click canvas or list = hide/show"),
                x + 18, y + 79, 0xFF687587);
        c.drawTextWithShadow(textRenderer, Text.literal("Wheel over module = resize"),
                x + 18, y + 95, 0xFF687587);
    }

    private void drawModule(DrawContext c, Entry e, int mx, int my) {
        ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
        double s = clampScale(p.scale);
        int x = (int) Math.round(p.x);
        int y = (int) Math.round(p.y);
        int w = (int) Math.round(e.w() * s);
        int h = (int) Math.round(e.h() * s);

        boolean hover = inside(mx, my, x, y, w, h);
        boolean active = e.id().equals(selected);
        boolean visible = isVisible(e.id());

        if (active) c.fill(x - 3, y - 3, x + w + 3, y + h + 3, 0xFFFFFFFF);
        c.fill(x, y, x + w, y + h, !visible ? 0xFF151A21 : hover ? 0xFF263544 : 0xE8171F29);

        // Preview is based on the real HUD module dimensions/content, not arbitrary editor labels.
        c.fill(x, y, x + w, y + 2, active ? 0xFFFFFFFF : 0xFF596777);
        int textX = x + Math.max(6, (int) Math.round(7 * s));
        int textY = y + Math.max(5, (int) Math.round(5 * s));

        switch (e.id()) {
            case "FPS" -> text(c, "FPS  144", textX, textY, visible);
            case "PING" -> text(c, "PING  42ms", textX, textY, visible);
            case "COORDINATES" -> text(c, "XYZ  120  64  -32", textX, textY, visible);
            case "FACING" -> text(c, "FACING  NORTH", textX, textY, visible);
            case "CLOCK" -> text(c, "WORLD  12000", textX, textY, visible);
            case "CPS" -> {
                text(c, "LMB 0 CPS", textX, textY, visible);
                text(c, "RMB 0 CPS", textX, textY + Math.max(11, (int) Math.round(12 * s)), visible);
            }
            case "KEYSTROKES" -> drawKeys(c, x, y, s, visible);
            case "EQUIPMENT" -> drawEquipment(c, x, y, s, visible);
        }

        if (!visible) c.drawTextWithShadow(textRenderer, Text.literal("HIDDEN"), x + 7, y + h / 2 - 4, 0xFF7A8798);
    }

    private void drawKeys(DrawContext c, int x, int y, double s, boolean visible) {
        int size = Math.max(16, (int) Math.round(22 * s));
        drawKey(c, x + size, y + 4, size, "W", visible);
        drawKey(c, x, y + size + 4, size, "A", visible);
        drawKey(c, x + size, y + size + 4, size, "S", visible);
        drawKey(c, x + size * 2, y + size + 4, size, "D", visible);
        drawKey(c, x + size * 3 + 5, y + size + 4, Math.max(size, (int)(40*s)), "SPACE", visible);
    }

    private void drawKey(DrawContext c, int x, int y, int w, String label, boolean visible) {
        c.fill(x, y, x + w, y + 20, visible ? 0xFF3B4655 : 0xFF202630);
        c.drawCenteredTextWithShadow(textRenderer, Text.literal(label), x + w / 2, y + 6,
                visible ? 0xFFFFFFFF : 0xFF687587);
    }

    private void drawEquipment(DrawContext c, int x, int y, double s, boolean visible) {
        int size = Math.max(16, (int) Math.round(18 * s));
        for (int i = 0; i < 4; i++) {
            c.fill(x + 2, y + 5 + i * size, x + size, y + 5 + i * size + size - 2,
                    visible ? 0xFF252D38 : 0xFF1B2028);
        }
        if (visible) c.drawTextWithShadow(textRenderer, Text.literal("ARMOR / DURABILITY"),
                x + size + 5, y + 12, 0xFFB7C1CE);
    }

    private void text(DrawContext c, String s, int x, int y, boolean visible) {
        c.drawTextWithShadow(textRenderer, Text.literal(s), x, y, visible ? 0xFFFFFFFF : 0xFF687587);
    }

    private List<Entry> entries() {
        return List.of(
                new Entry("FPS", "FPS", 80, 24, 8, 8),
                new Entry("PING", "PING", 88, 24, 8, 20),
                new Entry("COORDINATES", "COORDINATES", 150, 24, 8, 32),
                new Entry("FACING", "FACING", 105, 24, 8, 44),
                new Entry("CLOCK", "WORLD CLOCK", 110, 24, 8, 56),
                new Entry("KEYSTROKES", "KEYSTROKES", 136, 68, 8, 120),
                new Entry("CPS", "CPS", 120, 32, 8, 196),
                new Entry("EQUIPMENT", "EQUIPMENT", 92, 80, 8, 240)
        );
    }

    private boolean isVisible(String id) {
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

    private Entry hit(double mx, double my) {
        List<Entry> es = entries();
        for (int i = es.size() - 1; i >= 0; i--) {
            Entry e = es.get(i);
            ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
            double s = clampScale(p.scale);
            if (inside(mx, my, p.x, p.y, e.w() * s, e.h() * s)) return e;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        Entry e = hit(click.x(), click.y());
        if (e == null) return super.mouseClicked(click, doubled);

        selected = e.id();
        ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());

        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = e.id();
            dragOffsetX = click.x() - p.x;
            dragOffsetY = click.y() - p.y;
            return true;
        }
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            p.visible = !p.visible;
            HudLayout.save();
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click click, double dx, double dy) {
        if (dragging == null) return super.mouseDragged(click, dx, dy);
        Entry e = find(dragging);
        ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
        double s = clampScale(p.scale);
        double x = click.x() - dragOffsetX;
        double y = click.y() - dragOffsetY;

        if (snap) {
            x = Math.round(x / GRID) * GRID;
            y = Math.round(y / GRID) * GRID;
        }

        int canvasRight = width - SIDE;
        p.x = Math.max(2, Math.min(canvasRight - e.w() * s - 2, x));
        p.y = Math.max(TOP + 2, Math.min(height - BOTTOM - e.h() * s - 2, y));
        return true;
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click click) {
        if (click.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && dragging != null) {
            dragging = null;
            HudLayout.save();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        Entry e = hit(mx, my);
        if (e != null) {
            selected = e.id();
            ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
            p.scale = Math.max(0.5, Math.min(2.0, p.scale + (vertical > 0 ? 0.1 : -0.1)));
            HudLayout.save();
            return true;
        }
        return super.mouseScrolled(mx, my, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            HudLayout.save();
            if (client != null) client.setScreen(parent);
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_G) {
            snap = !snap;
            return true;
        }
        if (input.key() == GLFW.GLFW_KEY_R && selected != null) {
            Entry e = find(selected);
            ModulePosition p = HudLayout.get(e.id(), e.defaultX(), e.defaultY());
            p.x = e.defaultX();
            p.y = e.defaultY();
            p.scale = 1.0;
            p.visible = true;
            HudLayout.save();
            return true;
        }
        return super.keyPressed(input);
    }

    private static double clampScale(double scale) {
        return Math.max(0.5, Math.min(2.0, scale));
    }

    private static boolean inside(double mx, double my, double x, double y, double w, double h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
