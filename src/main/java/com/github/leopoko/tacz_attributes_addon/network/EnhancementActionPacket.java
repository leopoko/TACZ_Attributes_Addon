package com.github.leopoko.tacz_attributes_addon.network;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> Server: player action in the Enhancement Station (reroll or select choice).
 */
public record EnhancementActionPacket(
        BlockPos pos,
        int actionType,
        int choiceIndex
) implements CustomPacketPayload {

    public static final int ACTION_REROLL = 0;
    public static final int ACTION_SELECT = 1;

    public static final CustomPacketPayload.Type<EnhancementActionPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TaczAttributesAddon.MODID, "enhancement_action"));

    public static final StreamCodec<ByteBuf, EnhancementActionPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public EnhancementActionPacket decode(ByteBuf buf) {
            BlockPos pos = BlockPos.of(buf.readLong());
            int actionType = buf.readInt();
            int choiceIndex = buf.readInt();
            return new EnhancementActionPacket(pos, actionType, choiceIndex);
        }

        @Override
        public void encode(ByteBuf buf, EnhancementActionPacket msg) {
            buf.writeLong(msg.pos.asLong());
            buf.writeInt(msg.actionType);
            buf.writeInt(msg.choiceIndex);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EnhancementActionPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            // Validate player is close enough to the block
            if (player.distanceToSqr(msg.pos.getX() + 0.5, msg.pos.getY() + 0.5, msg.pos.getZ() + 0.5) > 64.0) {
                return;
            }

            BlockEntity be = player.level().getBlockEntity(msg.pos);
            if (!(be instanceof EnhancementStationBlockEntity station)) return;

            if (msg.actionType == ACTION_REROLL) {
                station.rerollChoices(player);
            } else if (msg.actionType == ACTION_SELECT) {
                station.applyEnhancement(msg.choiceIndex, player);
            }
        });
    }
}
