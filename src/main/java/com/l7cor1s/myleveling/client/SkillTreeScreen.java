package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.compat.CompatMods;
import com.l7cor1s.myleveling.network.SkillActionPayload;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.quest.QuestCatalog;
import com.l7cor1s.myleveling.quest.QuestDefinition;
import com.l7cor1s.myleveling.rank.Rank;
import com.l7cor1s.myleveling.skill.ActiveSkill;
import com.l7cor1s.myleveling.skill.MinorUpgrade;
import com.l7cor1s.myleveling.skill.PassiveSkill;
import com.l7cor1s.myleveling.skill.SkillBranch;
import com.l7cor1s.myleveling.skill.SkillCatalog;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Solo Leveling system UI: rank header, SP/PP, branch tabs, passives/actives, minor shop, reset.
 */
public class SkillTreeScreen extends Screen {
    // Light Solo Leveling system panel — high contrast, still cyan/system accent
    private static final int PANEL = 0xF0F4F7FF;
    private static final int PANEL_INNER = 0xE8EEF3FF;
    private static final int BORDER = 0xFF3A7BD5;
    private static final int ACCENT = 0xFF1E88E5;
    private static final int TEXT = 0xFF1A2333;
    private static final int TEXT_DIM = 0xFF5A6780;
    private static final int ROW = 0xFFE8EEF8;
    private static final int ROW_ACTIVE = 0xFFD6E8FF;
    private static final int ROW_CAN = 0xFFE3F2FD;
    private static final int ROW_LOCKED = 0xFFD5DBE6;

    private enum Tab { QUESTS, MINORS, MELEE, RANGED, MAGIC, ENGINEERING, SURVIVAL, HUNTER }

    private Tab tab = Tab.QUESTS;
    private final List<ClickZone> zones = new ArrayList<>();
    private Component hoveredTooltip;

    public SkillTreeScreen() {
        super(Component.translatable("screen.myleveling.skills"));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xB0A8B4C8);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        zones.clear();
        hoveredTooltip = null;

        PlayerProgress progress = ClientProgressCache.get();
        Rank rank = Rank.fromXp(progress.xp());

        int panelW = Math.min(this.width - 16, 460);
        int panelH = Math.min(this.height - 12, 320);
        int panelX = (this.width - panelW) / 2;
        int panelY = Math.max(6, (this.height - panelH) / 2);

        drawPanel(graphics, panelX, panelY, panelW, panelH);
        drawHeader(graphics, panelX, panelY, panelW, rank, progress, partialTick);
        drawTabs(graphics, panelX, panelY, panelW, mouseX, mouseY);
        drawContent(graphics, panelX, panelY, panelW, panelH, progress, rank, mouseX, mouseY);
        drawFooter(graphics, panelX, panelY, panelW, panelH, mouseX, mouseY);
        updateHover(mouseX, mouseY);

