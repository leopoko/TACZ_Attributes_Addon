package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.mojang.logging.LogUtils;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.modifiers.EntitySlotGroup;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Registers a custom LootCategory for TACZ guns so Apotheosis
 * recognizes them as socketable items.
 *
 * In Apotheosis 1.21.1, the API changed:
 * - Constructor is public: LootCategory(Predicate, EntitySlotGroup, int)
 * - Registration into sortedCategories via reflection
 */
public class GunLootCategory {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static LootCategory GUN;

    @SuppressWarnings("unchecked")
    public static void register() {
        try {
            // Create EntitySlotGroup for mainhand
            EntitySlotGroup mainhandGroup = new EntitySlotGroup(
                    ResourceLocation.fromNamespaceAndPath("tacz_attributes_addon", "gun_mainhand"),
                    HolderSet.empty()
            );

            // Create the GUN LootCategory with low priority
            GUN = new LootCategory(
                    (Predicate<ItemStack>) stack -> IGun.getIGunOrNull(stack) != null,
                    mainhandGroup,
                    100 // priority
            );

            // Register into sortedCategories via reflection
            try {
                Field sortedField = LootCategory.class.getDeclaredField("sortedCategories");
                sortedField.setAccessible(true);
                List<LootCategory> sorted = (List<LootCategory>) sortedField.get(null);
                if (sorted != null) {
                    List<LootCategory> mutable = new ArrayList<>(sorted);
                    mutable.add(GUN);
                    sortedField.set(null, mutable);
                }
            } catch (Exception e) {
                LOGGER.warn("Could not register GUN into sortedCategories", e);
            }

            LOGGER.info("Registered GUN LootCategory for Apotheosis socket integration");
        } catch (Exception e) {
            LOGGER.error("Failed to register GUN LootCategory", e);
            GUN = null;
        }
    }

    public static boolean isRegistered() {
        return GUN != null;
    }
}
