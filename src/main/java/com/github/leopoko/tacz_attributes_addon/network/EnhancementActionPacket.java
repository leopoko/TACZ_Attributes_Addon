package com.github.leopoko.tacz_attributes_addon.network;

import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Client -> Server: player action in the Enhancement Station (reroll or select choice).
 */
public class EnhancementActionPacket {

    public static final int ACTION_REROLL = 0;
    public static final int ACTION_SELECT = 1;

    private final BlockPos pos;
    private final int actionType;
    private final int choiceIndex;

    public EnhancementActionPacket(BlockPos pos, int actionType, int choiceIndex) {
        this.pos = pos;
        this.actionType = actionType;
        this.choiceIndex = choiceIndex;
    }

    public static void encode(EnhancementActionPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.actionType);
        buf.writeInt(msg.choiceIndex);
    }

    public static EnhancementActionPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int actionType = buf.readInt();
        int choiceIndex = buf.readInt();
        return new EnhancementActionPacket(pos, actionType, choiceIndex);
    }

    public static void handle(EnhancementActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

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
        ctx.get().setPacketHandled(true);
    }
}
