package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.item.Kv2HowitzerItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

/**
 * WoT-style aiming circle: shrinks while charging the KV-2 howitzer.
 * Center matches vanilla crosshair ((W-1)/2, (H-1)/2).
 */
public final class HowitzerAimHud {
    private HowitzerAimHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || mc.screen != null) {
            return;
        }
        if (!(player.getUseItem().getItem() instanceof Kv2HowitzerItem)
                && !(player.getMainHandItem().getItem() instanceof Kv2HowitzerItem)) {
            return;
        }

        float charge = Kv2HowitzerItem.chargeProgress(player);
        boolean charging = player.isUsingItem() && player.getUseItem().getItem() instanceof Kv2HowitzerItem;
        if (!charging && !(player.getMainHandItem().getItem() instanceof Kv2HowitzerItem)) {
            return;
        }

        // Match vanilla crosshair center: sprite is 15x15 at (W-15)/2 → center (W-1)/2
        int cx = (graphics.guiWidth() - 1) / 2;
        int cy = (graphics.guiHeight() - 1) / 2;
        float accuracy = charging ? charge : 0f;
        int radius = Math.round(Mth.lerp(accuracy, 48f, 10f));
        int color = accuracy > 0.85f ? 0xFF6CFF8A : (accuracy > 0.4f ? 0xFFFFE082 : 0xFFFF6B6B);

        drawCircle(graphics, cx, cy, radius, color);
        drawCircle(graphics, cx, cy, Math.max(2, radius / 4), 0x88FFFFFF);

        // Symmetric crosshair ticks around exact center
        int tick = 6;
        graphics.fill(cx - radius - tick, cy, cx - radius, cy + 1, color);
        graphics.fill(cx + radius + 1, cy, cx + radius + 1 + tick, cy + 1, color);
        graphics.fill(cx, cy - radius - tick, cx + 1, cy - radius, color);
        graphics.fill(cx, cy + radius + 1, cx + 1, cy + radius + 1 + tick, color);
        // Center pip
        graphics.fill(cx, cy, cx + 1, cy + 1, 0xFFFFFFFF);

        if (charging) {
            String pct = (int) (accuracy * 100) + "%";
            graphics.drawCenteredString(mc.font, pct, cx + 1, cy + radius + 8, color);
        }
    }

    private static void drawCircle(GuiGraphics graphics, int cx, int cy, int radius, int color) {
        int steps = Math.max(48, radius * 2);
        for (int i = 0; i < steps; i++) {
            double a = (Math.PI * 2 * i) / steps;
            int x = cx + (int) Math.round(Math.cos(a) * radius);
            int y = cy + (int) Math.round(Math.sin(a) * radius);
            graphics.fill(x, y, x + 1, y + 1, color);
        }
    }
}
