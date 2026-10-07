package com.cosmictweaks.hud;

import com.cosmictweaks.CosmicTweaksClient;
import com.cosmictweaks.module.ModuleManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
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
        int x = 8;
        int y = 8;

        if (ModuleManager.fps()) {
            drawText(context, "FPS  " + client.getCurrentFps(), x, y, WHITE);
            y += 12;
        }
        if (ModuleManager.ping()) {
            int ping = 0;
            if (client.getNetworkHandler() != null) {
                var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                if (entry != null) ping = entry.getLatency();
            }
            drawText(context, "PING  " + ping + "ms", x, y, WHITE);
            y += 12;
        }
        if (ModuleManager.coordinates()) {
            BlockPos pos = client.player.getBlockPos();
            drawText(context, "XYZ  " + pos.getX() + "  " + pos.getY() + "  " + pos.getZ(), x, y, WHITE);
            y += 12;
        }
        if (CosmicTweaksClient.CONFIG.showCompass) {
            drawText(context, "FACING  " + getFacing(client), x, y, MUTED);
            y += 12;
        }
        if (CosmicTweaksClient.CONFIG.showClock) {
            long time = client.world.getTimeOfDay() % 24000L;
            drawText(context, "WORLD  " + String.format("%05d", time), x, y, MUTED);
        }

        if (ModuleManager.keystrokes()) {
            renderKeystrokes(context, client);
        }
        if (ModuleManager.cps()) {
            renderCps(context, client);
        }
        if (ModuleManager.armor() || ModuleManager.durability()) {
            renderEquipment(context, client);
        }

        CosmicHudBranding.render(context, client);
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
        int x = 8;
        int y = thisHeight(client) - 102;
        drawText(context, "LMB " + LEFT_CLICKS.size() + " CPS", x, y, WHITE);
        drawText(context, "RMB " + RIGHT_CLICKS.size() + " CPS", x, y + 12, MUTED);
    }

    private void renderKeystrokes(DrawContext context, MinecraftClient client) {
        int x = 8;
        int y = thisHeight(client) - 78;
        int size = 22;
        drawKey(context, client, x + size, y, "W", GLFW.GLFW_KEY_W);
        drawKey(context, client, x, y + size, "A", GLFW.GLFW_KEY_A);
        drawKey(context, client, x + size, y + size, "S", GLFW.GLFW_KEY_S);
        drawKey(context, client, x + size * 2, y + size, "D", GLFW.GLFW_KEY_D);
        drawKey(context, client, x + size * 3 + 4, y + size, "SPACE", GLFW.GLFW_KEY_SPACE);
        drawKey(context, client, x + size * 3 + 4, y, "LMB", GLFW.GLFW_MOUSE_BUTTON_LEFT);
        drawKey(context, client, x + size * 3 + 30, y, "RMB", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
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
        int x = thisWidth(client) - 92;
        int y = thisHeight(client) - 80;
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = client.player.getInventory().getArmorStack(i);
            if (ModuleManager.armor()) context.drawItem(stack, x, y);
            if (ModuleManager.durability() && !stack.isEmpty() && stack.isDamageable()) {
                int remaining = stack.getMaxDamage() - stack.getDamage();
                drawText(context, String.valueOf(remaining), x + 18, y + 16,
                        remaining < 20 ? 0xFFFFB4B4 : MUTED);
            }
            y += 20;
        }
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
