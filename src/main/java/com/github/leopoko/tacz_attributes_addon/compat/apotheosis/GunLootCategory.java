package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.mojang.logging.LogUtils;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
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
 * In Apotheosis 1.21.1:
 * - LootCategory is a NeoForge Registry (Apoth.BuiltInRegs.LOOT_CATEGORY)
 * - Constructor: LootCategory(Predicate, EntitySlotGroup, int)
 * - Must register into the actual registry for gem/affix JSON resolution
 * - Must also be in sortedCategories for runtime item matching
 */
public class GunLootCategory {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static LootCategory GUN;

    @SuppressWarnings("unchecked")
    public static void register() {
        try {
            // Create the GUN LootCategory with low priority (100)
            GUN = new LootCategory(
                    (Predicate<ItemStack>) stack -> IGun.getIGunOrNull(stack) != null,
                    ALObjects.EquipmentSlotGroups.MAINHAND,
                    100 // priority (lower = checked later, after standard categories)
            );

            ResourceLocation gunId = ResourceLocation.fromNamespaceAndPath("tacz_attributes_addon", "gun");

            // 1. Register into the actual Apotheosis LootCategory registry
            //    This is required for gem/affix JSON to resolve "tacz_attributes_addon:gun"
            try {
                registerIntoRegistry(gunId);
                LOGGER.info("Registered GUN into Apotheosis LootCategory registry");
            } catch (Exception e) {
                LOGGER.warn("Could not register GUN into LootCategory registry: {}", e.getMessage());
            }

            // 2. Register into sortedCategories for runtime item matching
            //    LootCategory.forItem() iterates sortedCategories, so we need to be there too
            try {
                registerIntoSortedCategories();
            } catch (Exception e) {
                LOGGER.warn("Could not register GUN into sortedCategories", e);
            }

            LOGGER.info("Registered GUN LootCategory for Apotheosis socket integration");
        } catch (Exception e) {
            LOGGER.error("Failed to register GUN LootCategory", e);
            GUN = null;
        }
    }

    @SuppressWarnings("unchecked")
    private static void registerIntoRegistry(ResourceLocation id) throws Exception {
        // Access Apoth.BuiltInRegs.LOOT_CATEGORY
        Class<?> builtInRegsClass = Class.forName("dev.shadowsoffire.apotheosis.Apoth$BuiltInRegs");
        Field regField = builtInRegsClass.getDeclaredField("LOOT_CATEGORY");
        regField.setAccessible(true);
        Registry<LootCategory> registry = (Registry<LootCategory>) regField.get(null);

        if (registry == null) {
            throw new IllegalStateException("LOOT_CATEGORY registry is null");
        }

        // Unfreeze the registry if it's a MappedRegistry (it will be frozen post-init)
        boolean wasFrozen = false;
        Field frozenField = null;

        if (registry instanceof MappedRegistry<?>) {
            try {
                frozenField = MappedRegistry.class.getDeclaredField("frozen");
                frozenField.setAccessible(true);
                wasFrozen = frozenField.getBoolean(registry);
                if (wasFrozen) {
                    frozenField.set(registry, false);
                }
            } catch (NoSuchFieldException e) {
                LOGGER.debug("Could not find 'frozen' field on MappedRegistry");
            }
        }

        try {
            ResourceKey<LootCategory> key = ResourceKey.create(registry.key(), id);
            Registry.register(registry, key, GUN);
        } finally {
            // Refreeze the registry
            if (wasFrozen && frozenField != null) {
                try {
                    frozenField.set(registry, true);
                } catch (Exception e) {
                    LOGGER.warn("Could not refreeze LootCategory registry", e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void registerIntoSortedCategories() throws Exception {
        Field sortedField = LootCategory.class.getDeclaredField("sortedCategories");
        sortedField.setAccessible(true);
        List<LootCategory> sorted = (List<LootCategory>) sortedField.get(null);
        if (sorted != null) {
            List<LootCategory> mutable = new ArrayList<>(sorted);
            mutable.add(GUN);
            // Re-sort by priority (descending, matching Apotheosis behavior)
            mutable.sort((a, b) -> Integer.compare(b.priority(), a.priority()));
            sortedField.set(null, mutable);
        }
    }

    public static boolean isRegistered() {
        return GUN != null;
    }
}
