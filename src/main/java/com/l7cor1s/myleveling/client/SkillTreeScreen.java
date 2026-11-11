package com.l7cor1s.myleveling.client;

import com.l7cor1s.myleveling.network.UnlockSkillPayload;
import com.l7cor1s.myleveling.progress.PlayerProgress;
import com.l7cor1s.myleveling.rank.Rank;
import com.l7cor1s.myleveling.skill.Skill;
import com.l7cor1s.myleveling.skill.SkillBranch;
import com.l7cor1s.myleveling.skill.Skills;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Solo Leveling–inspired system UI: dark panel, purple/cyan accents, glowing rank.
 */
public class SkillTreeScreen extends Screen {
    private static final int BG = 0xF0080A14;
    private static final int PANEL = 0xF0121628;
    private static final int PANEL_INNER = 0xE00C1020;
    private static final int BORDER = 0xFF6C5CE7;
    private static final int BORDER_DIM = 0xFF3D4A7A;
    private static final int ACCENT = 0xFF4FC3F7;
    private static final int TEXT = 0xFFE8ECFF;
    private static final int TEXT_DIM = 0xFF8A93B8;
    private static final int UNLOCKED_FILL = 0xFF1A2A55;
    private static final int UNLOCKED_BORDER = 0xFF7B8CFF;
    private static final int AVAILABLE_FILL = 0xFF152038;
    private static final int AVAILABLE_BORDER = 0xFF4FC3F7;
    private static final int LOCKED_FILL = 0xFF10141F;
    private static final int LOCKED_BORDER = 0xFF2A3148;
    private static final int LOCKED_TEXT = 0xFF555E7A;
    private static final int BAR_BG = 0xFF1A2035;
    private static final int BAR_FILL = 0xFF5B8CFF;
    private static final int BAR_GLOW = 0x664FC3F7;

    private static final int NODE_W = 108;
    private static final int NODE_H = 28;
    private static final int COL_GAP = 18;
    private static final int ROW_GAP = 14;

    private final List<SkillNode> nodes = new ArrayList<>();
    private Skill hovered;

    public SkillTreeScreen() {
        super(Component.translatable("screen.myleveling.skills"));
    }

    @Override
    protected void init() {
        rebuildNodes();
    }

    @Override
    public void tick() {
        rebuildNodes();
    }

