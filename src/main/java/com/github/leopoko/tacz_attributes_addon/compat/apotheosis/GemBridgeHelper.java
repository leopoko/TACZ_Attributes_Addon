package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.github.leopoko.tacz_attributes_addon.bridge.AttributeBridge.AttributeModifierEntry;
import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.socket.SocketedGems;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apothic_attributes.modifiers.StackAttributeModifiers;
import dev.shadowsoffire.apothic_attributes.modifiers.StackAttributeModifiersEvent;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Extracts attribute modifiers from Apotheosis socketed gems on a gun ItemStack.
 * Used by AttributeBridge via the GemModifierSupplier functional interface
 * to apply gem effects as transient player attribute modifiers.
 *
 * In Apotheosis 1.21.1, GemInstance.addModifiers() takes a StackAttributeModifiersEvent
 * instead of (EquipmentSlot, BiConsumer). We construct the event to extract modifiers.
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
        try {
            SocketedGems gems = SocketHelper.getGems(gunStack);
            if (gems == null) return Collections.emptyList();

            // Create a StackAttributeModifiersEvent to collect gem modifiers
            StackAttributeModifiersEvent event = new StackAttributeModifiersEvent(
                    gunStack, StackAttributeModifiers.EMPTY);

            // Let each valid gem add its modifiers to the event
            gems.streamValidGems().forEach(gem -> {
                try {
                    gem.addModifiers(event);
                } catch (Exception e) {
                    // Skip gems that fail to add modifiers
                }
            });

            // Extract the modifiers added by gems
            List<AttributeModifierEntry> result = new ArrayList<>();
            for (StackAttributeModifiers.Entry entry : event.getModifiers()) {
                result.add(new AttributeModifierEntry(entry.attribute(), entry.modifier()));
            }

            return result;
        } catch (Exception e) {
            LOGGER.debug("Failed to extract gem modifiers: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Check if the gun has any valid socketed gems.
     */
    public static boolean hasGems(ItemStack gunStack) {
        SocketedGems gems = SocketHelper.getGems(gunStack);
        return gems != null && gems.streamValidGems().findAny().isPresent();
    }
}
