package com.github.leopoko.tacz_attributes_addon.block;

import com.github.leopoko.tacz_attributes_addon.data.AttributeEntry;
import com.github.leopoko.tacz_attributes_addon.data.AttributeRegistry;
import com.github.leopoko.tacz_attributes_addon.network.EnhancementActionPacket;
import com.github.leopoko.tacz_attributes_addon.network.EnhancementChoicesPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Client-side screen for the Enhancement Station.
 * Draws choice buttons and reroll button programmatically.
 */
public class EnhancementStationScreen extends AbstractContainerScreen<EnhancementStationMenu> {

    private static final int CHOICE_BTN_X = 56;
    private static final int CHOICE_BTN_W = 154;
    private static final int CHOICE_BTN_H = 20;
    private static final int CHOICE_SPACING = 22;
    private static final int CHOICE_START_Y = 10;

    private static final int REROLL_BTN_W = 60;
    private static final int REROLL_BTN_H = 16;

    public EnhancementStationScreen(EnhancementStationMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 220;
        this.imageHeight = 200;
        this.inventoryLabelY = 105;
        this.inventoryLabelX = 8;
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
        graphics.fill(x + 7, y + 112, x + imageWidth - 7, y + 113, 0xFF8B8B8B);

        // ===== Slot backgrounds =====
        drawSlotBg(graphics, x + 25, y + 34);   // Slot 0: Gun
        drawSlotBg(graphics, x + 25, y + 56);   // Slot 1: Material

        // Slot labels
        graphics.drawString(font,
                Component.translatable("gui.tacz_attributes_addon.enhancement.slot_gun"),
                x + 9, y + 38, 0xFF404040, false);
        graphics.drawString(font,
                Component.translatable("gui.tacz_attributes_addon.enhancement.slot_material"),
                x + 3, y + 60, 0xFF404040, false);

        // ===== Choice buttons =====
        List<EnhancementChoicesPacket.ChoiceEntry> choices = menu.getClientChoices();
        Map<String, AttributeEntry> entryMap = AttributeRegistry.getEntries().stream()
                .collect(Collectors.toMap(AttributeEntry::getAttributeId, e -> e, (a, b) -> a));

        boolean maxReached = isMaxReached();
        boolean hasGun = !menu.getSlot(0).getItem().isEmpty();

        if (!hasGun) {
            // No gun message
            Component noGun = Component.translatable("gui.tacz_attributes_addon.enhancement.no_gun");
            int textW = font.width(noGun);
            graphics.drawString(font, noGun, x + CHOICE_BTN_X + (CHOICE_BTN_W - textW) / 2,
                    y + CHOICE_START_Y + 30, 0xFF888888, false);
        } else if (choices.isEmpty()) {
            Component loading = Component.literal("...");
            int textW = font.width(loading);
            graphics.drawString(font, loading, x + CHOICE_BTN_X + (CHOICE_BTN_W - textW) / 2,
                    y + CHOICE_START_Y + 30, 0xFF888888, false);
        } else {
            for (int i = 0; i < choices.size(); i++) {
                EnhancementChoicesPacket.ChoiceEntry choice = choices.get(i);
                int btnX = x + CHOICE_BTN_X;
                int btnY = y + CHOICE_START_Y + i * CHOICE_SPACING;

                boolean hovered = !maxReached && isMouseOver(mouseX, mouseY, btnX, btnY, CHOICE_BTN_W, CHOICE_BTN_H);

                // Button background
                int bgColor;
                if (maxReached) {
                    bgColor = 0xFF2A2A2A;
                } else if (hovered) {
                    bgColor = 0xFF4A4A4A;
                } else {
                    bgColor = 0xFF3A3A3A;
                }
                graphics.fill(btnX, btnY, btnX + CHOICE_BTN_W, btnY + CHOICE_BTN_H, bgColor);

                // 3D border
                graphics.fill(btnX, btnY, btnX + CHOICE_BTN_W, btnY + 1, 0xFFFFFFFF);
                graphics.fill(btnX, btnY, btnX + 1, btnY + CHOICE_BTN_H, 0xFFFFFFFF);
                graphics.fill(btnX, btnY + CHOICE_BTN_H - 1, btnX + CHOICE_BTN_W, btnY + CHOICE_BTN_H, 0xFF373737);
                graphics.fill(btnX + CHOICE_BTN_W - 1, btnY, btnX + CHOICE_BTN_W, btnY + CHOICE_BTN_H, 0xFF373737);

                // Attribute name
                String translationKey = "attribute." + choice.attributeId().replace(':', '.');
                Component attrName = Component.translatable(translationKey);

                // Value text with color
                String valueStr = formatValue(choice.value(), choice.operation());
                AttributeEntry entry = entryMap.get(choice.attributeId());
                boolean isBuff = determineBuff(choice, entry);
                int valueColor = isBuff ? 0xFF55FF55 : 0xFFFF5555;

                graphics.drawString(font, attrName, btnX + 4, btnY + 6, 0xFFFFFFFF, false);
                int valueW = font.width(valueStr);
                graphics.drawString(font, valueStr, btnX + CHOICE_BTN_W - valueW - 4, btnY + 6, valueColor, false);
            }
        }

        // ===== Reroll button =====
        int choiceCount = Math.max(choices.size(), menu.getClientChoices().isEmpty() ? 3 : choices.size());
        int rerollBtnX = x + CHOICE_BTN_X + CHOICE_BTN_W - REROLL_BTN_W;
        int rerollBtnY = y + CHOICE_START_Y + choiceCount * CHOICE_SPACING + 4;

        if (hasGun) {
            boolean rerollHovered = isMouseOver(mouseX, mouseY, rerollBtnX, rerollBtnY, REROLL_BTN_W, REROLL_BTN_H);
            int rerollBg = rerollHovered ? 0xFF555555 : 0xFF3A3A3A;
            graphics.fill(rerollBtnX, rerollBtnY, rerollBtnX + REROLL_BTN_W, rerollBtnY + REROLL_BTN_H, rerollBg);

            // 3D border
            graphics.fill(rerollBtnX, rerollBtnY, rerollBtnX + REROLL_BTN_W, rerollBtnY + 1, 0xFFFFFFFF);
            graphics.fill(rerollBtnX, rerollBtnY, rerollBtnX + 1, rerollBtnY + REROLL_BTN_H, 0xFFFFFFFF);
            graphics.fill(rerollBtnX, rerollBtnY + REROLL_BTN_H - 1, rerollBtnX + REROLL_BTN_W, rerollBtnY + REROLL_BTN_H, 0xFF373737);
            graphics.fill(rerollBtnX + REROLL_BTN_W - 1, rerollBtnY, rerollBtnX + REROLL_BTN_W, rerollBtnY + REROLL_BTN_H, 0xFF373737);

            Component rerollText = Component.translatable("gui.tacz_attributes_addon.enhancement.reroll");
            int rerollTextW = font.width(rerollText);
            graphics.drawString(font, rerollText, rerollBtnX + (REROLL_BTN_W - rerollTextW) / 2,
                    rerollBtnY + 4, 0xFFFFFFFF, false);
        }

        // ===== Enhancement count & cost info =====
        int infoY = y + CHOICE_START_Y + choiceCount * CHOICE_SPACING + 4;
        int infoX = x + CHOICE_BTN_X;

        if (hasGun) {
            // Enhancement count
            int enhanceCount = menu.getClientEnhanceCount();
            int maxEnhancements = menu.getClientMaxEnhancements();
            Component countText;
            if (maxEnhancements > 0) {
                countText = Component.translatable("gui.tacz_attributes_addon.enhancement.count",
                        enhanceCount, maxEnhancements);
            } else {
                countText = Component.translatable("gui.tacz_attributes_addon.enhancement.count_unlimited",
                        enhanceCount);
            }
            graphics.drawString(font, countText, infoX, infoY + 2, 0xFF404040, false);

            // Cost info
            int applyCost = menu.getClientApplyCost();
            int rerollCost = menu.getClientRerollCost();
            String costStr = "Apply: " + applyCost + "  Reroll: " + rerollCost;
            graphics.drawString(font, costStr, infoX, infoY + 14, 0xFF606060, false);

            // Max reached overlay
            if (maxReached) {
                Component maxText = Component.translatable("gui.tacz_attributes_addon.enhancement.max_reached");
                int maxTextW = font.width(maxText);
                graphics.drawString(font, maxText, x + CHOICE_BTN_X + (CHOICE_BTN_W - maxTextW) / 2,
                        y + CHOICE_START_Y - 2, 0xFFFF4444, false);
            }
        }

        // ===== Player inventory slots =====
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotBg(graphics, x + 7 + col * 18, y + 117 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawSlotBg(graphics, x + 7 + col * 18, y + 175);
        }
    }

