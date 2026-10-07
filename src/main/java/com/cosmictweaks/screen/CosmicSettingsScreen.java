package com.cosmictweaks.screen;

import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.config.CosmicConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public final class CosmicSettingsScreen extends Screen {
    private final Screen parent;
    private int category = 0;

    private static final int SIDEBAR = 118;
    private static final int PANEL_W = 520;
    private static final int PANEL_H = 390;
    private static final int BUTTON_W = 360;
    private static final int BUTTON_H = 30;
    private static final int GAP = 7;

    private record Toggle(String label, boolean value, Runnable action) {}

    public CosmicSettingsScreen(Screen parent) {
        super(Text.literal("Cosmic Tweaks"));
        this.parent = parent;
    }

    @Override
    protected void init() {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xF20A0D12);

        int left = (width - PANEL_W) / 2;
        int top = Math.max(8, (height - PANEL_H) / 2);
        int right = left + PANEL_W;

        context.fill(left, top, right, top + PANEL_H, 0xF0121720);
        context.fill(left, top, right, top + 2, 0xFFB9C7D9);
        context.fill(left, top, left + SIDEBAR, top + PANEL_H, 0xFF0D1219);
        context.fill(left + SIDEBAR, top + 44, left + SIDEBAR + 1, top + PANEL_H, 0xFF2B3542);

        context.drawTextWithShadow(textRenderer, Text.literal("COSMIC"), left + 14, top + 14, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("TWEAKS"), left + 14, top + 28, 0xFF9AA7B8);

        String[] cats = {"HUD", "CLIENT", "PERFORMANCE"};
        for (int i = 0; i < cats.length; i++) {
            int y = top + 58 + i * 36;
            boolean active = category == i;
            context.fill(left + 8, y - 5, left + SIDEBAR - 8, y + 25, active ? 0xFF263746 : 0x00000000);
            context.drawTextWithShadow(textRenderer, Text.literal(cats[i]), left + 18, y + 5,
                    active ? 0xFFFFFFFF : 0xFF778395);
        }

        context.drawTextWithShadow(textRenderer, Text.literal(cats[category]), left + SIDEBAR + 22, top + 16, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Cosmic Tweaks  •  1.0.0"),
                left + SIDEBAR + 22, top + 31, 0xFF778395);

        int y = top + 58;
        for (Toggle t : toggles()) {
            drawToggle(context, mouseX, mouseY, left + SIDEBAR + 22, y, t.label(), t.value());
            y += BUTTON_H + GAP;
        }

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("ESC  BACK"),
                left + SIDEBAR + (PANEL_W - SIDEBAR) / 2, top + PANEL_H - 22, 0xFF778395);

        super.render(context, mouseX, mouseY, delta);
    }

    private List<Toggle> toggles() {
        CosmicConfig c = CosmicTweaksClient.CONFIG;
        return switch (category) {
            case 0 -> List.of(
                    new Toggle("FPS HUD", c.showFps, () -> c.showFps = !c.showFps),
                    new Toggle("PING HUD", c.showPing, () -> c.showPing = !c.showPing),
                    new Toggle("COORDINATES", c.showCoordinates, () -> c.showCoordinates = !c.showCoordinates),
                    new Toggle("KEYSTROKES", c.showKeystrokes, () -> c.showKeystrokes = !c.showKeystrokes),
                    new Toggle("CPS", c.showCps, () -> c.showCps = !c.showCps),
                    new Toggle("ARMOR", c.showArmor, () -> c.showArmor = !c.showArmor),
                    new Toggle("DURABILITY", c.showDurability, () -> c.showDurability = !c.showDurability),
                    new Toggle("COMPASS", c.showCompass, () -> c.showCompass = !c.showCompass),
                    new Toggle("WORLD CLOCK", c.showClock, () -> c.showClock = !c.showClock)
            );
            case 1 -> List.of(
                    new Toggle("FULLBRIGHT", c.fullbright, () -> {
                        c.fullbright = !c.fullbright;
                        if (client != null) client.options.getGamma().setValue(c.fullbright ? 10.0 : 1.0);
                    }),
                    new Toggle("ZOOM", c.showZoom, () -> c.showZoom = !c.showZoom),
                    new Toggle("SPRINT TOGGLE", c.sprintToggle, () -> c.sprintToggle = !c.sprintToggle),
                    new Toggle("SNEAK TOGGLE", c.sneakToggle, () -> c.sneakToggle = !c.sneakToggle),
                    new Toggle("FREELOOK", c.freelook, () -> c.freelook = !c.freelook),
                    new Toggle("WAYPOINTS", c.showWaypoints, () -> c.showWaypoints = !c.showWaypoints)
            );
            default -> List.of(
                    new Toggle("PERFORMANCE PROFILE", c.performanceProfile, () -> c.performanceProfile = !c.performanceProfile),
                    new Toggle("REDUCE PARTICLES", c.reduceParticles, () -> c.reduceParticles = !c.reduceParticles),
                    new Toggle("REDUCE ENTITY DISTANCE", c.reduceEntityRenderDistance, () -> c.reduceEntityRenderDistance = !c.reduceEntityRenderDistance),
                    new Toggle("HIDE COSMETICS", c.hideCosmetics, () -> c.hideCosmetics = !c.hideCosmetics)
            );
        };
    }

    private void drawToggle(DrawContext context, int mouseX, int mouseY, int x, int y, String label, boolean enabled) {
        boolean hovered = mouseX >= x && mouseX < x + BUTTON_W && mouseY >= y && mouseY < y + BUTTON_H;
        int fill = hovered ? 0xFF26313F : 0xFF1A222D;
        int border = hovered ? 0xFFB9C7D9 : 0xFF3B4655;
        context.fill(x, y, x + BUTTON_W, y + BUTTON_H, fill);
        context.fill(x, y, x + BUTTON_W, y + 1, border);
        context.fill(x, y + BUTTON_H - 1, x + BUTTON_W, y + BUTTON_H, border);
        context.fill(x, y, x + 1, y + BUTTON_H, border);
        context.fill(x + BUTTON_W - 1, y, x + BUTTON_W, y + BUTTON_H, border);
        context.drawTextWithShadow(textRenderer, Text.literal(label), x + 12, y + 10, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal(enabled ? "ON" : "OFF"),
                x + BUTTON_W - 34, y + 10, enabled ? 0xFFFFFFFF : 0xFF778395);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
        if (click.button() != 0) return super.mouseClicked(click, doubled);

        int left = (width - PANEL_W) / 2;
        int top = Math.max(8, (height - PANEL_H) / 2);

        for (int i = 0; i < 3; i++) {
            int y = top + 53 + i * 36;
            if (inside(click.x(), click.y(), left + 8, y, SIDEBAR - 16, 30)) {
                category = i;
                return true;
            }
        }

        int y = top + 58;
        for (Toggle t : toggles()) {
            if (inside(click.x(), click.y(), left + SIDEBAR + 22, y, BUTTON_W, BUTTON_H)) {
                t.action().run();
                CosmicConfig.save();
                return true;
            }
            y += BUTTON_H + GAP;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (input.key() == 256) {
            closeScreen();
            return true;
        }
        return super.keyPressed(input);
    }

    private void closeScreen() {
        CosmicConfig.save();
        if (client != null) client.setScreen(parent);
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }
}
