package com.github.leopoko.tacz_attributes_addon.data;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;

/**
 * Static helper for reading/writing addon attribute data from/to gun ItemStack NBT.
 * In 1.21.1, data is stored via DataComponents.CUSTOM_DATA wrapping CompoundTag.
 *
 * NBT structure (inside CUSTOM_DATA):
 * TaczAddon: {
 *   Modifiers: [{Attr:"...", Val:0.15, Op:1}, ...],
 *   FixedModifiers: [{Attr:"...", Val:0.1, Op:1}, ...],
 *   Score: 42,
 *   Rarity: 0,
 *   Sealed: 1
 * }
 */
public class GunAttributeData {
    public static final String ROOT_TAG = "TaczAddon";
    public static final String MODIFIERS_TAG = "Modifiers";
    public static final String FIXED_MODIFIERS_TAG = "FixedModifiers";
    public static final String SCORE_TAG = "Score";
    public static final String RARITY_TAG = "Rarity";
    public static final String SEALED_TAG = "Sealed";
    public static final String REROLL_COUNT_TAG = "RerollCount";
    public static final String ENHANCED_MODIFIERS_TAG = "EnhancedModifiers";
    public static final String ENHANCE_COUNT_TAG = "EnhanceCount";
    public static final String PRESET_TAG = "TaczPreset";
    public static final String PRESET_MIN_RARITY_TAG = "MinRarity";
    public static final String PRESET_TARGET_RARITY_TAG = "TargetRarity";

    public static boolean hasAddonData(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.contains(ROOT_TAG, Tag.TAG_COMPOUND);
    }

    public static boolean isSealed(ItemStack stack) {
        CompoundTag root = getRoot(stack);
        return root.getBoolean(SEALED_TAG);
    }

    public static void setSealed(ItemStack stack, boolean sealed) {
        updateRoot(stack, root -> root.putBoolean(SEALED_TAG, sealed));
    }

    public static List<GunModifier> getModifiers(ItemStack stack) {
        return readModifierList(stack, MODIFIERS_TAG);
    }

    public static void setModifiers(ItemStack stack, List<GunModifier> modifiers) {
        writeModifierList(stack, MODIFIERS_TAG, modifiers);
    }

    public static List<GunModifier> getFixedModifiers(ItemStack stack) {
        return readModifierList(stack, FIXED_MODIFIERS_TAG);
    }

    public static void setFixedModifiers(ItemStack stack, List<GunModifier> modifiers) {
        writeModifierList(stack, FIXED_MODIFIERS_TAG, modifiers);
    }

    public static List<GunModifier> getEnhancedModifiers(ItemStack stack) {
        return readModifierList(stack, ENHANCED_MODIFIERS_TAG);
    }

    public static void setEnhancedModifiers(ItemStack stack, List<GunModifier> modifiers) {
        writeModifierList(stack, ENHANCED_MODIFIERS_TAG, modifiers);
    }

    public static int getEnhanceCount(ItemStack stack) {
        return getRoot(stack).getInt(ENHANCE_COUNT_TAG);
    }

    public static void setEnhanceCount(ItemStack stack, int count) {
        updateRoot(stack, root -> root.putInt(ENHANCE_COUNT_TAG, count));
    }

    public static void incrementEnhanceCount(ItemStack stack) {
        setEnhanceCount(stack, getEnhanceCount(stack) + 1);
    }

    public static List<GunModifier> getAllModifiers(ItemStack stack) {
        List<GunModifier> all = new ArrayList<>();
        all.addAll(getModifiers(stack));
        all.addAll(getFixedModifiers(stack));
        all.addAll(getEnhancedModifiers(stack));
        return all;
    }

    public static int getScore(ItemStack stack) {
        return getRoot(stack).getInt(SCORE_TAG);
    }

    public static void setScore(ItemStack stack, int score) {
        updateRoot(stack, root -> root.putInt(SCORE_TAG, score));
    }

    public static int getRarityOrdinal(ItemStack stack) {
        return getRoot(stack).getInt(RARITY_TAG);
    }