    private void rebuildNodes() {
        nodes.clear();

        int treeWidth = SkillBranch.values().length * NODE_W + (SkillBranch.values().length - 1) * COL_GAP;
        int startX = (this.width - treeWidth) / 2;
        int startY = 96;

        SkillBranch[] branches = SkillBranch.values();
        for (int col = 0; col < branches.length; col++) {
            List<Skill> skills = Skills.byBranch(branches[col]);
            int x = startX + col * (NODE_W + COL_GAP);
            for (int row = 0; row < skills.size(); row++) {
                int y = startY + 22 + row * (NODE_H + ROW_GAP);
                nodes.add(new SkillNode(skills.get(row), x, y));
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xD0050710);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        PlayerProgress progress = ClientProgressCache.get();
        Rank rank = Rank.fromXp(progress.xp());

        int panelW = Math.min(this.width - 24, SkillBranch.values().length * (NODE_W + COL_GAP) + 48);
        int panelH = Math.min(this.height - 20, 292);
        int panelX = (this.width - panelW) / 2;
        int panelY = Math.max(8, (this.height - panelH) / 2 - 8);

        drawSystemPanel(graphics, panelX, panelY, panelW, panelH);
        drawHeader(graphics, panelX, panelY, panelW, rank, progress, partialTick);
        drawBranches(graphics, mouseX, mouseY, progress);
        drawFooter(graphics, panelX, panelY, panelW, panelH);

        if (hovered != null) {
            drawTooltip(graphics, hovered, progress, mouseX, mouseY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawSystemPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        // Outer glow
        graphics.fill(x - 2, y - 2, x + w + 2, y + h + 2, 0x332A2A80);
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0x556C5CE7);

        graphics.fill(x, y, x + w, y + h, PANEL);
        graphics.renderOutline(x, y, w, h, BORDER);
        graphics.fill(x + 3, y + 3, x + w - 3, y + h - 3, PANEL_INNER);
        graphics.renderOutline(x + 3, y + 3, w - 6, h - 6, BORDER_DIM);

        // Top accent line
        graphics.fill(x + 12, y + 4, x + w - 12, y + 5, ACCENT);
    }

    private void drawHeader(
            GuiGraphics graphics,
            int panelX,
            int panelY,
            int panelW,
            Rank rank,
            PlayerProgress progress,
            float partialTick
    ) {
        int centerX = panelX + panelW / 2;

        graphics.drawCenteredString(this.font, Component.translatable("screen.myleveling.system_title"), centerX, panelY + 10, ACCENT);
        graphics.drawCenteredString(this.font, Component.translatable("screen.myleveling.system_subtitle"), centerX, panelY + 22, TEXT_DIM);

        // Glowing rank
        String rankText = rank.getDisplayName();
        int rankColor = rankColor(rank);
        float pulse = 0.55f + 0.45f * Mth.sin((System.currentTimeMillis() % 4000L) / 4000f * Mth.TWO_PI + partialTick * 0.05f);
        int glowAlpha = Mth.clamp((int) (pulse * 90), 30, 110);
        int glowColor = (glowAlpha << 24) | (rankColor & 0x00FFFFFF);

        graphics.pose().pushPose();
        graphics.pose().translate(centerX, panelY + 48, 0);
        graphics.pose().scale(2.4f, 2.4f, 1.0f);
        // Soft glow layers
        graphics.drawCenteredString(this.font, rankText, 1, 0, glowColor);
        graphics.drawCenteredString(this.font, rankText, -1, 0, glowColor);
        graphics.drawCenteredString(this.font, rankText, 0, 1, glowColor);
        graphics.drawCenteredString(this.font, rankText, 0, -1, glowColor);
        graphics.drawCenteredString(this.font, rankText, 0, 0, rankColor);
        graphics.pose().popPose();

        // XP bar
        int barW = Math.min(220, panelW - 80);
        int barX = centerX - barW / 2;
        int barY = panelY + 70;
        float xpProgress = xpBarProgress(progress.xp(), rank);

        graphics.fill(barX - 1, barY - 1, barX + barW + 1, barY + 7, 0x664FC3F7);
        graphics.fill(barX, barY, barX + barW, barY + 6, BAR_BG);
        int filled = Math.round(barW * xpProgress);
        if (filled > 0) {
            graphics.fill(barX, barY, barX + filled, barY + 6, BAR_FILL);
            graphics.fill(barX, barY, barX + filled, barY + 2, BAR_GLOW);
        }

        String xpLabel;
        if (rank.isMax()) {
            xpLabel = progress.xp() + " XP";
        } else {
            xpLabel = progress.xp() + " / " + rank.next().getRequiredXp() + " XP";
        }
        graphics.drawCenteredString(this.font, xpLabel, centerX, barY + 9, TEXT_DIM);

        graphics.drawCenteredString(
                this.font,
                Component.translatable("screen.myleveling.skill_points", progress.skillPoints()),
                centerX,
                barY + 20,
                ACCENT
        );
    }

    private void drawBranches(GuiGraphics graphics, int mouseX, int mouseY, PlayerProgress progress) {
        hovered = null;

        SkillBranch[] branches = SkillBranch.values();
        int treeWidth = branches.length * NODE_W + (branches.length - 1) * COL_GAP;
        int startX = (this.width - treeWidth) / 2;
        int labelY = nodes.isEmpty() ? 118 : nodes.getFirst().y - 16;

        for (int col = 0; col < branches.length; col++) {
            int x = startX + col * (NODE_W + COL_GAP) + NODE_W / 2;
            graphics.drawCenteredString(this.font, branches[col].getDisplayName(), x, labelY, TEXT);
        }

        // Connector lines within each column
        for (int col = 0; col < branches.length; col++) {
            SkillBranch branch = branches[col];
            List<SkillNode> column = nodes.stream().filter(n -> n.skill.branch() == branch).toList();
            for (int i = 0; i < column.size() - 1; i++) {
                SkillNode a = column.get(i);
                SkillNode b = column.get(i + 1);
                int lineX = a.x + NODE_W / 2;
                boolean linked = progress.hasSkill(a.skill.id());
                int color = linked ? 0xFF5B8CFF : 0xFF2A3148;
                graphics.fill(lineX - 1, a.y + NODE_H, lineX + 1, b.y, color);
            }
        }

        for (SkillNode node : nodes) {
            boolean unlocked = progress.hasSkill(node.skill.id());
            boolean canUnlock = canUnlock(progress, node.skill);
            boolean isHovered = mouseX >= node.x && mouseX < node.x + NODE_W
                    && mouseY >= node.y && mouseY < node.y + NODE_H;

            if (isHovered) {
                hovered = node.skill;
            }

            int fill;
            int border;
            int textColor;
            if (unlocked) {
                fill = UNLOCKED_FILL;
                border = UNLOCKED_BORDER;
                textColor = TEXT;
            } else if (canUnlock) {
                fill = AVAILABLE_FILL;
                border = AVAILABLE_BORDER;
                textColor = TEXT;
            } else {
                fill = LOCKED_FILL;
                border = LOCKED_BORDER;
                textColor = LOCKED_TEXT;
            }

            if (isHovered) {
                graphics.fill(node.x - 1, node.y - 1, node.x + NODE_W + 1, node.y + NODE_H + 1, 0x554FC3F7);
            }

            graphics.fill(node.x, node.y, node.x + NODE_W, node.y + NODE_H, fill);
            graphics.renderOutline(node.x, node.y, NODE_W, NODE_H, border);

            // Left rank marker strip
            graphics.fill(node.x, node.y, node.x + 3, node.y + NODE_H, border);

            String label = node.skill.getDisplayName().getString();
            if (this.font.width(label) > NODE_W - 10) {
                label = this.font.plainSubstrByWidth(label, NODE_W - 14) + "…";
            }
            graphics.drawCenteredString(this.font, label, node.x + NODE_W / 2, node.y + 10, textColor);

            if (unlocked) {
                graphics.drawString(this.font, "◆", node.x + NODE_W - 10, node.y + 3, ACCENT, false);
            } else {
                graphics.drawString(this.font, String.valueOf(node.skill.cost()), node.x + NODE_W - 10, node.y + 3, TEXT_DIM, false);
            }
        }
    }

    private void drawFooter(GuiGraphics graphics, int panelX, int panelY, int panelW, int panelH) {
        graphics.drawCenteredString(
                this.font,
                Component.translatable("screen.myleveling.hint"),
                panelX + panelW / 2,
                panelY + panelH - 14,
                TEXT_DIM
        );
    }

    private void drawTooltip(GuiGraphics graphics, Skill skill, PlayerProgress progress, int mouseX, int mouseY) {
        List<Component> lines = new ArrayList<>();
        lines.add(skill.getDisplayName());
        lines.add(skill.getDescription());
        lines.add(Component.translatable(
                "screen.myleveling.tooltip_meta",
                skill.branch().getDisplayName(),
                skill.cost(),
                skill.minRank().getDisplayName()
        ));
        skill.prerequisite().ifPresent(prereq ->
                lines.add(Component.translatable("screen.myleveling.tooltip_prereq", prereq))
        );

        if (progress.hasSkill(skill.id())) {
            lines.add(Component.translatable("screen.myleveling.tooltip_unlocked"));
        } else if (canUnlock(progress, skill)) {
            lines.add(Component.translatable("screen.myleveling.tooltip_click"));
        } else {
            lines.add(Component.translatable("screen.myleveling.tooltip_locked"));
        }

        graphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            PlayerProgress progress = ClientProgressCache.get();
            for (SkillNode node : nodes) {
                if (mouseX >= node.x && mouseX < node.x + NODE_W
                        && mouseY >= node.y && mouseY < node.y + NODE_H
                        && canUnlock(progress, node.skill)) {
                    PacketDistributor.sendToServer(new UnlockSkillPayload(node.skill.id()));
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

    private static boolean canUnlock(PlayerProgress progress, Skill skill) {
        if (progress.hasSkill(skill.id())) {
            return false;
        }
        if (progress.skillPoints() < skill.cost()) {
            return false;
        }
        Rank rank = Rank.fromXp(progress.xp());
        if (rank.ordinal() < skill.minRank().ordinal()) {
            return false;
        }
        return skill.prerequisite().isEmpty() || progress.hasSkill(skill.prerequisite().get());
    }

    private static float xpBarProgress(int xp, Rank rank) {
        if (rank.isMax()) {
            return 1.0f;
        }
        int from = rank.getRequiredXp();
        int to = rank.next().getRequiredXp();
        if (to <= from) {
            return 1.0f;
        }
        return Mth.clamp((xp - from) / (float) (to - from), 0.0f, 1.0f);
    }

    private static int rankColor(Rank rank) {
        return switch (rank) {
            case E -> 0xFF9AA4C7;
            case D -> 0xFF5DFFB0;
            case C -> 0xFF4FC3F7;
            case B -> 0xFF7B5CFF;
            case A -> 0xFFFF6BCB;
            case S -> 0xFFFFE082;
        };
    }

    private record SkillNode(Skill skill, int x, int y) {
    }
}
