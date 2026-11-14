package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.MyLeveling;
import com.l7cor1s.myleveling.item.Kv2HowitzerItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

/**
 * Military howitzer reticle: outer accuracy ring shrinks while charging (WoT-style),
 * with mil ticks and a high-res base reticle texture. Center matches vanilla crosshair.
 */
public final class HowitzerAimHud {
    private static final ResourceLocation RETICLE =
            ResourceLocation.fromNamespaceAndPath(MyLeveling.MOD_ID, "textures/gui/howitzer_reticle.png");

    private HowitzerAimHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || mc.screen != null) {
            return;
        }
        boolean holding = player.getMainHandItem().getItem() instanceof Kv2HowitzerItem
                || player.getOffhandItem().getItem() instanceof Kv2HowitzerItem;
        boolean charging = player.isUsingItem() && player.getUseItem().getItem() instanceof Kv2HowitzerItem;
        if (!holding && !charging) {
            return;
        }

        float charge = Kv2HowitzerItem.chargeProgress(player);
        float accuracy = charging ? charge : 0f;

        // Match vanilla crosshair center: sprite is 15x15 at (W-15)/2 → center (W-1)/2
        int cx = (graphics.guiWidth() - 1) / 2;
        int cy = (graphics.guiHeight() - 1) / 2;

        int color = accuracy > 0.85f ? 0xFF7CFF90 : (accuracy > 0.4f ? 0xFFE8D090 : 0xFFFF6B5A);
        int dim = 0x88C8B878;

        // High-res static military reticle (64→96 screen px)
        int retSize = 96;
        int rx = cx - retSize / 2;
        int ry = cy - retSize / 2;
        graphics.setColor(1f, 1f, 1f, charging ? 0.95f : 0.55f);
        graphics.blit(RETICLE, rx, ry, 0, 0, retSize, retSize, retSize, retSize);
        graphics.setColor(1f, 1f, 1f, 1f);

        // Dynamic accuracy ring (shrinks as charge rises)
        int radius = Math.round(Mth.lerp(accuracy, 56f, 12f));
        drawRing(graphics, cx, cy, radius, color, 2);
        drawRing(graphics, cx, cy, Math.max(3, radius / 3), dim, 1);

        // Cardinal mil ticks on the accuracy ring
        int tick = 8;
        graphics.fill(cx - radius - tick, cy - 1, cx - radius, cy + 2, color);
        graphics.fill(cx + radius + 1, cy - 1, cx + radius + 1 + tick, cy + 2, color);
        graphics.fill(cx - 1, cy - radius - tick, cx + 2, cy - radius, color);
        graphics.fill(cx - 1, cy + radius + 1, cx + 2, cy + radius + 1 + tick, color);

        // Diagonal mil marks
        int d = (int) Math.round(radius * 0.707);
        graphics.fill(cx - d - 2, cy - d - 2, cx - d + 1, cy - d + 1, dim);
        graphics.fill(cx + d - 1, cy - d - 2, cx + d + 2, cy - d + 1, dim);
        graphics.fill(cx - d - 2, cy + d - 1, cx - d + 1, cy + d + 2, dim);
        graphics.fill(cx + d - 1, cy + d - 1, cx + d + 2, cy + d + 2, dim);

        // Center pip
        graphics.fill(cx - 1, cy, cx + 2, cy + 1, 0xFFFFFFFF);
        graphics.fill(cx, cy - 1, cx + 1, cy + 2, 0xFFFFFFFF);

        if (charging) {
            String pct = (int) (accuracy * 100) + "%";
            graphics.drawCenteredString(mc.font, pct, cx, cy + radius + 10, color);
            graphics.drawCenteredString(mc.font, "КВ-2", cx, cy - radius - 12, dim);
        }
    }

    private static void drawRing(GuiGraphics graphics, int cx, int cy, int radius, int color, int thickness) {
        int steps = Math.max(64, radius * 3);
        for (int t = 0; t < thickness; t++) {
            int r = radius + t;
            for (int i = 0; i < steps; i++) {
                double a = (Math.PI * 2.0 * i) / steps;
                int x = cx + (int) Math.round(Math.cos(a) * r);
                int y = cy + (int) Math.round(Math.sin(a) * r);
                graphics.fill(x, y, x + 1, y + 1, color);
            }
        }
    }
}