        if (hoveredTooltip != null) {
            graphics.renderTooltip(this.font, this.font.split(hoveredTooltip, 200), mouseX, mouseY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.fill(x - 3, y - 3, x + w + 3, y + h + 3, 0x552A4A80);
        graphics.fill(x, y, x + w, y + h, PANEL);
        graphics.renderOutline(x, y, w, h, BORDER);
        graphics.fill(x + 3, y + 3, x + w - 3, y + h - 3, PANEL_INNER);
        graphics.fill(x + 12, y + 4, x + w - 12, y + 6, ACCENT);
    }

    private void drawHeader(GuiGraphics graphics, int x, int y, int w, Rank rank, PlayerProgress progress, float pt) {
        int cx = x + w / 2;
        graphics.drawCenteredString(this.font, Component.translatable("screen.myleveling.system_title"), cx, y + 8, ACCENT);

        int rankColor = rankColor(rank);
        float pulse = 0.55f + 0.45f * Mth.sin((System.currentTimeMillis() % 4000L) / 4000f * Mth.TWO_PI);
        int glow = ((int) (pulse * 80) << 24) | (rankColor & 0xFFFFFF);
        graphics.pose().pushPose();
        graphics.pose().translate(cx, y + 28, 0);
        graphics.pose().scale(2.0f, 2.0f, 1f);
        graphics.drawCenteredString(this.font, rank.getDisplayName(), 0, 0, glow);
        graphics.drawCenteredString(this.font, rank.getDisplayName(), 0, 0, rankColor);
        graphics.pose().popPose();

        int barW = Math.min(200, w - 60);
        int barX = cx - barW / 2;
        int barY = y + 48;
        float fill = xpProgress(progress.xp(), rank);
        graphics.fill(barX, barY, barX + barW, barY + 6, 0xFFC5D0E0);
        graphics.renderOutline(barX, barY, barW, 6, 0xFF8AA0BE);
        graphics.fill(barX, barY, barX + Math.round(barW * fill), barY + 6, 0xFF42A5F5);

        String xpText = rank.isMax()
                ? progress.xp() + " XP"
                : progress.xp() + " / " + rank.next().getRequiredXp() + " XP";
        graphics.drawCenteredString(this.font, xpText, cx, barY + 8, TEXT_DIM);
        graphics.drawCenteredString(this.font,
                Component.translatable("screen.myleveling.points", progress.skillPoints(), progress.passivePoints()),
                cx, barY + 18, ACCENT);
    }

    private void drawTabs(GuiGraphics graphics, int panelX, int panelY, int panelW, int mouseX, int mouseY) {
        Tab[] tabs = Tab.values();
        int tabY = panelY + 78;
        int tabW = Math.max(36, (panelW - 20) / tabs.length);
        int startX = panelX + 10;

        for (int i = 0; i < tabs.length; i++) {
            Tab t = tabs[i];
            int tx = startX + i * tabW;
            boolean selected = t == tab;
            boolean hover = mouseX >= tx && mouseX < tx + tabW - 2 && mouseY >= tabY && mouseY < tabY + 14;
            graphics.fill(tx, tabY, tx + tabW - 2, tabY + 14, selected ? 0xFFBBDEFB : (hover ? 0xFFE3F2FD : 0xFFDCE3EE));
            graphics.renderOutline(tx, tabY, tabW - 2, 14, selected ? ACCENT : 0xFF90A4C0);
            String label = tabLabel(t);
            graphics.drawCenteredString(this.font, label, tx + (tabW - 2) / 2, tabY + 3, selected ? 0xFF0D47A1 : TEXT_DIM);
            zones.add(new ClickZone(tx, tabY, tabW - 2, 14, () -> tab = t, Component.translatable("screen.myleveling.tab." + t.name().toLowerCase())));
        }
    }

    private void drawContent(GuiGraphics graphics, int panelX, int panelY, int panelW, int panelH,
                             PlayerProgress progress, Rank rank, int mouseX, int mouseY) {
        int contentX = panelX + 12;
        int contentY = panelY + 98;
        int contentW = panelW - 24;

        if (tab == Tab.QUESTS) {
            drawQuests(graphics, contentX, contentY, contentW, progress);
            return;
        }
        if (tab == Tab.MINORS) {
            drawMinors(graphics, contentX, contentY, contentW, progress, mouseX, mouseY);
            return;
        }

        SkillBranch branch = branchForTab(tab);
        graphics.drawString(this.font, branch.getDisplayName(), contentX, contentY, TEXT, false);
        graphics.drawString(this.font, Component.translatable("screen.myleveling.passives"), contentX, contentY + 12, ACCENT, false);

        int y = contentY + 24;
        for (Rank req : List.of(Rank.D, Rank.C, Rank.B, Rank.A, Rank.S)) {
            List<PassiveSkill> pair = SkillCatalog.passivesFor(branch, req);
            graphics.drawString(this.font, req.getDisplayName(), contentX, y + 4, TEXT_DIM, false);
            int bx = contentX + 16;
            for (PassiveSkill passive : pair) {
                boolean selected = progress.hasPassive(passive.id());
                boolean locked = !rank.isAtLeast(req);
                boolean can = !locked && !selected && !slotTaken(progress, passive)
                        && progress.passivePoints() >= passive.passivePointCost();
                int color = selected ? ROW_ACTIVE : (can ? ROW_CAN : ROW_LOCKED);
                int border = selected ? 0xFF1565C0 : (can ? ACCENT : 0xFF9AA8BC);
                int text = selected || can ? TEXT : 0xFF7A8699;
                graphics.fill(bx, y, bx + 150, y + 16, color);
                graphics.renderOutline(bx, y, 150, 16, border);
                String name = passive.getDisplayName().getString();
                if (this.font.width(name) > 140) {
                    name = this.font.plainSubstrByWidth(name, 136) + "…";
                }
                graphics.drawString(this.font, name, bx + 4, y + 4, text, false);

                int finalBx = bx;
                int finalY = y;
                zones.add(new ClickZone(finalBx, finalY, 150, 16, () -> {
                    if (can) {
                        PacketDistributor.sendToServer(SkillActionPayload.selectPassive(passive.id()));
                    }
                }, Component.empty().append(passive.getDisplayName()).append("\n").append(passive.getDescription())));
                bx += 156;
            }
            y += 20;
        }

        graphics.drawString(this.font, Component.translatable("screen.myleveling.actives"), contentX, y + 4, ACCENT, false);
        y += 16;
        for (ActiveSkill active : SkillCatalog.activesFor(branch)) {
            boolean unlocked = progress.hasActiveUnlocked(active.id());
            boolean canUnlock = !unlocked && rank.isAtLeast(active.requiredRank())
                    && progress.passivePoints() >= active.passivePointCost();
            int bx = contentX;
            graphics.fill(bx, y, bx + 210, y + 16, unlocked ? ROW_ACTIVE : ROW_LOCKED);
            graphics.renderOutline(bx, y, 210, 16, unlocked ? 0xFF1565C0 : (canUnlock ? ACCENT : 0xFF9AA8BC));
            String label = active.getDisplayName().getString() + " [" + active.requiredRank() + ", " + active.passivePointCost() + "PP]";
            graphics.drawString(this.font, this.font.plainSubstrByWidth(label, 200), bx + 4, y + 4, unlocked || canUnlock ? TEXT : 0xFF7A8699, false);

            int finalY = y;
            zones.add(new ClickZone(bx, finalY, 210, 16, () -> {
                if (canUnlock) {
                    PacketDistributor.sendToServer(SkillActionPayload.unlockActive(active.id()));
                } else if (unlocked) {
                    // Equip into first empty / slot 0 by default; shift into slot 1 conceptually via second click cycles.
                    int slot = progress.equipped(0).equals(active.id()) ? 1 : 0;
                    PacketDistributor.sendToServer(SkillActionPayload.equipActive(slot, active.id()));
                }
            }, Component.empty().append(active.getDisplayName()).append("\n").append(active.getDescription())
                    .append("\n").append(Component.translatable("screen.myleveling.active_hint"))));

            // Equip buttons
            if (unlocked) {
                for (int slot = 0; slot < 2; slot++) {
                    int sx = bx + 216 + slot * 34;
                    boolean eq = active.id().equals(progress.equipped(slot));
                    graphics.fill(sx, y, sx + 30, y + 16, eq ? 0xFFC8E6C9 : 0xFFE8EEF8);
                    graphics.renderOutline(sx, y, 30, 16, eq ? 0xFF2E7D32 : 0xFF90A4C0);
                    graphics.drawCenteredString(this.font, "S" + (slot + 1), sx + 15, y + 4, TEXT);
                    int finalSlot = slot;
                    zones.add(new ClickZone(sx, finalY, 30, 16, () ->
                            PacketDistributor.sendToServer(SkillActionPayload.equipActive(finalSlot, active.id())),
                            Component.translatable("screen.myleveling.equip_slot", slot + 1)));
                }
            }
            y += 20;
        }
    }

    private void drawQuests(GuiGraphics graphics, int x, int y, int w, PlayerProgress progress) {
        graphics.drawString(this.font, Component.translatable("screen.myleveling.quests_title"), x, y, ACCENT, false);
        if (progress.monarchDefeated()) {
            graphics.drawString(this.font, Component.translatable("screen.myleveling.goal_done"), x, y + 10, 0xFF2E7D32, false);
        } else {
            graphics.drawString(this.font, Component.translatable("screen.myleveling.goal_hint"), x, y + 10, TEXT_DIM, false);
        }

        int row = y + 24;
        for (QuestDefinition quest : QuestCatalog.availableFor(progress, CompatMods.isLoaded(CompatMods.MCA))) {
            if (row > y + 180) {
                break;
            }
            boolean active = progress.activeQuests().contains(quest.id());
            boolean done = progress.completedQuests().contains(quest.id());
            int cur = progress.questCount(quest.id());
            String line = quest.title().getString() + " [" + quest.kind().name() + "] "
                    + (active ? cur + "/" + quest.targetCount() : (done ? "✓" : "·"));
            graphics.fill(x, row, x + w, row + 16, active ? ROW_ACTIVE : ROW);
            graphics.renderOutline(x, row, w, 16, active ? ACCENT : 0xFF9AA8BC);
            graphics.drawString(this.font, this.font.plainSubstrByWidth(line, w - 8), x + 4, row + 4, TEXT, false);

            int finalRow = row;
            zones.add(new ClickZone(x, finalRow, w, 16, () -> {
                if (active && cur >= quest.targetCount()) {
                    PacketDistributor.sendToServer(SkillActionPayload.turnInQuest(quest.id()));
                } else if (!active) {
                    PacketDistributor.sendToServer(SkillActionPayload.acceptQuest(quest.id()));
                }
            }, Component.empty().append(quest.title()).append(" — ").append(quest.description())));
            row += 18;
        }
    }

    private void drawMinors(GuiGraphics graphics, int x, int y, int w, PlayerProgress progress, int mouseX, int mouseY) {
        graphics.drawString(this.font, Component.translatable("screen.myleveling.minors_title"), x, y, ACCENT, false);
        graphics.drawString(this.font, Component.translatable("screen.myleveling.minors_hint"), x, y + 10, TEXT_DIM, false);
        int row = y + 24;
        for (MinorUpgrade upgrade : MinorUpgrade.all()) {
            int level = progress.minorLevel(upgrade.id());
            boolean can = progress.skillPoints() >= upgrade.costPerLevel() && level < upgrade.maxLevel();
            graphics.fill(x, row, x + w, row + 16, can ? ROW_CAN : ROW);
            graphics.renderOutline(x, row, w, 16, can ? ACCENT : 0xFF9AA8BC);
            String line = upgrade.getDisplayName().getString() + "  " + level + "/" + upgrade.maxLevel()
                    + "  (" + upgrade.costPerLevel() + " SP)";
            graphics.drawString(this.font, line, x + 4, row + 4, TEXT, false);
            int finalRow = row;
            zones.add(new ClickZone(x, finalRow, w, 16, () -> {
                if (can) {
                    PacketDistributor.sendToServer(SkillActionPayload.buyMinor(upgrade.id()));
                }
            }, Component.empty().append(upgrade.getDisplayName()).append("\n").append(upgrade.getDescription())));
            row += 18;
        }
    }

    private void drawFooter(GuiGraphics graphics, int panelX, int panelY, int panelW, int panelH, int mouseX, int mouseY) {
        int bx = panelX + panelW - 90;
        int by = panelY + panelH - 20;
        boolean hover = mouseX >= bx && mouseX < bx + 78 && mouseY >= by && mouseY < by + 14;
        graphics.fill(bx, by, bx + 78, by + 14, hover ? 0xFFFFCDD2 : 0xFFFFEBEE);
        graphics.renderOutline(bx, by, 78, 14, 0xFFE53935);
        graphics.drawCenteredString(this.font, Component.translatable("screen.myleveling.reset"), bx + 39, by + 3, 0xFFB71C1C);
        zones.add(new ClickZone(bx, by, 78, 14,
                () -> PacketDistributor.sendToServer(SkillActionPayload.reset()),
                Component.translatable("screen.myleveling.reset_hint")));

        graphics.drawString(this.font, Component.translatable("screen.myleveling.hint"), panelX + 12, by + 3, TEXT_DIM, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (ClickZone zone : zones) {
                if (mouseX >= zone.x && mouseX < zone.x + zone.w && mouseY >= zone.y && mouseY < zone.y + zone.h) {
                    zone.action.run();
                    // refresh tooltip target
                    hoveredTooltip = zone.tooltip;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void updateHover(int mouseX, int mouseY) {
        for (ClickZone zone : zones) {
            if (mouseX >= zone.x && mouseX < zone.x + zone.w && mouseY >= zone.y && mouseY < zone.y + zone.h) {
                hoveredTooltip = zone.tooltip;
                return;
            }
        }
    }

    private static boolean slotTaken(PlayerProgress progress, PassiveSkill passive) {
        if (progress.hasPassive(passive.pairId())) {
            return true;
        }
        for (PassiveSkill other : SkillCatalog.passivesFor(passive.branch(), passive.requiredRank())) {
            if (progress.hasPassive(other.id())) {
                return true;
            }
        }
        return false;
    }

    private static SkillBranch branchForTab(Tab tab) {
        return switch (tab) {
            case MELEE -> SkillBranch.MELEE;
            case RANGED -> SkillBranch.RANGED;
            case MAGIC -> SkillBranch.MAGIC;
            case ENGINEERING -> SkillBranch.ENGINEERING;
            case SURVIVAL -> SkillBranch.SURVIVAL;
            case HUNTER -> SkillBranch.HUNTER;
            default -> SkillBranch.MELEE;
        };
    }

    private static String tabLabel(Tab tab) {
        return switch (tab) {
            case QUESTS -> "QV";
            case MINORS -> "SP";
            case MELEE -> "ML";
            case RANGED -> "RG";
            case MAGIC -> "MG";
            case ENGINEERING -> "EN";
            case SURVIVAL -> "SV";
            case HUNTER -> "HT";
        };
    }

    private static float xpProgress(int xp, Rank rank) {
        if (rank.isMax()) {
            return 1f;
        }
        int from = rank.getRequiredXp();
        int to = rank.next().getRequiredXp();
        return Mth.clamp((xp - from) / (float) (to - from), 0f, 1f);
    }

    private static int rankColor(Rank rank) {
        // Darker/saturated for light Solo Leveling panels
        return switch (rank) {
            case E -> 0xFF546E7A;
            case D -> 0xFF00897B;
            case C -> 0xFF1565C0;
            case B -> 0xFF5E35B1;
            case A -> 0xFFC2185B;
            case S -> 0xFFF9A825;
        };
    }

    private record ClickZone(int x, int y, int w, int h, Runnable action, Component tooltip) {
    }
}
