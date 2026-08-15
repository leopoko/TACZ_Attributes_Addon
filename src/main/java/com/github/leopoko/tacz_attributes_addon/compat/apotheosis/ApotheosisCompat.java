package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.github.leopoko.tacz_attributes_addon.bridge.AttributeBridge;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

/**
 * Features 4 & 5: Apotheosis integration for gem/socket system on guns.
 *
 * Registers a GUN LootCategory, handles socket count events, and wires
 * gem modifier extraction into AttributeBridge. All Apotheosis-dependent
 * classes are loaded lazily to avoid ClassNotFoundException when Apotheosis
 * is not installed.
 */
public class ApotheosisCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean apotheosisPresent = false;

    public static void init() {
        if (!CommonConfig.ENABLE_APOTHEOSIS.get()) {
            LOGGER.info("Apotheosis integration disabled in config");
            return;
        }

        apotheosisPresent = ModList.get().isLoaded("apotheosis");
        if (!apotheosisPresent) {
            LOGGER.info("Apotheosis not found, skipping integration");
            return;
        }

        LOGGER.info("Apotheosis detected, initializing gun socket/gem integration");
        initApotheosis();
    }

    private static void initApotheosis() {
        try {
            // Guard: only execute if Apotheosis classes are actually available
            Class.forName("dev.shadowsoffire.apotheosis.Apoth");

            // 1. Register GUN LootCategory
            GunLootCategory.register();
            if (!GunLootCategory.isRegistered()) {
                LOGGER.warn("Failed to register GUN LootCategory, Apotheosis integration partially disabled");
            }

            // 2. Wire gem modifier extraction into AttributeBridge
            AttributeBridge.setGemModifierSupplier(GemBridgeHelper::extractGemModifiers);
            LOGGER.info("Wired GemBridgeHelper into AttributeBridge");

            LOGGER.info("Apotheosis integration fully initialized");
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Apotheosis classes not found despite mod being loaded");
            apotheosisPresent = false;
        }
    }

    public static boolean isApotheosisPresent() {
        return apotheosisPresent;
    }

    /**
     * Sets initial socket count on a gun based on its rarity.
     * Uses Apotheosis's native SocketHelper so Sigil of Socketing works naturally.
     * Safe to call even when Apotheosis is not loaded (no-op).
     */
    public static void applyInitialSockets(ItemStack stack) {
        if (!apotheosisPresent) return;
        try {
            GunSocketHandler.applyInitialSockets(stack);
        } catch (NoClassDefFoundError ignored) {
            // Apotheosis classes not available
        }
    }

    /**
     * Migration: sets initial sockets only if the gun doesn't already have them.
     * Used for existing guns created before sockets were stored in Data Components.
     */
    public static void applyInitialSocketsIfMissing(ItemStack stack) {
        if (!apotheosisPresent) return;
        try {
            if (!GunSocketHandler.hasSockets(stack)) {
                GunSocketHandler.applyInitialSockets(stack);
            }
        } catch (NoClassDefFoundError ignored) {
            // Apotheosis classes not available
        }
    }
}
