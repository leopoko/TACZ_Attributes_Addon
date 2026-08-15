package com.github.leopoko.tacz_attributes_addon.random;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.AttributeEntry;
import com.github.leopoko.tacz_attributes_addon.data.AttributeGroup;
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

        // Use split positive/negative generation if configured
        if (override != null && override.hasSplitCounts()) {
            return generateSplit(pool, override, mode, distType, exponent, raritySpread, random);
        }

        // Determine count
        int count = effectiveMin + (effectiveMax > effectiveMin ? random.nextInt(effectiveMax - effectiveMin + 1) : 0);
        count = Math.min(count, pool.size());

        // Select attributes
        List<AttributeEntry> selected = selectAttributes(pool, count, mode, raritySpread, override, random);

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
                // Use override operation if available
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation op = override != null
                        ? override.getOperation(entry.getAttributeId(), entry.getOperation())
                        : entry.getOperation();
                modifiers.add(new GunModifier(entry.getAttributeId(), value, op));
            }
        }

        return modifiers;
    }

    /**
     * Split generation mode: selects positive and negative attributes independently.
     * Splits the pool into positive-capable and negative-capable sub-pools based on
     * each attribute's buffThreshold, then selects from each pool with separate counts.
     */
    private static List<GunModifier> generateSplit(List<AttributeEntry> pool,
                                                    GunAttributeOverrides.GunOverride override,
                                                    CommonConfig.RandomMode mode,
                                                    CommonConfig.DistributionType distType,
                                                    double exponent,
                                                    double raritySpread,
                                                    RandomSource random) {
        // Split pool into positive-capable and negative-capable sub-pools
        List<AttributeEntry> positivePool = new ArrayList<>();
        List<AttributeEntry> negativePool = new ArrayList<>();

        for (AttributeEntry entry : pool) {
            String attrId = entry.getAttributeId();
            double effectiveMax = override.getMaxValue(attrId, entry.getMaxValue());
            double effectiveMin = override.getMinValue(attrId, entry.getMinValue());
            double threshold = entry.getBuffThreshold();
            boolean inverted = entry.getScoreWeight() < 0;

            // For inverted attributes (e.g., recoil where negative = buff):
            //   positive pool = values below threshold (negative values = buff)
            //   negative pool = values above threshold (positive values = debuff)
            // For normal attributes:
            //   positive pool = values above threshold (positive values = buff)
            //   negative pool = values below threshold (negative values = debuff)

            // Also check override pos/neg value ranges for pool membership
            boolean hasOverridePosRange = !Double.isNaN(override.getMinValuePos(attrId, Double.NaN))
                    || !Double.isNaN(override.getMaxValuePos(attrId, Double.NaN));
            boolean hasOverrideNegRange = !Double.isNaN(override.getMinValueNeg(attrId, Double.NaN))
                    || !Double.isNaN(override.getMaxValueNeg(attrId, Double.NaN));

            if (inverted) {
                // Inverted: buff = below threshold, debuff = above threshold
                if (hasOverridePosRange || effectiveMin < threshold) {
                    positivePool.add(entry);
                }
                if (hasOverrideNegRange || effectiveMax > threshold) {
                    negativePool.add(entry);
                }
            } else {
                // Normal: buff = above threshold, debuff = below threshold
                if (hasOverridePosRange || effectiveMax > threshold) {
                    positivePool.add(entry);
                }
                if (hasOverrideNegRange || effectiveMin < threshold) {
                    negativePool.add(entry);
                }
            }
        }

        // Determine counts from override (default to 0 if not set)
        int minPos = override.hasMinAttributesPos() ? override.getMinAttributesPos() : 0;
        int maxPos = override.hasMaxAttributesPos() ? override.getMaxAttributesPos() : minPos;
        int minNeg = override.hasMinAttributesNeg() ? override.getMinAttributesNeg() : 0;
        int maxNeg = override.hasMaxAttributesNeg() ? override.getMaxAttributesNeg() : minNeg;

        int posCount = minPos + (maxPos > minPos ? random.nextInt(maxPos - minPos + 1) : 0);
        int negCount = minNeg + (maxNeg > minNeg ? random.nextInt(maxNeg - minNeg + 1) : 0);

        // Cap to pool sizes
        posCount = Math.min(posCount, positivePool.size());
        negCount = Math.min(negCount, negativePool.size());

        // Cap total if minAttributes/maxAttributes are also set
        if (override.hasMaxAttributes()) {
            int totalMax = override.getMaxAttributes();
            if (posCount + negCount > totalMax) {
                // Proportionally reduce, prioritizing the larger allocation
                double ratio = (double) totalMax / (posCount + negCount);
                posCount = (int) Math.round(posCount * ratio);
                negCount = totalMax - posCount;
            }
        }

        // Select positive attributes
        List<AttributeEntry> posSelected = selectAttributes(positivePool, posCount, mode, raritySpread, override, random);

        // Remove positive selections from negative pool to prevent duplicates
        Set<String> posSelectedIds = new HashSet<>();
        for (AttributeEntry e : posSelected) {
            posSelectedIds.add(e.getAttributeId());
        }
        List<AttributeEntry> filteredNegPool = negativePool.stream()
                .filter(e -> !posSelectedIds.contains(e.getAttributeId()))
                .collect(Collectors.toList());
        negCount = Math.min(negCount, filteredNegPool.size());

        // Select negative attributes
        List<AttributeEntry> negSelected = selectAttributes(filteredNegPool, negCount, mode, raritySpread, override, random);

        // Merge and resolve linked attributes
        List<AttributeEntry> allSelected = new ArrayList<>(posSelected);
        allSelected.addAll(negSelected);
        allSelected = resolveLinkedAttributes(allSelected, pool);

        // Track which attributes were selected for positive vs negative
        Set<String> negSelectedIds = new HashSet<>();
        for (AttributeEntry e : negSelected) {
            negSelectedIds.add(e.getAttributeId());
        }

        // Generate values and create modifiers
        List<GunModifier> modifiers = new ArrayList<>();

        for (AttributeEntry entry : allSelected) {
            String attrId = entry.getAttributeId();
            boolean isPositiveSelection = posSelectedIds.contains(attrId);
            boolean isNegativeSelection = negSelectedIds.contains(attrId);
            // Linked attributes that weren't in either set: use full range
            boolean isLinkedAddition = !isPositiveSelection && !isNegativeSelection;

            double valMin, valMax;

            if (isPositiveSelection) {
                // Positive roll: generate a buff value
                double threshold = entry.getBuffThreshold();
                boolean inverted = entry.getScoreWeight() < 0;
                double effectiveMin = override.getMinValue(attrId, entry.getMinValue());
                double effectiveMax = override.getMaxValue(attrId, entry.getMaxValue());

                if (inverted) {
                    // Inverted attribute (e.g., recoil): buff = values below threshold
                    valMin = override.getMinValuePos(attrId, effectiveMin);
                    valMax = override.getMaxValuePos(attrId, Math.min(effectiveMax, threshold));
                    if (valMin > valMax) { double tmp = valMin; valMin = valMax; valMax = tmp; }
                    valMax = Math.min(valMax, threshold);
                } else {
                    // Normal attribute: buff = values above threshold
                    valMin = override.getMinValuePos(attrId, Math.max(effectiveMin, threshold));
                    valMax = override.getMaxValuePos(attrId, effectiveMax);
                    if (valMin > valMax) { double tmp = valMin; valMin = valMax; valMax = tmp; }
                    valMin = Math.max(valMin, threshold);
                }
            } else if (isNegativeSelection) {
                // Negative roll: generate a debuff value
                double threshold = entry.getBuffThreshold();
                boolean inverted = entry.getScoreWeight() < 0;
                double effectiveMin = override.getMinValue(attrId, entry.getMinValue());
                double effectiveMax = override.getMaxValue(attrId, entry.getMaxValue());

                if (inverted) {
                    // Inverted attribute (e.g., recoil): debuff = values above threshold
                    valMin = override.getMinValueNeg(attrId, Math.max(effectiveMin, threshold));
                    valMax = override.getMaxValueNeg(attrId, effectiveMax);
                    if (valMin > valMax) { double tmp = valMin; valMin = valMax; valMax = tmp; }
                    valMin = Math.max(valMin, threshold);
                } else {
                    // Normal attribute: debuff = values below threshold
                    valMin = override.getMinValueNeg(attrId, effectiveMin);
                    valMax = override.getMaxValueNeg(attrId, Math.min(effectiveMax, threshold));
                    if (valMin > valMax) { double tmp = valMin; valMin = valMax; valMax = tmp; }
                    valMax = Math.min(valMax, threshold);
                }
            } else {
                // Linked addition: use full range
                valMin = override.getMinValue(attrId, entry.getMinValue());
                valMax = override.getMaxValue(attrId, entry.getMaxValue());
            }

            if (valMin >= valMax) continue; // Skip if range is invalid

            double value;
            if (mode == CommonConfig.RandomMode.RARITY_ADAPTIVE || mode == CommonConfig.RandomMode.BALANCED) {
                value = ValueDistribution.sampleAbsoluteSkewed(valMin, valMax, distType, exponent, random);
            } else {
                value = ValueDistribution.sample(valMin, valMax, CommonConfig.DistributionType.LINEAR, 1.0, random);
            }

            value = ValueDistribution.roundToPercent(value);

            if (value != 0.0) {
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation op = override.getOperation(attrId, entry.getOperation());
                modifiers.add(new GunModifier(attrId, value, op));
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
                                                         GunAttributeOverrides.GunOverride override,
                                                         RandomSource random) {
        List<AttributeGroup> groups = getEffectiveGroups(override);
        if (mode == CommonConfig.RandomMode.FULL_RANDOM || mode == CommonConfig.RandomMode.ADAPTIVE) {
            // Uniform selection by weight
            return weightedSelect(pool, count, false, 1.0, override, groups, random);
        } else {
            // Rarity-weighted selection
            return weightedSelect(pool, count, true, raritySpread, override, groups, random);
        }
    }

    /**
     * Merge global attribute groups with per-gun overrides.
     * Per-gun groups with the same name override the global definition.
     * Per-gun groups without an attributes list inherit the global group's attribute list.
     */
    private static List<AttributeGroup> getEffectiveGroups(GunAttributeOverrides.GunOverride override) {
        List<AttributeGroup> globalGroups = AttributeRegistry.getGroups();

        if (override == null || !override.hasAttributeGroups()) {
            return globalGroups;
        }

        // Build a map from global groups
        Map<String, AttributeGroup> merged = new LinkedHashMap<>();
        for (AttributeGroup g : globalGroups) {
            merged.put(g.getName(), g);
        }

        // Merge per-gun groups
        for (AttributeGroup perGun : override.getAttributeGroups()) {
            if (perGun.hasAttributes()) {
                // Per-gun group has its own attribute list — fully overrides
                merged.put(perGun.getName(), perGun);
            } else {
                // Per-gun group has no attribute list — inherit from global if exists
                AttributeGroup existing = merged.get(perGun.getName());
                if (existing != null) {
                    // Override maxFromGroup but keep global attributes
                    merged.put(perGun.getName(), new AttributeGroup(
                            perGun.getName(), perGun.getMaxFromGroup(), existing.getAttributes()));
                } else {
                    // New group with no attributes — effectively a no-op, but store it
                    merged.put(perGun.getName(), perGun);
                }
            }
        }

        return new ArrayList<>(merged.values());
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
     * Enforces attribute group limits by removing exhausted group members from the pool.
     */
    private static List<AttributeEntry> weightedSelect(List<AttributeEntry> pool, int count,
                                                       boolean useRarityWeight, double raritySpread,
                                                       GunAttributeOverrides.GunOverride override,
                                                       List<AttributeGroup> groups,
                                                       RandomSource random) {
        List<AttributeEntry> available = new ArrayList<>(pool);
        List<AttributeEntry> selected = new ArrayList<>();

        // Track group selection counts
        Map<String, Integer> groupCounts = groups.isEmpty() ? Collections.emptyMap() : new HashMap<>();

        for (int i = 0; i < count && !available.isEmpty(); i++) {
            double totalWeight = 0;
            double[] weights = new double[available.size()];

            for (int j = 0; j < available.size(); j++) {
                AttributeEntry entry = available.get(j);
                // Use override weight if available, otherwise pool weight
                double w = override != null
                        ? override.getWeight(entry.getAttributeId(), entry.getWeight())
                        : entry.getWeight();
                if (useRarityWeight) {
                    // Use override rarityTier if available, otherwise pool rarityTier
                    int tier = override != null
                            ? override.getRarityTier(entry.getAttributeId(), entry.getRarityTier())
                            : entry.getRarityTier();
                    // Higher rarity tier = lower weight (rarer)
                    w /= Math.pow(tier, raritySpread);
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

            AttributeEntry chosen = available.remove(selectedIndex);
            selected.add(chosen);

            // Enforce attribute group limits
            if (!groups.isEmpty()) {
                String chosenId = chosen.getAttributeId();
                for (AttributeGroup group : groups) {
                    if (group.getAttributes().contains(chosenId)) {
                        int newCount = groupCounts.merge(group.getName(), 1, Integer::sum);
                        if (newCount >= group.getMaxFromGroup()) {
                            // Remove all remaining members of this group from available pool
                            available.removeIf(e -> group.getAttributes().contains(e.getAttributeId()));
                        }
                    }
                }
            }
        }

        return selected;
    }
}
