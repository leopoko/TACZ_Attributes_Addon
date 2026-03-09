package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketedGems;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Displays gem/socket information for the /taczaddon info command.
 * This class imports Apotheosis types and must only be loaded when Apotheosis is present.
 * Called via reflection from ModCommands.
 */
public class GemInfoHelper {

    public static void showInfo(CommandSourceStack source, ItemStack held) {
        int sockets = SocketHelper.getSockets(held);
        if (sockets <= 0) return;

        SocketedGems gems = SocketHelper.getGems(held);
        long filledCount = gems.streamValidGems().count();

        source.sendSuccess(() -> Component.literal("Sockets: " + filledCount + "/" + sockets)
                .withStyle(ChatFormatting.DARK_PURPLE), false);

        for (int i = 0; i < gems.size(); i++) {
            GemInstance gem = gems.get(i);
            if (gem.isValid()) {
                Component name = gem.getSocketBonusTooltip();
                source.sendSuccess(() -> Component.literal("  ").append(name), false);
            } else {
                source.sendSuccess(() -> Component.literal("  [Empty]")
                        .withStyle(ChatFormatting.DARK_GRAY), false);
            }
        }
    }
}
