package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketedGems;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Adds socket/gem information to gun tooltips (client-side only).
 * This class imports Apotheosis types and must only be loaded when Apotheosis is present.
 */
public class GemTooltipHelper {

    public static void addGemTooltips(ItemStack stack, List<Component> tooltip) {
        int sockets = SocketHelper.getSockets(stack);
        if (sockets <= 0) return;

        SocketedGems gems = SocketHelper.getGems(stack);
        long filledCount = gems.streamValidGems().count();

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.sockets",
                        (int) filledCount, sockets)
                .withStyle(ChatFormatting.DARK_PURPLE));

        for (GemInstance gem : gems) {
            if (gem.isValid()) {
                tooltip.add(Component.literal("  ").append(gem.getSocketBonusTooltip()));
            } else {
                tooltip.add(Component.translatable("tooltip.tacz_attributes_addon.empty_socket")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}
