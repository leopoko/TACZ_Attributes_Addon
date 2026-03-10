package com.github.leopoko.tacz_attributes_addon.block;

import com.github.leopoko.tacz_attributes_addon.init.ModMenuTypes;
import com.github.leopoko.tacz_attributes_addon.network.EnhancementChoicesPacket;
import com.tacz.guns.api.item.IGun;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Container menu for the Enhancement Station.
 * Uses network packets (not ContainerData) for choice synchronization.
 */
public class EnhancementStationMenu extends AbstractContainerMenu {
    private final EnhancementStationBlockEntity blockEntity;
    private final Player player;

    // Server-side: track gun slot changes to auto-generate choices
    private ItemStack lastGunItem = ItemStack.EMPTY;

    // Client-side data synced via packets
    private List<EnhancementChoicesPacket.ChoiceEntry> clientChoices = new ArrayList<>();
    private int clientEnhanceCount = 0;
    private int clientMaxEnhancements = 0;
    private int clientApplyCost = 1;
    private int clientRerollCost = 1;

    // Client constructor (from network)
    public EnhancementStationMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        super(ModMenuTypes.ENHANCEMENT_STATION.get(), containerId);
        this.blockEntity = getBlockEntity(playerInv, buf);
        this.player = playerInv.player;
        this.lastGunItem = blockEntity.getItem(0).copy();
        initSlots(playerInv);
    }

    // Server constructor
    public EnhancementStationMenu(int containerId, Inventory playerInv, EnhancementStationBlockEntity blockEntity) {
        super(ModMenuTypes.ENHANCEMENT_STATION.get(), containerId);
        this.blockEntity = blockEntity;
        this.player = playerInv.player;
        this.lastGunItem = blockEntity.getItem(0).copy();
        initSlots(playerInv);
    }

    private void initSlots(Inventory playerInv) {
        // Slot 0: Gun input
        addSlot(new Slot(blockEntity, 0, 26, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return IGun.getIGunOrNull(stack) != null;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        // Slot 1: Material
        addSlot(new Slot(blockEntity, 1, 26, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return IGun.getIGunOrNull(stack) == null;
            }
        });

        // Player inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 118 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 176));
        }
    }

    // ========== Client-side choice accessors ==========

    public void setClientChoices(List<EnhancementChoicesPacket.ChoiceEntry> choices) {
        this.clientChoices = choices;
    }

    public List<EnhancementChoicesPacket.ChoiceEntry> getClientChoices() {
        return clientChoices;
    }

    public void setClientEnhanceCount(int count) {
        this.clientEnhanceCount = count;
    }

    public int getClientEnhanceCount() {
        return clientEnhanceCount;
    }

    public void setClientMaxEnhancements(int max) {
        this.clientMaxEnhancements = max;
    }

    public int getClientMaxEnhancements() {
        return clientMaxEnhancements;
    }

    public void setClientApplyCost(int cost) {
        this.clientApplyCost = cost;
    }

    public int getClientApplyCost() {
        return clientApplyCost;
    }

    public void setClientRerollCost(int cost) {
        this.clientRerollCost = cost;
    }

    public int getClientRerollCost() {
        return clientRerollCost;
    }

    public BlockPos getBlockPos() {
        return blockEntity.getBlockPos();
    }

    /**
     * Called every server tick. Detects gun slot changes and triggers
     * choice regeneration + sync so that placing a gun while the GUI
     * is open immediately shows enhancement choices.
     */
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!(player instanceof ServerPlayer serverPlayer)) return;

        ItemStack currentGun = getSlot(0).getItem();
        if (!ItemStack.matches(currentGun, lastGunItem)) {
            lastGunItem = currentGun.copy();
            blockEntity.generateChoices(currentGun, serverPlayer.getUUID());
            blockEntity.sendChoicesToPlayer(serverPlayer);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack result = slotStack.copy();

        if (index < 2) {
            // Moving from container to player inventory
            if (!moveItemStackTo(slotStack, 2, 38, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Moving from player inventory to container
            if (IGun.getIGunOrNull(slotStack) != null) {
                if (!moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(slotStack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        blockEntity.onPlayerCloseMenu(player.getUUID());
    }

    private static EnhancementStationBlockEntity getBlockEntity(Inventory inv, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return (EnhancementStationBlockEntity) inv.player.level().getBlockEntity(pos);
    }
}
