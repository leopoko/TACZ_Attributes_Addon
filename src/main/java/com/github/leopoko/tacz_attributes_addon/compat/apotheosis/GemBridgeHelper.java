package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.github.leopoko.tacz_attributes_addon.bridge.AttributeBridge.AttributeModifierEntry;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketedGems;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Extracts attribute modifiers from Apotheosis socketed gems on a gun ItemStack.
 * Used by AttributeBridge via the GemModifierSupplier functional interface
 * to apply gem effects as transient player attribute modifiers.
 */
public class GemBridgeHelper {

    /**
     * Extract all attribute modifiers from socketed gems on a gun.
     *
     * @param gunStack the gun ItemStack to read gems from
     * @return list of (Attribute, AttributeModifier) entries ready for application
     */
    public static List<AttributeModifierEntry> extractGemModifiers(ItemStack gunStack) {
        SocketedGems gems = SocketHelper.getGems(gunStack);
        if (gems == null || gems.isEmpty()) return Collections.emptyList();

        List<AttributeModifierEntry> result = new ArrayList<>();
        for (GemInstance gem : gems) {
            if (!gem.isValid()) continue;

            gem.addModifiers(EquipmentSlot.MAINHAND, (attr, mod) ->
                    result.add(new AttributeModifierEntry(attr, mod)));
        }
        return result;
    }

    /**
     * Check if the gun has any valid socketed gems.
     */
    public static boolean hasGems(ItemStack gunStack) {
        SocketedGems gems = SocketHelper.getGems(gunStack);
        return gems != null && gems.streamValidGems().findAny().isPresent();
    }
}
