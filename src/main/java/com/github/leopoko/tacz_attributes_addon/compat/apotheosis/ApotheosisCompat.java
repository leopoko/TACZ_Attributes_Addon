package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

/**
 * Features 4 & 5: Apotheosis integration for gem/socket system on guns.
 *
 * This is a stub implementation. Full integration requires:
 * 1. Registering a custom LootCategory for TACZ guns
 * 2. Creating gun-specific Gem implementations
 * 3. Hooking into Apotheosis' socket system to make guns socketable
 *
 * Implementation depends on Apotheosis API which is loaded as compileOnly.
 * All Apotheosis-dependent code is guarded by isLoaded() check.
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
        // TODO: Implement full Apotheosis integration
        // 1. Register GUN LootCategory
        // 2. Hook into LootCategory.forItem() for TACZ gun items
        // 3. Register gun-specific gems as datapack entries
        // 4. Add socket slots to guns based on config
        initApotheosis();
    }

    private static void initApotheosis() {
        try {
            // Guard: only execute if Apotheosis classes are actually available
            Class.forName("dev.shadowsoffire.apotheosis.Apotheosis");
            LOGGER.info("Apotheosis integration initialized (stub - full implementation pending)");

            // Future implementation:
            // GunLootCategory.register();
            // GunGems.registerGems();
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Apotheosis classes not found despite mod being loaded");
            apotheosisPresent = false;
        }
    }

    public static boolean isApotheosisPresent() {
        return apotheosisPresent;
    }
}
