package com.github.leopoko.tacz_attributes_addon.network;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Server -> Client: sync enhancement choices for the Enhancement Station GUI.
 */
public record EnhancementChoicesPacket(
        BlockPos pos,
        List<ChoiceEntry> choices,
        int enhanceCount,
        int maxEnhancements,
        int applyCost,
        int rerollCost
) implements CustomPacketPayload {

    public record ChoiceEntry(String attributeId, double value, int operation) {}

    public static final CustomPacketPayload.Type<EnhancementChoicesPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczAttributesAddon.MODID, "enhancement_choices"));

    public static final StreamCodec<ByteBuf, EnhancementChoicesPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public EnhancementChoicesPacket decode(ByteBuf buf) {
            BlockPos pos = BlockPos.of(buf.readLong());
            int size = buf.readInt();
            List<ChoiceEntry> choices = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String attrId = ByteBufCodecs.STRING_UTF8.decode(buf);
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

        @Override
        public void encode(ByteBuf buf, EnhancementChoicesPacket msg) {
            buf.writeLong(msg.pos.asLong());
            buf.writeInt(msg.choices.size());
            for (ChoiceEntry entry : msg.choices) {
                ByteBufCodecs.STRING_UTF8.encode(buf, entry.attributeId());
                buf.writeDouble(entry.value());
                buf.writeInt(entry.operation());
            }
            buf.writeInt(msg.enhanceCount);
            buf.writeInt(msg.maxEnhancements);
            buf.writeInt(msg.applyCost);
            buf.writeInt(msg.rerollCost);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EnhancementChoicesPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            if (!(player.containerMenu instanceof EnhancementStationMenu menu)) return;

            menu.setClientChoices(msg.choices);
            menu.setClientEnhanceCount(msg.enhanceCount);
            menu.setClientMaxEnhancements(msg.maxEnhancements);
            menu.setClientApplyCost(msg.applyCost);
            menu.setClientRerollCost(msg.rerollCost);
        });
    }
}
