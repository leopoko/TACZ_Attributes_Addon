package com.github.leopoko.tacz_attributes_addon.block;

import com.github.leopoko.tacz_attributes_addon.init.ModMenuTypes;
import com.tacz.guns.api.item.IGun;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/**
 * Container menu for the Attribute Station.
 * Uses ContainerData to sync progress from server to client.
 * Uses direct Slot with WorldlyContainer (no ItemStackHandler wrapper needed).
 */
public class AttributeStationMenu extends AbstractContainerMenu {
    private final AttributeStationBlockEntity blockEntity;
    private final ContainerData data;

    // Client constructor (from network)
    public AttributeStationMenu(int containerId, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInv, getBlockEntity(playerInv, buf), new SimpleContainerData(2));
    }

    // Server constructor — uses the block entity's dataAccess for real sync
    public AttributeStationMenu(int containerId, Inventory playerInv, AttributeStationBlockEntity blockEntity) {
        this(containerId, playerInv, blockEntity, blockEntity.dataAccess);
    }

    private AttributeStationMenu(int containerId, Inventory playerInv,
                                  AttributeStationBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.ATTRIBUTE_STATION.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;

        // Track progress data — this syncs server→client automatically
        addDataSlots(data);

        // Slot 0: Gun input (left of arrow)
        addSlot(new Slot(blockEntity, 0, 48, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return IGun.getIGunOrNull(stack) != null;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        // Slot 1: Material (above arrow, optional) — guns cannot be placed here
        addSlot(new Slot(blockEntity, 1, 80, 11) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return IGun.getIGunOrNull(stack) == null; // Reject gun items
            }
        });

        // Slot 2: Output (right of arrow)
        addSlot(new Slot(blockEntity, 2, 112, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // Output only
            }
        });

        // Player inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public float getProgressPercent() {
        int max = getMaxProgress();
        return max > 0 ? (float) getProgress() / max : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack result = slotStack.copy();

        if (index < 3) {
            // Moving from container to player inventory
            if (!moveItemStackTo(slotStack, 3, 39, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Moving from player inventory to container
            if (IGun.getIGunOrNull(slotStack) != null) {
                // Gun goes to slot 0
                if (!moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Other items go to material slot
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

    private static AttributeStationBlockEntity getBlockEntity(Inventory inv, RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        return (AttributeStationBlockEntity) inv.player.level().getBlockEntity(pos);
    }
}
