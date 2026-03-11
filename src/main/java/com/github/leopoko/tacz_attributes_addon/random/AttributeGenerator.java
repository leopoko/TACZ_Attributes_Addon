package com.github.leopoko.tacz_attributes_addon.random;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.AttributeEntry;
import com.github.leopoko.tacz_attributes_addon.data.AttributeRegistry;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeOverrides;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core attribute generation logic supporting multiple random modes.
 */
public class AttributeGenerator {

    /**
     * Generate random attributes for a gun.
     */
    public static List<GunModifier> generate(ItemStack gun, RandomSource random) {
        CommonConfig.RandomMode mode = CommonConfig.RANDOM_MODE.get();
        int min = CommonConfig.MIN_ATTRIBUTES.get();
        int max = CommonConfig.MAX_ATTRIBUTES.get();
        CommonConfig.DistributionType distType = CommonConfig.VALUE_DISTRIBUTION.get();
        double exponent = CommonConfig.DISTRIBUTION_EXPONENT.get();
        double raritySpread = CommonConfig.RARITY_SPREAD_FACTOR.get();
        double buffRatio = CommonConfig.BUFF_DEBUFF_RATIO.get();

        return generate(gun, mode, min, max, distType, exponent, raritySpread, buffRatio, random);
    }

    public static List<GunModifier> generate(ItemStack gun, CommonConfig.RandomMode mode,
                                             int minCount, int maxCount,
                                             CommonConfig.DistributionType distType, double exponent,
                                             double raritySpread, double buffRatio,
                                             RandomSource random) {
        // Resolve gun type, gun ID, and fire modes
        String gunType = GunTypeFilter.resolveGunType(gun);
        ResourceLocation gunId = GunTypeFilter.resolveGunId(gun);
        Set<String> fireModes = GunTypeFilter.getAvailableFireModes(gun);

        // Check for per-gun overrides
        GunAttributeOverrides.GunOverride override = gunId != null
                ? GunAttributeOverrides.getOverride(gunId.toString()) : null;

        // Use override counts if present, otherwise global config
        int effectiveMin = override != null && override.hasMinAttributes() ? override.getMinAttributes() : minCount;
        int effectiveMax = override != null && override.hasMaxAttributes() ? override.getMaxAttributes() : maxCount;

        // Get filtered attribute pool
        List<AttributeEntry> pool = getPool(mode, gunType, gunId, fireModes);

        // Apply per-gun attribute whitelist filter
        if (override != null && override.hasAttributeList()) {
            pool = pool.stream()
                    .filter(e -> override.hasAttribute(e.getAttributeId()))
                    .collect(Collectors.toList());
        }

        if (pool.isEmpty()) return Collections.emptyList();

        // Determine count
        int count = effectiveMin + (effectiveMax > effectiveMin ? random.nextInt(effectiveMax - effectiveMin + 1) : 0);
        count = Math.min(count, pool.size());

        // Select attributes
        List<AttributeEntry> selected = selectAttributes(pool, count, mode, raritySpread, random);

        // Resolve linked attributes: if a selected entry has a linkedAttribute,
        // ensure the linked partner is also in the selection.
        // e.g., ammo_recovery_chance requires ammo_recovery_amount to function.
        selected = resolveLinkedAttributes(selected, pool);

        // Generate values and create modifiers
        List<GunModifier> modifiers = new ArrayList<>();
        int buffCount = 0;
        int debuffCount = 0;

        for (AttributeEntry entry : selected) {
            // Use override value ranges if available
            double entryMin = override != null
                    ? override.getMinValue(entry.getAttributeId(), entry.getMinValue()) : entry.getMinValue();
            double entryMax = override != null
                    ? override.getMaxValue(entry.getAttributeId(), entry.getMaxValue()) : entry.getMaxValue();

            double value;

            if (mode == CommonConfig.RandomMode.RARITY_ADAPTIVE || mode == CommonConfig.RandomMode.BALANCED) {
                // Skewed distribution favoring lower absolute values
                value = ValueDistribution.sampleAbsoluteSkewed(
                        entryMin, entryMax, distType, exponent, random);
            } else {
                // Uniform distribution
                value = ValueDistribution.sample(
                        entryMin, entryMax,
                        CommonConfig.DistributionType.LINEAR, 1.0, random);
            }

            value = ValueDistribution.roundToPercent(value);

            // BALANCED mode: adjust for buff/debuff ratio
            if (mode == CommonConfig.RandomMode.BALANCED) {
                boolean isBuff = entry.isValueBuff(value);
                if (isBuff) buffCount++;
                else debuffCount++;

                // If ratio is off, try to flip the sign
                if (buffCount > 0 && debuffCount > 0) {
                    double currentRatio = (double) buffCount / debuffCount;
                    if (currentRatio > buffRatio * 1.5 && isBuff && entryMin < entry.getBuffThreshold()) {
                        // Too many buffs, flip to debuff
                        value = ValueDistribution.sample(
                                entryMin, entry.getBuffThreshold(),
                                distType, exponent, random);
                        value = ValueDistribution.roundToPercent(value);
                        buffCount--;
                        debuffCount++;
                    } else if (currentRatio < buffRatio * 0.5 && !isBuff && entryMax > entry.getBuffThreshold()) {
                        // Too many debuffs, flip to buff
                        value = ValueDistribution.sample(
                                entry.getBuffThreshold(), entryMax,
                                distType, exponent, random);
                        value = ValueDistribution.roundToPercent(value);
                        debuffCount--;
                        buffCount++;
                    }
                }
            }

            if (value != 0.0) {
                modifiers.add(new GunModifier(entry.getAttributeId(), value, entry.getOperation()));
            }
        }

        return modifiers;
    }

