package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.quest.QuestCatalog;
import com.l7cor1s.myleveling.skill.SkillCatalog;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Left-center active icons with cooldowns; quest progress listed beneath.
 */
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

        int slotSize = 24;
        int gap = 6;
        int x = 8;
        int y = graphics.guiHeight() / 2 - 40;

        graphics.drawString(mc.font, Component.translatable("hud.myleveling.mana", (int) progress.mana()), x, y - 12, 0xFF7B8CFF, false);

        for (int slot = 0; slot < PlayerProgress.MAX_EQUIPPED_ACTIVES; slot++) {
            int sy = y + slot * (slotSize + gap);
            String activeId = progress.equipped(slot);

            graphics.fill(x - 1, sy - 1, x + slotSize + 1, sy + slotSize + 1, 0xAA6C5CE7);
            graphics.fill(x, sy, x + slotSize, sy + slotSize, 0xE0101628);
            graphics.renderOutline(x, sy, slotSize, slotSize, 0xFF4FC3F7);

            String keyLabel = slot == 0 ? "R" : "F";
            graphics.drawString(mc.font, keyLabel, x + 2, sy + 2, 0xFF8A93B8, false);

            if (activeId == null || activeId.isEmpty()) {
                graphics.drawCenteredString(mc.font, "-", x + slotSize / 2, sy + 9, 0xFF555E7A);
                continue;
            }

            SkillCatalog.active(activeId).ifPresent(active -> {
                String shortName = active.getShortName().getString();
                if (mc.font.width(shortName) > slotSize - 4) {
                    shortName = mc.font.plainSubstrByWidth(shortName, slotSize - 6);
                }
                graphics.drawCenteredString(mc.font, shortName, x + slotSize / 2, sy + 9, 0xFFE8ECFF);

                long remaining = progress.cooldownRemaining(active.id(), gameTime);
                if (remaining > 0) {
                    float ratio = remaining / (float) Math.max(1, active.cooldownTicks());
                    int cover = Math.round(slotSize * ratio);
                    graphics.fill(x, sy + slotSize - cover, x + slotSize, sy + slotSize, 0xAA0A0A14);
                    String secs = String.format("%.0f", remaining / 20.0);
                    graphics.drawCenteredString(mc.font, secs, x + slotSize / 2, sy + 9, 0xFFFFD54F);
                }
            });
        }

        int qy = y + 2 * (slotSize + gap) + 8;
        graphics.drawString(mc.font, Component.translatable("hud.myleveling.quests"), x, qy, 0xFF4FC3F7, false);
        qy += 10;
        int shown = 0;
        for (String questId : progress.activeQuests()) {
            if (shown >= 4) {
                break;
            }
            var def = QuestCatalog.byId(questId);
            if (def.isEmpty()) {
                continue;
            }
            int cur = progress.questCount(questId);
            int max = def.get().targetCount();
            String line = def.get().title().getString() + " " + cur + "/" + max;
            if (mc.font.width(line) > 140) {
                line = mc.font.plainSubstrByWidth(line, 136) + "…";
            }
            graphics.drawString(mc.font, line, x, qy, 0xFFE8ECFF, false);
            qy += 10;
            shown++;
        }
        if (shown == 0) {
            graphics.drawString(mc.font, Component.translatable("hud.myleveling.no_quests"), x, qy, 0xFF8A93B8, false);
        }

        if (progress.monarchDefeated()) {
            graphics.drawString(mc.font, Component.translatable("hud.myleveling.gate_cleared"), x, qy + 14, 0xFFFFE082, false);
        }
    }
}
