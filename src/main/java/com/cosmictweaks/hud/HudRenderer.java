package com.cosmictweaks.hud;

import com.cosmictweaks.CosmicTweaksClient;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.BlockPos;

public class HudRenderer implements HudRenderCallback {
    private static final int WHITE = 0xFFFFFFFF;

    public static void register() {
        HudRenderCallback.EVENT.register(new HudRenderer());
    }

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        int x = 8;
        int y = 8;

        if (CosmicTweaksClient.CONFIG.showFps) {
            drawText(context, "FPS: " + client.getWindow().getFps(), x, y);
            y += 12;
        }

        if (CosmicTweaksClient.CONFIG.showPing) {
            int ping = 0;
            if (client.getNetworkHandler() != null) {
                var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
                if (entry != null) {
                    ping = entry.getLatency();
                }
            }
            drawText(context, "Ping: " + ping + "ms", x, y);
            y += 12;
        }

        if (CosmicTweaksClient.CONFIG.showCoordinates) {
            BlockPos pos = client.player.getBlockPos();
            drawText(context, "XYZ: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ(), x, y);
            y += 12;
        }

        if (CosmicTweaksClient.CONFIG.showKeystrokes) {
            drawText(context, "Keystrokes: ON", x, y);
            y += 12;
        }

        if (CosmicTweaksClient.CONFIG.showCompass) {
            drawText(context, "Facing: " + getFacing(client), x, y);
            y += 12;
        }

        if (CosmicTweaksClient.CONFIG.showClock) {
            drawText(context, "Time: " + getTimeString(client), x, y);
            y += 12;
        }
    }

    private void drawText(DrawContext context, String text, int x, int y) {
        context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, WHITE, true);
    }

    private String getFacing(MinecraftClient client) {
        float yaw = client.player.getYaw();
        if (yaw >= 135 || yaw < -135) return "S";
        if (yaw >= -135 && yaw < -45) return "W";
        if (yaw >= -45 && yaw < 45) return "N";
        return "E";
    }

    private String getTimeString(MinecraftClient client) {
        long time = client.world != null ? client.world.getTimeOfDay() : 0L;
        return String.valueOf(time % 24000);
    }
}
