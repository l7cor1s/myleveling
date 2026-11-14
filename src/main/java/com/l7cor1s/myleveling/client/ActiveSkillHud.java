package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.skill.SkillCatalog;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class ActiveSkillHud {
    private ActiveSkillHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.screen != null) {
            return;
        }

        PlayerProgress progress = ClientProgressCache.get();
        long gameTime = mc.level != null ? mc.level.getGameTime() : 0L;

        int slotSize = 22;
        int gap = 8;
        int totalW = slotSize * 2 + gap;
        int x = (graphics.guiWidth() - totalW) / 2;
        int y = graphics.guiHeight() - 66;

        for (int slot = 0; slot < PlayerProgress.MAX_EQUIPPED_ACTIVES; slot++) {
            int sx = x + slot * (slotSize + gap);
            String activeId = progress.equipped(slot);

            graphics.fill(sx - 1, y - 1, sx + slotSize + 1, y + slotSize + 1, 0xAA6C5CE7);
            graphics.fill(sx, y, sx + slotSize, y + slotSize, 0xE0101628);
            graphics.renderOutline(sx, y, slotSize, slotSize, 0xFF4FC3F7);

            String keyLabel = slot == 0 ? "R" : "F";
            graphics.drawString(mc.font, keyLabel, sx + 2, y + 2, 0xFF8A93B8, false);

            if (activeId == null || activeId.isEmpty()) {
                graphics.drawCenteredString(mc.font, "-", sx + slotSize / 2, y + 8, 0xFF555E7A);
                continue;
            }

            SkillCatalog.active(activeId).ifPresent(active -> {
                String shortName = active.getShortName().getString();
                if (mc.font.width(shortName) > slotSize - 4) {
                    shortName = mc.font.plainSubstrByWidth(shortName, slotSize - 6);
                }
                graphics.drawCenteredString(mc.font, shortName, sx + slotSize / 2, y + 8, 0xFFE8ECFF);

                long remaining = progress.cooldownRemaining(active.id(), gameTime);
                if (remaining > 0) {
                    float ratio = remaining / (float) Math.max(1, active.cooldownTicks());
                    int cover = Math.round(slotSize * ratio);
                    graphics.fill(sx, y + slotSize - cover, sx + slotSize, y + slotSize, 0xAA0A0A14);
                    String secs = String.format("%.1f", remaining / 20.0);
                    graphics.drawCenteredString(mc.font, secs, sx + slotSize / 2, y + 8, 0xFFFFD54F);
                }
            });
        }

        // Mana pip
        float mana = progress.mana();
        graphics.drawCenteredString(
                mc.font,
                Component.translatable("hud.myleveling.mana", (int) mana),
                graphics.guiWidth() / 2,
                y - 12,
                0xFF7B8CFF
        );
    }
}
