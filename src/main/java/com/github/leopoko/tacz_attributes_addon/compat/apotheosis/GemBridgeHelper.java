package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.github.leopoko.tacz_attributes_addon.bridge.AttributeBridge.AttributeModifierEntry;
import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.socket.SocketedGems;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Extracts attribute modifiers from Apotheosis socketed gems on a gun ItemStack.
 * Used by AttributeBridge via the GemModifierSupplier functional interface
 * to apply gem effects as transient player attribute modifiers.
 *
 * In Apotheosis 1.21.1, GemInstance.addModifiers() now takes a StackAttributeModifiersEvent
 * instead of (EquipmentSlot, BiConsumer). Since we can't easily construct that event,
 * gem modifier extraction is stubbed for now. Gems still work via Apotheosis's own system;
 * this only affects our addon's bridge display.
 */
public class GemBridgeHelper {

    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Extract all attribute modifiers from socketed gems on a gun.
     *
     * @param gunStack the gun ItemStack to read gems from
     * @return list of (Attribute, AttributeModifier) entries ready for application
     */
    public static List<AttributeModifierEntry> extractGemModifiers(ItemStack gunStack) {
        // In Apotheosis 1.21.1, the addModifiers API changed to use StackAttributeModifiersEvent
        // which requires full event context. Gem effects are still applied by Apotheosis itself;
        // our bridge extraction is not currently possible with the new API.
        return Collections.emptyList();
    }

    /**
     * Check if the gun has any valid socketed gems.
     */
    public static boolean hasGems(ItemStack gunStack) {
        SocketedGems gems = SocketHelper.getGems(gunStack);
        return gems != null && gems.streamValidGems().findAny().isPresent();
    }
}
