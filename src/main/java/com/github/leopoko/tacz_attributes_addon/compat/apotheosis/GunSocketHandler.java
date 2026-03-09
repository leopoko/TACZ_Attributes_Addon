package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.adventure.event.GetItemSocketsEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles Apotheosis GetItemSocketsEvent to provide socket counts for TACZ guns.
 * Registered manually via MinecraftForge.EVENT_BUS (not @Mod.EventBusSubscriber)
 * to avoid class loading issues when Apotheosis is absent.
 */
public class GunSocketHandler {

    @SubscribeEvent
    public static void onGetItemSockets(GetItemSocketsEvent event) {
        ItemStack stack = event.getStack();
        if (IGun.getIGunOrNull(stack) == null) return;

        int sockets;
        if (CommonConfig.SOCKETS_SCALE_WITH_RARITY.get()) {
            Rarity rarity = GunAttributeData.getRarity(stack);
            sockets = getSocketsForRarity(rarity);
        } else {
            sockets = CommonConfig.GUN_BASE_SOCKETS.get();
        }

        event.setSockets(sockets);
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
