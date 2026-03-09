package com.github.leopoko.tacz_attributes_addon.block;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Client-side screen for the Attribute Station.
 * Draws GUI programmatically (no external texture needed).
 * Shows a progress arrow and progress bar with percentage.
 */
public class AttributeStationScreen extends AbstractContainerScreen<AttributeStationMenu> {

    public AttributeStationScreen(AttributeStationMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = leftPos;
        int y = topPos;

        // ===== Background panel =====
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);

        // Outer border
        graphics.fill(x, y, x + imageWidth, y + 1, 0xFF373737);
        graphics.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, 0xFF373737);
        graphics.fill(x, y, x + 1, y + imageHeight, 0xFF373737);
        graphics.fill(x + imageWidth - 1, y, x + imageWidth, y + imageHeight, 0xFF373737);

        // Inner highlight (3D look)
        graphics.fill(x + 1, y + 1, x + imageWidth - 1, y + 2, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 1, x + 2, y + imageHeight - 1, 0xFFFFFFFF);

        // Separator between station area and inventory
        graphics.fill(x + 7, y + 78, x + imageWidth - 7, y + 79, 0xFF8B8B8B);

        // ===== Station slots =====
        drawSlotBg(graphics, x + 47, y + 34);   // Slot 0: Gun input
        drawSlotBg(graphics, x + 79, y + 10);   // Slot 1: Material
        drawSlotBg(graphics, x + 111, y + 34);  // Slot 2: Output

        // ===== Arrow between input and output =====
        drawArrow(graphics, x + 72, y + 37, menu.getProgressPercent());

        // ===== Progress bar =====
        drawProgressBar(graphics, x + 8, y + 60, 160, 10, menu.getProgressPercent());

        // ===== Slot labels =====
        graphics.drawString(font,
                Component.translatable("gui.tacz_attributes_addon.slot_gun"),
                x + 44, y + 24, 0xFF404040, false);
        graphics.drawString(font,
                Component.translatable("gui.tacz_attributes_addon.slot_material"),
                x + 72, y + 2, 0xFF404040, false);
        graphics.drawString(font,
                Component.translatable("gui.tacz_attributes_addon.slot_result"),
                x + 107, y + 24, 0xFF404040, false);

        // ===== Player inventory slots =====
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotBg(graphics, x + 7 + col * 18, y + 83 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawSlotBg(graphics, x + 7 + col * 18, y + 141);
        }
    }

    /**
     * Draw a slot background with 3D inset border.
     */
    private void drawSlotBg(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 1, 0xFF373737);
        graphics.fill(x, y, x + 1, y + 18, 0xFF373737);
        graphics.fill(x + 1, y + 17, x + 18, y + 18, 0xFFFFFFFF);
        graphics.fill(x + 17, y + 1, x + 18, y + 18, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF8B8B8B);
    }

    /**
     * Draw a right-pointing arrow with progress fill.
     */
    private void drawArrow(GuiGraphics graphics, int x, int y, float progress) {
        int w = 32;
        int h = 12;
        // Arrow body background
        graphics.fill(x, y, x + w, y + h, 0xFF555555);
        // Arrow body inner
        graphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xFF8B8B8B);

        // Progress fill (green bar inside arrow)
        if (progress > 0) {
            int fillW = Math.max(1, (int) ((w - 2) * progress));
            int color = progress >= 1.0f ? 0xFF00DD00 : 0xFF44BB44;
            graphics.fill(x + 1, y + 1, x + 1 + fillW, y + h - 1, color);
        }

        // Arrow head (triangle pointing right, using small rectangles)
        graphics.fill(x + w - 1, y + 1, x + w, y + h - 1, 0xFF555555);
        graphics.fill(x + w, y + 2, x + w + 1, y + h - 2, 0xFF555555);
        graphics.fill(x + w + 1, y + 3, x + w + 2, y + h - 3, 0xFF555555);
        graphics.fill(x + w + 2, y + 4, x + w + 3, y + h - 4, 0xFF555555);
        graphics.fill(x + w + 3, y + 5, x + w + 4, y + h - 5, 0xFF555555);
    }

    /**
     * Draw a progress bar with percentage text.
     */
    private void drawProgressBar(GuiGraphics graphics, int x, int y, int width, int height, float progress) {
        // Background
        graphics.fill(x, y, x + width, y + height, 0xFF373737);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF1E1E1E);

        // Fill
        if (progress > 0) {
            int fillW = (int) ((width - 2) * progress);
            int color;
            if (progress >= 1.0f) {
                color = 0xFF00DD00; // Complete = bright green
            } else if (progress >= 0.5f) {
                color = 0xFF44AAFF; // >50% = blue
            } else {
                color = 0xFF00AAFF; // <50% = cyan
            }
            graphics.fill(x + 1, y + 1, x + 1 + fillW, y + height - 1, color);
        }

        // Percentage text
        int percent = (int) (progress * 100);
        String text = percent + "%";
        int textW = font.width(text);
        graphics.drawString(font, text, x + (width - textW) / 2, y + (height - 8) / 2 + 1,
                0xFFFFFFFF, true);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Title removed to prevent overlap with Material label
        // Only show player inventory title
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }
}
