package com.cosmictweaks.hud;

import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.module.ModuleManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class HudRenderer implements HudRenderCallback {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int MUTED = 0xFFB7C1CE;
    private static final int PANEL = 0xB8141A22;
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>();
    private static boolean leftWasDown;
    private static boolean rightWasDown;

    public static void register() {
        HudRenderCallback.EVENT.register(new HudRenderer());
    }

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;

        updateClicks(client);

        if (ModuleManager.fps()) {
            drawScaledText(context, "FPS  " + client.getCurrentFps(), "FPS", 8, 8, WHITE);
        }

        if (ModuleManager.ping()) {
            int ping = 0;
            if (client.getNetworkHandler() != null) {
                var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                if (entry != null) ping = entry.getLatency();
            }
            drawScaledText(context, "PING  " + ping + "ms", "PING", 8, 20, WHITE);
        }

        if (ModuleManager.coordinates()) {
            BlockPos pos = client.player.getBlockPos();
            drawScaledText(context, "XYZ  " + pos.getX() + "  " + pos.getY() + "  " + pos.getZ(),
                    "COORDINATES", 8, 32, WHITE);
        }

        if (CosmicTweaksClient.CONFIG.showCompass) {
            drawScaledText(context, "FACING  " + getFacing(client), "FACING", 8, 44, MUTED);
        }

        if (CosmicTweaksClient.CONFIG.showClock) {
            long time = client.world.getTimeOfDay() % 24000L;
            drawScaledText(context, "WORLD  " + String.format("%05d", time), "CLOCK", 8, 56, MUTED);
        }

        if (ModuleManager.keystrokes()) renderKeystrokes(context, client);
        if (ModuleManager.cps()) renderCps(context, client);
        if (ModuleManager.armor() || ModuleManager.durability()) renderEquipment(context, client);
    }

    private HudLayout.ModulePosition position(String id, double defaultX, double defaultY) {
        HudLayout.ModulePosition p = HudLayout.get(id, defaultX, defaultY);
        return p.visible ? p : new HudLayout.ModulePosition(-1000, -1000);
    }

    private void drawScaledText(DrawContext context, String text, String id, double defaultX, double defaultY, int color) {
        HudLayout.ModulePosition p = position(id, defaultX, defaultY);
        if (p.x < -900) return;
        float s = (float)Math.max(0.5, Math.min(2.0, p.scale));
        if (s == 1.0f) {
            drawText(context, text, (int)p.x, (int)p.y, color);
            return;
        }
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(p.x, p.y, 0);
        matrices.scale(s, s, 1.0f);
        drawText(context, text, 0, 0, color);
        matrices.pop();
    }

    private void updateClicks(MinecraftClient client) {
        long now = System.currentTimeMillis();
        long handle = client.getWindow().getHandle();
        boolean left = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (left && !leftWasDown) LEFT_CLICKS.addLast(now);
        if (right && !rightWasDown) RIGHT_CLICKS.addLast(now);
        leftWasDown = left;
        rightWasDown = right;
        prune(LEFT_CLICKS, now);
        prune(RIGHT_CLICKS, now);
    }

    private void prune(Deque<Long> clicks, long now) {
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000L) clicks.removeFirst();
    }

    private void renderCps(DrawContext context, MinecraftClient client) {
        HudLayout.ModulePosition p = position("CPS", 8, thisHeight(client) - 102);
        if (p.x < -900) return;
        drawScaledText(context, "LMB " + LEFT_CLICKS.size() + " CPS", "CPS", 8, thisHeight(client) - 102, WHITE);
        HudLayout.ModulePosition current = HudLayout.get("CPS", 8, thisHeight(client) - 102);
        drawScaledText(context, "RMB " + RIGHT_CLICKS.size() + " CPS", "CPS_SECOND", current.x, current.y + 12 * current.scale, MUTED);
    }

    private void renderKeystrokes(DrawContext context, MinecraftClient client) {
        HudLayout.ModulePosition p = position("KEYSTROKES", 8, thisHeight(client) - 78);
        if (p.x < -900) return;
        int x = (int)p.x;
        int y = (int)p.y;
        float s = (float)Math.max(0.5, Math.min(2.0, p.scale));
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(s, s, 1.0f);
        int size = 22;
        drawKey(context, client, size, 0, "W", GLFW.GLFW_KEY_W);
        drawKey(context, client, 0, size, "A", GLFW.GLFW_KEY_A);
        drawKey(context, client, size, size, "S", GLFW.GLFW_KEY_S);
        drawKey(context, client, size * 2, size, "D", GLFW.GLFW_KEY_D);
        drawKey(context, client, size * 3 + 4, size, "SPACE", GLFW.GLFW_KEY_SPACE);
        drawKey(context, client, size * 3 + 4, 0, "LMB", GLFW.GLFW_MOUSE_BUTTON_LEFT);
        drawKey(context, client, size * 3 + 30, 0, "RMB", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
        matrices.pop();
    }

    private void drawKey(DrawContext context, MinecraftClient client, int x, int y, String label, int input) {
        long handle = client.getWindow().getHandle();
        boolean pressed = input >= GLFW.GLFW_MOUSE_BUTTON_1
                ? GLFW.glfwGetMouseButton(handle, input) == GLFW.GLFW_PRESS
                : GLFW.glfwGetKey(handle, input) == GLFW.GLFW_PRESS;
        int width = label.length() > 2 ? 40 : 22;
        context.fill(x, y, x + width, y + 20, pressed ? 0xFF3B4655 : PANEL);
        context.drawCenteredTextWithShadow(client.textRenderer, net.minecraft.text.Text.literal(label),
                x + width / 2, y + 6, WHITE);
    }

    private void renderEquipment(DrawContext context, MinecraftClient client) {
        HudLayout.ModulePosition p = position("EQUIPMENT", thisWidth(client) - 92, thisHeight(client) - 80);
        if (p.x < -900) return;
        float s = (float)Math.max(0.5, Math.min(2.0, p.scale));
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(p.x, p.y, 0);
        matrices.scale(s, s, 1.0f);
        int x = 0, y = 0;
        EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : armorSlots) {
            ItemStack stack = client.player.getEquippedStack(slot);
            if (ModuleManager.armor()) context.drawItem(stack, x, y);
            if (ModuleManager.durability() && !stack.isEmpty() && stack.isDamageable()) {
                int remaining = stack.getMaxDamage() - stack.getDamage();
                drawText(context, String.valueOf(remaining), x + 18, y + 16,
                        remaining < 20 ? 0xFFFFB4B4 : MUTED);
            }
            y += 20;
        }
        matrices.pop();
    }

    private int thisWidth(MinecraftClient client) { return client.getWindow().getScaledWidth(); }
    private int thisHeight(MinecraftClient client) { return client.getWindow().getScaledHeight(); }

    private void drawText(DrawContext context, String text, int x, int y, int color) {
        context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, true);
    }

    private String getFacing(MinecraftClient client) {
        float yaw = client.player.getYaw();
        if (yaw >= 135 || yaw < -135) return "S";
        if (yaw >= -135 && yaw < -45) return "W";
        if (yaw >= -45 && yaw < 45) return "N";
        return "E";
    }
}
