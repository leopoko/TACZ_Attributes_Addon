package com.github.leopoko.tacz_attributes_addon.network;

import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Server -> Client: sync enhancement choices for the Enhancement Station GUI.
 */
public class EnhancementChoicesPacket {

    public record ChoiceEntry(String attributeId, double value, int operation) {}

    private final BlockPos pos;
    private final List<ChoiceEntry> choices;
    private final int enhanceCount;
    private final int maxEnhancements;
    private final int applyCost;
    private final int rerollCost;

    public EnhancementChoicesPacket(BlockPos pos, List<ChoiceEntry> choices,
                                    int enhanceCount, int maxEnhancements,
                                    int applyCost, int rerollCost) {
        this.pos = pos;
        this.choices = choices;
        this.enhanceCount = enhanceCount;
        this.maxEnhancements = maxEnhancements;
        this.applyCost = applyCost;
        this.rerollCost = rerollCost;
    }

    public static void encode(EnhancementChoicesPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.choices.size());
        for (ChoiceEntry entry : msg.choices) {
            buf.writeUtf(entry.attributeId());
            buf.writeDouble(entry.value());
            buf.writeInt(entry.operation());
        }
        buf.writeInt(msg.enhanceCount);
        buf.writeInt(msg.maxEnhancements);
        buf.writeInt(msg.applyCost);
        buf.writeInt(msg.rerollCost);
    }

    public static EnhancementChoicesPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readInt();
        List<ChoiceEntry> choices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String attrId = buf.readUtf();
            double value = buf.readDouble();
            int op = buf.readInt();
            choices.add(new ChoiceEntry(attrId, value, op));
        }
        int enhanceCount = buf.readInt();
        int maxEnhancements = buf.readInt();
        int applyCost = buf.readInt();
        int rerollCost = buf.readInt();
        return new EnhancementChoicesPacket(pos, choices, enhanceCount, maxEnhancements, applyCost, rerollCost);
    }

    public static void handle(EnhancementChoicesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            if (!(player.containerMenu instanceof EnhancementStationMenu menu)) return;

            menu.setClientChoices(msg.choices);
            menu.setClientEnhanceCount(msg.enhanceCount);
            menu.setClientMaxEnhancements(msg.maxEnhancements);
            menu.setClientApplyCost(msg.applyCost);
            menu.setClientRerollCost(msg.rerollCost);
        });
        ctx.get().setPacketHandled(true);
    }
}
