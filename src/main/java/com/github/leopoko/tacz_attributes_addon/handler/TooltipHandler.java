package com.github.leopoko.tacz_attributes_addon.handler;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.AttributeEntry;
import com.github.leopoko.tacz_attributes_addon.data.AttributeRegistry;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeOverrides;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.github.leopoko.tacz_attributes_addon.random.GunTypeFilter;
import com.tacz.guns.api.item.IGun;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adds attribute information to gun tooltips.
 */
@Mod.EventBusSubscriber(modid = TaczAttributesAddon.MODID, value = Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return;
        if (!GunAttributeData.hasAddonData(stack)) return;

        List<Component> tooltip = event.getToolTip();

        // Add separator
        tooltip.add(Component.empty());

        // Always show rarity info (compact summary)
        if (CommonConfig.ENABLE_RARITY_SCORING.get()) {
            Rarity rarity = GunAttributeData.getRarity(stack);
            int score = GunAttributeData.getScore(stack);
            ChatFormatting color = getRarityColor(rarity);
            tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.rarity",
                    Component.translatable("tooltip.tacz_attributes_addon.rarity." + rarity.name().toLowerCase())
                            .withStyle(color))
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(" (" + score + ")").withStyle(ChatFormatting.DARK_GRAY)));
        }

        // Show detailed modifiers only when Shift is held
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        // Add reroll count
        int rerollCount = GunAttributeData.getRerollCount(stack);
        if (rerollCount > 0) {
            int maxRerolls = CommonConfig.STATION_MAX_REROLLS.get();
            if (maxRerolls > 0) {
                tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.reroll_count",
                        rerollCount, maxRerolls).withStyle(ChatFormatting.DARK_GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.reroll_count_unlimited",
                        rerollCount).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        // Build attribute lookup
        Map<String, AttributeEntry> entryMap = AttributeRegistry.getEntries().stream()
                .collect(Collectors.toMap(AttributeEntry::getAttributeId, e -> e, (a, b) -> a));

        // Add random modifiers
        List<GunModifier> randomMods = GunAttributeData.getModifiers(stack);
        if (!randomMods.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.modifiers")
                    .withStyle(ChatFormatting.GOLD));
            for (GunModifier mod : randomMods) {
                addModifierTooltip(tooltip, mod, entryMap);
            }
        }

        // Show empty attribute slots if enabled
        if (CommonConfig.SHOW_EMPTY_SLOTS.get()) {
            int currentCount = randomMods.size();
            int maxAttributes = CommonConfig.MAX_ATTRIBUTES.get();

            // Check per-gun override
            ResourceLocation gunId = GunTypeFilter.resolveGunId(stack);
            if (gunId != null) {
                GunAttributeOverrides.GunOverride override = GunAttributeOverrides.getOverride(gunId.toString());
                if (override != null && override.hasMaxAttributes()) {
                    maxAttributes = override.getMaxAttributes();
                }
            }

            int emptySlots = maxAttributes - currentCount;
            for (int i = 0; i < emptySlots; i++) {
                tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.empty_slot")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        // Add fixed modifiers
        List<GunModifier> fixedMods = GunAttributeData.getFixedModifiers(stack);
        if (!fixedMods.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.fixed_modifiers")
                    .withStyle(ChatFormatting.BLUE));
            for (GunModifier mod : fixedMods) {
                addModifierTooltip(tooltip, mod, entryMap);
            }
        }

        // Add enhanced modifiers
        List<GunModifier> enhancedMods = GunAttributeData.getEnhancedModifiers(stack);
        if (!enhancedMods.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.enhanced_modifiers")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            for (GunModifier mod : enhancedMods) {
                addModifierTooltip(tooltip, mod, entryMap);
            }
        }

        // Add enhancement count
        int enhanceCount = GunAttributeData.getEnhanceCount(stack);
        if (enhanceCount > 0) {
            int maxEnhancements = CommonConfig.ENHANCEMENT_MAX_COUNT.get();
            if (maxEnhancements > 0) {
                tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.enhance_count",
                        enhanceCount, maxEnhancements).withStyle(ChatFormatting.DARK_GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.enhance_count_unlimited",
                        enhanceCount).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        // Note: Socket/gem info is NOT added here.
        // Apotheosis natively adds its own socket tooltip via its event handler.
    }

    private static void addModifierTooltip(List<Component> tooltip, GunModifier mod,
                                           Map<String, AttributeEntry> entryMap) {
        String attrId = mod.getAttributeId();
        // Convert attribute ID to translation key: tacz_attributes:gun_damage -> attribute.tacz_attributes.gun_damage
        String translationKey = "attribute." + attrId.replace(':', '.');
        double value = mod.getValue();

        // Determine if this is a buff
        AttributeEntry entry = entryMap.get(attrId);
        boolean isBuff;
        if (entry != null) {
            isBuff = entry.isValueBuff(value);
            // For recoil (negative scoreWeight), invert: negative value = buff
            if (entry.getScoreWeight() < 0) {
                isBuff = value < entry.getBuffThreshold();
            }
        } else {
            isBuff = value > 0;
        }

        ChatFormatting color = isBuff ? ChatFormatting.GREEN : ChatFormatting.RED;

        // Format value
        String valueStr;
        if (mod.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE ||
            mod.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
            valueStr = String.format("%+.0f%%", value * 100);
        } else {
            if (value == Math.floor(value)) {
                valueStr = String.format("%+.0f", value);
            } else {
                valueStr = String.format("%+.2f", value);
            }
        }

        MutableComponent line = Component.literal("  ")
                .append(Component.translatable(translationKey).withStyle(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(valueStr).withStyle(color));

        tooltip.add(line);
    }

    private static ChatFormatting getRarityColor(Rarity rarity) {
        switch (rarity) {
            case UNCOMMON: return ChatFormatting.YELLOW;
            case RARE: return ChatFormatting.AQUA;
            case EPIC: return ChatFormatting.LIGHT_PURPLE;
            default: return ChatFormatting.WHITE;
        }
    }
}
