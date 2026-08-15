package com.github.leopoko.tacz_attributes_addon.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Represents a material type that can be used in the Attribute Station.
 * Each material can have rarity constraints that affect the generated attributes.
 */
public class StationMaterial {
    private final String itemId;
    private final int count;
    private final int targetRarity;  // -1 = not set
    private final int minRarity;     // -1 = not set
    private final int maxRarity;     // -1 = not set

    public StationMaterial(String itemId, int count, int targetRarity, int minRarity, int maxRarity) {
        this.itemId = itemId;
        this.count = count;
        this.targetRarity = targetRarity;
        this.minRarity = minRarity;
        this.maxRarity = maxRarity;
    }

    public String getItemId() { return itemId; }
    public int getCount() { return count; }
    public int getTargetRarity() { return targetRarity; }
    public int getMinRarity() { return minRarity; }
    public int getMaxRarity() { return maxRarity; }

    public boolean hasRarityConstraint() {
        return targetRarity >= 0 || minRarity >= 0 || maxRarity >= 0;
    }

    /**
     * Check if the given ItemStack matches this material (item ID and sufficient count).
     */
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (stackId == null) return false;
        ResourceLocation required = ResourceLocation.parse(itemId);
        return required.equals(stackId) && stack.getCount() >= count;
    }
}
