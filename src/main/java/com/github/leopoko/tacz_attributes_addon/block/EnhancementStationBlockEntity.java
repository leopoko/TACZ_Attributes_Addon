package com.github.leopoko.tacz_attributes_addon.block;

import com.github.leopoko.tacz_attributes_addon.bridge.AttributeBridge;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.AttributeEntry;
import com.github.leopoko.tacz_attributes_addon.data.AttributeRegistry;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.github.leopoko.tacz_attributes_addon.handler.RarityHandler;
import com.github.leopoko.tacz_attributes_addon.init.ModBlockEntities;
import com.github.leopoko.tacz_attributes_addon.network.EnhancementChoicesPacket;
import com.github.leopoko.tacz_attributes_addon.network.ModNetwork;
import com.github.leopoko.tacz_attributes_addon.random.GunTypeFilter;
import com.github.leopoko.tacz_attributes_addon.random.ValueDistribution;
import com.tacz.guns.api.item.IGun;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Enhancement Station block entity.
 * Slots: 0=gun, 1=material (no output slot - gun is modified in place).
 * Generates random enhancement choices that players can select from.
 */
public class EnhancementStationBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    // Per-player choices (transient, not saved to NBT)
    private final Map<UUID, List<EnhancementChoice>> playerChoices = new HashMap<>();

    private static final int[] SLOTS_FOR_UP = {0, 1};
    private static final int[] SLOTS_FOR_SIDES = {0, 1};
    private static final int[] SLOTS_FOR_DOWN = {};

    public record EnhancementChoice(String attributeId, double value, AttributeModifier.Operation operation) {}

    public EnhancementStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENHANCEMENT_STATION.get(), pos, state);
    }

    // ========== Enhancement Logic ==========

    public void generateChoices(ItemStack gun, UUID playerUUID) {
        List<EnhancementChoice> choices = new ArrayList<>();

        if (gun.isEmpty() || IGun.getIGunOrNull(gun) == null) {
            playerChoices.put(playerUUID, choices);
            return;
        }

        String gunType = GunTypeFilter.resolveGunType(gun);
        ResourceLocation gunId = GunTypeFilter.resolveGunId(gun);
        Set<String> fireModes = GunTypeFilter.getAvailableFireModes(gun);

        List<AttributeEntry> pool = GunTypeFilter.filter(
                AttributeRegistry.getEntries(), gunType, gunId, fireModes, true);
        if (pool.isEmpty()) {
            playerChoices.put(playerUUID, choices);
            return;
        }

        int choiceCount = CommonConfig.ENHANCEMENT_CHOICE_COUNT.get();
        double minMult = CommonConfig.ENHANCEMENT_MIN_VALUE.get();
        double maxMult = CommonConfig.ENHANCEMENT_MAX_VALUE.get();
        boolean onlyPositive = CommonConfig.ENHANCEMENT_ONLY_POSITIVE.get();
        RandomSource random = level != null ? level.getRandom() : RandomSource.create();

        // Weighted selection without replacement
        List<AttributeEntry> available = new ArrayList<>(pool);
        for (int i = 0; i < choiceCount && !available.isEmpty(); i++) {
            AttributeEntry entry = weightedRandomSelect(available, random);
            available.remove(entry);

            double range = Math.abs(entry.getMaxValue() - entry.getMinValue());
            double enhanceValue;

            if (onlyPositive) {
                enhanceValue = (minMult + random.nextDouble() * (maxMult - minMult)) * range;
                // For inverted attributes (recoil, etc.), buff direction is negative
                if (entry.getScoreWeight() < 0) {
                    enhanceValue = -Math.abs(enhanceValue);
                } else {
                    enhanceValue = Math.abs(enhanceValue);
                }
            } else {
                enhanceValue = (minMult + random.nextDouble() * (maxMult - minMult)) * range;
                if (random.nextBoolean()) enhanceValue = -enhanceValue;
            }

            enhanceValue = ValueDistribution.roundToPercent(enhanceValue);
            if (enhanceValue == 0.0) enhanceValue = 0.01;

            choices.add(new EnhancementChoice(entry.getAttributeId(), enhanceValue, entry.getOperation()));
        }

        playerChoices.put(playerUUID, choices);
    }

    private AttributeEntry weightedRandomSelect(List<AttributeEntry> entries, RandomSource random) {
        int totalWeight = entries.stream().mapToInt(AttributeEntry::getWeight).sum();
        if (totalWeight <= 0) return entries.get(random.nextInt(entries.size()));

        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (AttributeEntry entry : entries) {
            cumulative += entry.getWeight();
            if (roll < cumulative) return entry;
        }
        return entries.get(entries.size() - 1);
    }

    public void applyEnhancement(int choiceIndex, ServerPlayer player) {
        List<EnhancementChoice> choices = playerChoices.get(player.getUUID());
        if (choices == null || choiceIndex < 0 || choiceIndex >= choices.size()) return;

        ItemStack gun = getItem(0);
        if (gun.isEmpty() || IGun.getIGunOrNull(gun) == null) return;

        // Check max enhancements
        int maxEnhancements = CommonConfig.ENHANCEMENT_MAX_COUNT.get();
        if (maxEnhancements > 0) {
            int currentCount = GunAttributeData.getEnhanceCount(gun);
            if (currentCount >= maxEnhancements) return;
        }

        // Check material
        int cost = getApplyCost(gun);
        if (!hasSufficientMaterials(cost)) return;

        // Apply the chosen enhancement
        EnhancementChoice choice = choices.get(choiceIndex);
        mergeEnhancement(gun, choice);

        // Consume materials
        consumeMaterials(cost);

        setChanged();

        // Refresh player attributes if they're holding this gun
        AttributeBridge.refreshPlayer(player);

        // Auto-reroll and send updated choices
        generateChoices(gun, player.getUUID());
        sendChoicesToPlayer(player);
    }

    public void rerollChoices(ServerPlayer player) {
        ItemStack gun = getItem(0);
        if (gun.isEmpty() || IGun.getIGunOrNull(gun) == null) return;

        // Check material for reroll
        int rerollCost = CommonConfig.ENHANCEMENT_REROLL_COST.get();
        if (!hasSufficientMaterials(rerollCost)) return;

        // Consume materials
        consumeMaterials(rerollCost);

        setChanged();

        // Generate new choices
        generateChoices(gun, player.getUUID());
        sendChoicesToPlayer(player);
    }

    private void mergeEnhancement(ItemStack gun, EnhancementChoice choice) {
        List<GunModifier> enhanced = GunAttributeData.getEnhancedModifiers(gun);
        boolean merged = false;

        List<GunModifier> updated = new ArrayList<>();
        for (GunModifier existing : enhanced) {
            if (existing.getAttributeId().equals(choice.attributeId()) &&
                    existing.getOperation() == choice.operation()) {
                double newValue = existing.getValue() + choice.value();
                newValue = ValueDistribution.roundToPercent(newValue);
                updated.add(new GunModifier(existing.getAttributeId(), newValue, existing.getOperation()));
                merged = true;
            } else {
                updated.add(existing);
            }
        }

        if (!merged) {
            updated.add(new GunModifier(choice.attributeId(), choice.value(), choice.operation()));
        }

        GunAttributeData.setEnhancedModifiers(gun, updated);
        GunAttributeData.incrementEnhanceCount(gun);

        // Recalculate rarity score
        if (CommonConfig.ENABLE_RARITY_SCORING.get()) {
            RarityHandler.calculateAndApplyRarity(gun);
        }
    }

    public int getApplyCost(ItemStack gun) {
        int baseCost = CommonConfig.ENHANCEMENT_APPLY_COST.get();
        if (!CommonConfig.ENHANCEMENT_COST_SCALING.get()) return baseCost;
        int enhanceCount = GunAttributeData.getEnhanceCount(gun);
        return baseCost + enhanceCount * CommonConfig.ENHANCEMENT_COST_SCALING_AMOUNT.get();
    }

    private boolean hasSufficientMaterials(int requiredCount) {
        ItemStack material = getItem(1);
        if (material.isEmpty()) return false;

        String requiredItemId = CommonConfig.ENHANCEMENT_MATERIAL_ID.get();
        ResourceLocation required = new ResourceLocation(requiredItemId);
        ResourceLocation materialId = ForgeRegistries.ITEMS.getKey(material.getItem());
        if (!required.equals(materialId)) return false;

        return material.getCount() >= requiredCount;
    }

    private void consumeMaterials(int count) {
        ItemStack material = getItem(1);
        material.shrink(count);
    }

    public void sendChoicesToPlayer(ServerPlayer player) {
        List<EnhancementChoice> choices = playerChoices.getOrDefault(player.getUUID(), List.of());
        ItemStack gun = getItem(0);

        List<EnhancementChoicesPacket.ChoiceEntry> entries = new ArrayList<>();
        for (EnhancementChoice c : choices) {
            entries.add(new EnhancementChoicesPacket.ChoiceEntry(
                    c.attributeId(), c.value(), c.operation().toValue()));
        }

        int enhanceCount = gun.isEmpty() ? 0 : GunAttributeData.getEnhanceCount(gun);
        int maxEnhancements = CommonConfig.ENHANCEMENT_MAX_COUNT.get();
        int applyCost = gun.isEmpty() ? CommonConfig.ENHANCEMENT_APPLY_COST.get() : getApplyCost(gun);
        int rerollCost = CommonConfig.ENHANCEMENT_REROLL_COST.get();

        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new EnhancementChoicesPacket(getBlockPos(), entries,
                        enhanceCount, maxEnhancements, applyCost, rerollCost));
    }

    public void onPlayerOpenMenu(ServerPlayer player) {
        ItemStack gun = getItem(0);
        if (!gun.isEmpty() && IGun.getIGunOrNull(gun) != null) {
            if (!playerChoices.containsKey(player.getUUID())) {
                generateChoices(gun, player.getUUID());
            }
        }
        sendChoicesToPlayer(player);
    }

    public void onPlayerCloseMenu(UUID playerUUID) {
        playerChoices.remove(playerUUID);
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
        if (slot == 0) return IGun.getIGunOrNull(stack) != null;
        if (slot == 1) return IGun.getIGunOrNull(stack) == null;
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return false; // No hopper extraction
    }

    @Override
    public int getContainerSize() {
        return 2;
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

    // ========== MenuProvider ==========

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tacz_attributes_addon.enhancement_station");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return new EnhancementStationMenu(containerId, inv, this);
    }

    // ========== Accessors ==========

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public void dropContents() {
        if (level != null) {
            Containers.dropContents(level, getBlockPos(), items);
        }
    }

    // ========== NBT Persistence ==========

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.clear();
        ContainerHelper.loadAllItems(tag, items);
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
