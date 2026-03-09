package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.mojang.logging.LogUtils;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Registers a custom LootCategory for TACZ guns so Apotheosis
 * recognizes them as socketable items.
 */
public class GunLootCategory {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static LootCategory GUN;

    @SuppressWarnings("unchecked")
    public static void register() {
        try {
            // LootCategory constructor is private; use reflection to create a new instance
            Constructor<LootCategory> ctor = LootCategory.class.getDeclaredConstructor(
                    String.class, Predicate.class, EquipmentSlot[].class);
            ctor.setAccessible(true);

            GUN = ctor.newInstance(
                    "gun",
                    (Predicate<ItemStack>) stack -> IGun.getIGunOrNull(stack) != null,
                    new EquipmentSlot[]{EquipmentSlot.MAINHAND}
            );

            // Register into internal maps so LootCategory.forItem() and BY_ID find our category
            Field byIdField = LootCategory.class.getDeclaredField("BY_ID_INTERNAL");
            byIdField.setAccessible(true);
            Map<String, LootCategory> byId = (Map<String, LootCategory>) byIdField.get(null);
            byId.put("gun", GUN);

            Field valuesField = LootCategory.class.getDeclaredField("VALUES_INTERNAL");
            valuesField.setAccessible(true);
            List<LootCategory> values = (List<LootCategory>) valuesField.get(null);
            values.add(GUN);

            LOGGER.info("Registered GUN LootCategory for Apotheosis socket integration");
        } catch (Exception e) {
            LOGGER.error("Failed to register GUN LootCategory via reflection", e);
            GUN = null;
        }
    }

    public static boolean isRegistered() {
        return GUN != null;
    }
}