    public static void setRarityOrdinal(ItemStack stack, int rarityOrdinal) {
        updateRoot(stack, root -> root.putInt(RARITY_TAG, rarityOrdinal));
    }

    // ========== Reroll Count ==========

    public static int getRerollCount(ItemStack stack) {
        return getRoot(stack).getInt(REROLL_COUNT_TAG);
    }

    public static void setRerollCount(ItemStack stack, int count) {
        updateRoot(stack, root -> root.putInt(REROLL_COUNT_TAG, count));
    }

    public static void incrementRerollCount(ItemStack stack) {
        setRerollCount(stack, getRerollCount(stack) + 1);
    }

    // ========== Remove all addon data ==========

    /**
     * Remove the entire TaczAddon NBT compound from the item, fully resetting it.
     */
    public static void removeAllAddonData(ItemStack stack) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data ->
                data.update(tag -> tag.remove(ROOT_TAG)));
    }

    // ========== Preset (rarity pre-specification via NBT) ==========

    /**
     * Check if the item has a TaczPreset tag for rarity pre-specification.
     */
    public static boolean hasPreset(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.contains(PRESET_TAG, Tag.TAG_COMPOUND);
    }

    /**
     * Get the minimum rarity preset value.
     * @return rarity ordinal (0-3), or -1 if not set
     */
    public static int getPresetMinRarity(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (!tag.contains(PRESET_TAG, Tag.TAG_COMPOUND)) return -1;
        CompoundTag preset = tag.getCompound(PRESET_TAG);
        return preset.contains(PRESET_MIN_RARITY_TAG, Tag.TAG_ANY_NUMERIC)
                ? preset.getInt(PRESET_MIN_RARITY_TAG) : -1;
    }

    /**
     * Get the exact target rarity preset value.
     * @return rarity ordinal (0-3), or -1 if not set
     */
    public static int getPresetTargetRarity(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (!tag.contains(PRESET_TAG, Tag.TAG_COMPOUND)) return -1;
        CompoundTag preset = tag.getCompound(PRESET_TAG);
        return preset.contains(PRESET_TARGET_RARITY_TAG, Tag.TAG_ANY_NUMERIC)
                ? preset.getInt(PRESET_TARGET_RARITY_TAG) : -1;
    }

    /**
     * Remove the TaczPreset tag after generation is complete.
     */
    public static void removePreset(ItemStack stack) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data ->
                data.update(tag -> tag.remove(PRESET_TAG)));
    }

    // ========== Rarity ==========

    public static Rarity getRarity(ItemStack stack) {
        if (!hasAddonData(stack)) return Rarity.COMMON;
        int ordinal = getRarityOrdinal(stack);
        Rarity[] values = Rarity.values();
        if (ordinal >= 0 && ordinal < values.length) {
            return values[ordinal];
        }
        return Rarity.COMMON;
    }

    /**
     * Read the root TaczAddon compound (read-only snapshot).
     */
    private static CompoundTag getRoot(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (!tag.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
            return new CompoundTag();
        }
        return tag.getCompound(ROOT_TAG);
    }

    /**
     * Modify the root TaczAddon compound via DataComponents.CUSTOM_DATA update.
     */
    private static void updateRoot(ItemStack stack, java.util.function.Consumer<CompoundTag> modifier) {
        stack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data ->
                data.update(tag -> {
                    if (!tag.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
                        tag.put(ROOT_TAG, new CompoundTag());
                    }
                    modifier.accept(tag.getCompound(ROOT_TAG));
                }));
    }

    private static List<GunModifier> readModifierList(ItemStack stack, String key) {
        List<GunModifier> result = new ArrayList<>();
        CompoundTag root = getRoot(stack);
        if (!root.contains(key, Tag.TAG_LIST)) return result;
        ListTag list = root.getList(key, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            result.add(GunModifier.fromNbt(list.getCompound(i)));
        }
        return result;
    }

    private static void writeModifierList(ItemStack stack, String key, List<GunModifier> modifiers) {
        updateRoot(stack, root -> {
            ListTag list = new ListTag();
            for (GunModifier mod : modifiers) {
                list.add(mod.toNbt());
            }
            root.put(key, list);
        });
    }
}
