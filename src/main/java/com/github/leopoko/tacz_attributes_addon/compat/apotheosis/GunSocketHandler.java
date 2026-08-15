package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import dev.shadowsoffire.apotheosis.socket.SocketHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Handles Apotheosis socket initialization for TACZ guns.
 * Socket count is determined once at obtain time and stored via Apotheosis's Data Components.
 * After that, Apotheosis manages sockets natively — including Sigil of Socketing for expansion.
 */
public class GunSocketHandler {

    /**
     * Sets initial sockets on a gun based on its rarity.
     * Called once when the gun first receives addon data.
     * Uses Apotheosis's own SocketHelper.setSockets() so the Sigil of Socketing
     * can naturally add more sockets later.
     */
    public static void applyInitialSockets(ItemStack stack) {
        int sockets;
        if (CommonConfig.SOCKETS_SCALE_WITH_RARITY.get()) {
            Rarity rarity = GunAttributeData.getRarity(stack);
            sockets = getSocketsForRarity(rarity);
        } else {
            sockets = CommonConfig.GUN_BASE_SOCKETS.get();
        }

        SocketHelper.setSockets(stack, sockets);
    }

    /**
     * Checks whether the gun already has Apotheosis socket data set.
     */
    public static boolean hasSockets(ItemStack stack) {
        return SocketHelper.getSockets(stack) > 0;
    }

    private static int getSocketsForRarity(Rarity rarity) {
        return switch (rarity) {
            case EPIC -> CommonConfig.EPIC_SOCKETS.get();
            case RARE -> CommonConfig.RARE_SOCKETS.get();
            case UNCOMMON -> CommonConfig.UNCOMMON_SOCKETS.get();
            default -> CommonConfig.COMMON_SOCKETS.get();
        };
    }
}