    private boolean isMaxReached() {
        int max = menu.getClientMaxEnhancements();
        if (max <= 0) return false;
        return menu.getClientEnhanceCount() >= max;
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    private String formatValue(double value, int operationOrdinal) {
        AttributeModifier.Operation[] ops = AttributeModifier.Operation.values();
        AttributeModifier.Operation op = (operationOrdinal >= 0 && operationOrdinal < ops.length)
                ? ops[operationOrdinal] : AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        if (op == AttributeModifier.Operation.ADD_MULTIPLIED_BASE ||
                op == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
            return String.format("%+.0f%%", value * 100);
        } else {
            if (value == Math.floor(value)) {
                return String.format("%+.0f", value);
            } else {
                return String.format("%+.2f", value);
            }
        }
    }

    private boolean determineBuff(EnhancementChoicesPacket.ChoiceEntry choice, AttributeEntry entry) {
        if (entry != null) {
            if (entry.getScoreWeight() < 0) {
                return choice.value() < entry.getBuffThreshold();
            }
            return choice.value() > entry.getBuffThreshold();
        }
        return choice.value() > 0;
    }

    private void drawSlotBg(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 1, 0xFF373737);
        graphics.fill(x, y, x + 1, y + 18, 0xFF373737);
        graphics.fill(x + 1, y + 17, x + 18, y + 18, 0xFFFFFFFF);
        graphics.fill(x + 17, y + 1, x + 18, y + 18, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF8B8B8B);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int x = leftPos;
            int y = topPos;
            boolean hasGun = !menu.getSlot(0).getItem().isEmpty();

            if (hasGun) {
                List<EnhancementChoicesPacket.ChoiceEntry> choices = menu.getClientChoices();

                // Check choice buttons
                if (!isMaxReached()) {
                    for (int i = 0; i < choices.size(); i++) {
                        int btnX = x + CHOICE_BTN_X;
                        int btnY = y + CHOICE_START_Y + i * CHOICE_SPACING;
                        if (isMouseOver(mouseX, mouseY, btnX, btnY, CHOICE_BTN_W, CHOICE_BTN_H)) {
                            PacketDistributor.sendToServer(
                                    new EnhancementActionPacket(menu.getBlockPos(),
                                            EnhancementActionPacket.ACTION_SELECT, i));
                            return true;
                        }
                    }
                }

                // Check reroll button
                int choiceCount = Math.max(choices.size(), 3);
                int rerollBtnX = x + CHOICE_BTN_X + CHOICE_BTN_W - REROLL_BTN_W;
                int rerollBtnY = y + CHOICE_START_Y + choiceCount * CHOICE_SPACING + 4;
                if (isMouseOver(mouseX, mouseY, rerollBtnX, rerollBtnY, REROLL_BTN_W, REROLL_BTN_H)) {
                    PacketDistributor.sendToServer(
                            new EnhancementActionPacket(menu.getBlockPos(),
                                    EnhancementActionPacket.ACTION_REROLL, -1));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth - font.width(title)) / 2, -10, 0xFF404040, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);
    }
}
