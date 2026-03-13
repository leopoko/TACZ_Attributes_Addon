package com.github.leopoko.tacz_attributes_addon.block;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.github.leopoko.tacz_attributes_addon.data.StationMaterial;
import com.github.leopoko.tacz_attributes_addon.data.StationMaterialRegistry;
import com.github.leopoko.tacz_attributes_addon.handler.RarityHandler;
import com.github.leopoko.tacz_attributes_addon.handler.WeaponTypeHandler;
import com.github.leopoko.tacz_attributes_addon.init.ModBlockEntities;
import com.github.leopoko.tacz_attributes_addon.random.AttributeGenerator;
import com.github.leopoko.tacz_attributes_addon.random.RarityConstraintHelper;
import com.tacz.guns.api.item.IGun;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Attribute Station block entity with WorldlyContainer for hopper support.
 * Slots: 0=gun input, 1=material, 2=output
 */
public class AttributeStationBlockEntity extends BlockEntity implements WorldlyContainer {
    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int progress = 0;
    private int maxProgress = 200;
    private boolean processing = false;

    // Hopper slot access: input from top/sides, output from bottom
    private static final int[] SLOTS_FOR_UP = {0, 1};        // Gun + material from top
    private static final int[] SLOTS_FOR_SIDES = {0, 1};     // Gun + material from sides
    private static final int[] SLOTS_FOR_DOWN = {2};          // Output from bottom

    // ContainerData for client sync (progress + maxProgress)
    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public AttributeStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATTRIBUTE_STATION.get(), pos, state);
    }

    // ========== WorldlyContainer Implementation ==========

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) return SLOTS_FOR_DOWN;
        if (side == Direction.UP) return SLOTS_FOR_UP;
        return SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        if (slot == 0) return IGun.getIGunOrNull(stack) != null; // Only guns in slot 0
        if (slot == 1) return IGun.getIGunOrNull(stack) == null; // Non-gun items only in material slot
        return false; // Cannot insert into output
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return slot == 2; // Can only extract from output slot
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    // ========== Accessors ==========

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public ItemStack getInput() { return items.get(0); }
    public ItemStack getMaterial() { return items.get(1); }
    public ItemStack getOutput() { return items.get(2); }

    public int getProgress() { return progress; }
    public int getMaxProgress() { return maxProgress; }

    // ========== Server Tick Logic ==========

    public static void serverTick(Level level, BlockPos pos, BlockState state, AttributeStationBlockEntity be) {
        be.maxProgress = CommonConfig.STATION_PROCESSING_TIME.get();

        if (be.canProcess()) {
            if (!be.processing) {
                be.processing = true;
                be.progress = 0;
            }

            be.progress++;
            be.setChanged();

            if (be.progress >= be.maxProgress) {
                be.processGun(level);
                be.progress = 0;
                be.processing = false;
            }
        } else {
            if (be.processing) {
                be.progress = 0;
                be.processing = false;
                be.setChanged();
            }
        }
    }

    private boolean canProcess() {
        ItemStack input = getInput();
        ItemStack output = getOutput();

        // Must have a gun in input
        if (input.isEmpty() || IGun.getIGunOrNull(input) == null) return false;

        // Output must be empty
        if (!output.isEmpty()) return false;

        // Check reroll permission
        if (GunAttributeData.hasAddonData(input)) {
            if (!CommonConfig.STATION_ALLOW_REROLL.get()) return false;

            // Check reroll limit (0 = unlimited)
            int maxRerolls = CommonConfig.STATION_MAX_REROLLS.get();
            if (maxRerolls > 0) {
                int currentRerolls = GunAttributeData.getRerollCount(input);
                if (currentRerolls >= maxRerolls) return false;
            }
        }

        // Check material requirement
        if (CommonConfig.STATION_CONSUME_ITEM.get()) {
            ItemStack material = getMaterial();
            if (material.isEmpty()) return false;

            if (StationMaterialRegistry.hasRegisteredMaterials()) {
                // Use station_materials.json registry
                if (StationMaterialRegistry.findMatchingMaterial(material) == null) return false;
            } else {
                // Fallback to legacy config
                String requiredItemId = CommonConfig.STATION_CONSUME_ITEM_ID.get();
                ResourceLocation required = new ResourceLocation(requiredItemId);
                ResourceLocation materialId = ForgeRegistries.ITEMS.getKey(material.getItem());
                if (!required.equals(materialId)) return false;

                int requiredCount = CommonConfig.STATION_CONSUME_COUNT.get();
                if (material.getCount() < requiredCount) return false;
            }
        }

        return true;
    }

    private void processGun(Level level) {
        ItemStack input = getInput();
        ItemStack result = input.copy();
        RandomSource random = level.getRandom();

        // Increment reroll count
        if (GunAttributeData.hasAddonData(result)) {
            GunAttributeData.incrementRerollCount(result);
        }

        // Resolve material rarity constraints
        StationMaterial matchedMaterial = null;
        if (CommonConfig.STATION_CONSUME_ITEM.get() && StationMaterialRegistry.hasRegisteredMaterials()) {
            matchedMaterial = StationMaterialRegistry.findMatchingMaterial(getMaterial());
        }

        if (matchedMaterial != null && matchedMaterial.hasRarityConstraint()) {
            // Generate with rarity constraint from material
            RarityConstraintHelper.generateWithConstraint(result, random,
                    matchedMaterial.getTargetRarity(),
                    matchedMaterial.getMinRarity(),
                    matchedMaterial.getMaxRarity());
        } else {
            // Standard generation (no material constraint)
            RarityConstraintHelper.generateStandard(result, random);
        }

        GunAttributeData.setSealed(result, true);

        // Consume material if required
        if (CommonConfig.STATION_CONSUME_ITEM.get()) {
            ItemStack material = getMaterial();
            int consumeCount = matchedMaterial != null
                    ? matchedMaterial.getCount()
                    : CommonConfig.STATION_CONSUME_COUNT.get();
            material.shrink(consumeCount);
        }

        // Set result
        items.set(0, ItemStack.EMPTY);
        items.set(2, result);
        setChanged();
    }

    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, items);
    }

    // ========== NBT Persistence ==========

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("Progress", progress);
        tag.putBoolean("Processing", processing);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.clear();
        ContainerHelper.loadAllItems(tag, items);
        progress = tag.getInt("Progress");
        processing = tag.getBoolean("Processing");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