    private static List<AttributeEntry> getPool(CommonConfig.RandomMode mode, String gunType,
                                                ResourceLocation gunId, Set<String> fireModes) {
        List<AttributeEntry> all = AttributeRegistry.getEntries();

        switch (mode) {
            case FULL_RANDOM:
                return GunTypeFilter.filter(all, gunType, gunId, fireModes, false);
            case ADAPTIVE:
            case RARITY_ADAPTIVE:
            case BALANCED:
                return GunTypeFilter.filter(all, gunType, gunId, fireModes, true);
            default:
                return new ArrayList<>(all);
        }
    }

    private static List<AttributeEntry> selectAttributes(List<AttributeEntry> pool, int count,
                                                         CommonConfig.RandomMode mode, double raritySpread,
                                                         RandomSource random) {
        if (mode == CommonConfig.RandomMode.FULL_RANDOM || mode == CommonConfig.RandomMode.ADAPTIVE) {
            // Uniform selection by weight
            return weightedSelect(pool, count, false, 1.0, random);
        } else {
            // Rarity-weighted selection
            return weightedSelect(pool, count, true, raritySpread, random);
        }
    }

    /**
     * Ensure linked attribute pairs are complete.
     * If an attribute with a linkedAttribute is selected but its partner is not,
     * the partner is found from the pool and added automatically.
     * This prevents useless attributes like ammo_recovery_chance without ammo_recovery_amount.
     */
    private static List<AttributeEntry> resolveLinkedAttributes(List<AttributeEntry> selected, List<AttributeEntry> pool) {
        Set<String> selectedIds = new HashSet<>();
        for (AttributeEntry e : selected) {
            selectedIds.add(e.getAttributeId());
        }

        List<AttributeEntry> toAdd = new ArrayList<>();
        for (AttributeEntry entry : selected) {
            if (entry.hasLinkedAttribute() && !selectedIds.contains(entry.getLinkedAttribute())) {
                // Find the linked entry from the pool
                for (AttributeEntry poolEntry : pool) {
                    if (poolEntry.getAttributeId().equals(entry.getLinkedAttribute())) {
                        toAdd.add(poolEntry);
                        selectedIds.add(poolEntry.getAttributeId());
                        break;
                    }
                }
            }
        }

        if (!toAdd.isEmpty()) {
            List<AttributeEntry> result = new ArrayList<>(selected);
            result.addAll(toAdd);
            return result;
        }
        return selected;
    }

    /**
     * Weighted random selection without replacement.
     */
    private static List<AttributeEntry> weightedSelect(List<AttributeEntry> pool, int count,
                                                       boolean useRarityWeight, double raritySpread,
                                                       RandomSource random) {
        List<AttributeEntry> available = new ArrayList<>(pool);
        List<AttributeEntry> selected = new ArrayList<>();

        for (int i = 0; i < count && !available.isEmpty(); i++) {
            double totalWeight = 0;
            double[] weights = new double[available.size()];

            for (int j = 0; j < available.size(); j++) {
                AttributeEntry entry = available.get(j);
                double w = entry.getWeight();
                if (useRarityWeight) {
                    // Higher rarity tier = lower weight (rarer)
                    w /= Math.pow(entry.getRarityTier(), raritySpread);
                }
                weights[j] = w;
                totalWeight += w;
            }

            if (totalWeight <= 0) break;

            // Select based on weight
            double roll = random.nextDouble() * totalWeight;
            double cumulative = 0;
            int selectedIndex = 0;
            for (int j = 0; j < weights.length; j++) {
                cumulative += weights[j];
                if (roll < cumulative) {
                    selectedIndex = j;
                    break;
                }
            }

            selected.add(available.remove(selectedIndex));
        }

        return selected;
    }
}
