package com.github.leopoko.tacz_attributes_addon.random;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.github.leopoko.tacz_attributes_addon.handler.RarityHandler;
import com.github.leopoko.tacz_attributes_addon.handler.WeaponTypeHandler;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

/**
 * Utility for generating gun attributes with rarity constraints.
 * Used by both GunObtainHandler (TaczPreset NBT) and AttributeStationBlockEntity (station materials).
 */
public class RarityConstraintHelper {

    private static final int MAX_ATTEMPTS = 50;

    /**
     * Generate attributes with rarity constraints.
     * Tries up to MAX_ATTEMPTS to find attributes matching the rarity requirement.
     * If no match is found, uses the best candidate and forces the rarity value.
     *
     * @param stack         the gun ItemStack (will be modified in place)
     * @param random        random source
     * @param targetRarity  exact rarity required (-1 = not set, takes precedence)
     * @param minRarity     minimum rarity required (-1 = not set)
     * @param maxRarity     maximum rarity allowed (-1 = not set)
     */
    public static void generateWithConstraint(ItemStack stack, RandomSource random,
                                              int targetRarity, int minRarity, int maxRarity) {
        boolean useTarget = targetRarity >= 0 && targetRarity <= 3;
        boolean useMin = minRarity >= 0 && minRarity <= 3;
        boolean useMax = maxRarity >= 0 && maxRarity <= 3;

        if (!useTarget && !useMin && !useMax) {
            // No constraints — standard generation
            generateStandard(stack, random);
            return;
        }

        List<GunModifier> bestModifiers = null;
        int bestScore = -1;
        int bestRarityOrdinal = 0;
        // For maxRarity: track the best candidate that is at or below the max
        List<GunModifier> bestMaxModifiers = null;
        int bestMaxScore = -1;
        int bestMaxRarityOrdinal = -1;

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            // Generate candidate attributes
            List<GunModifier> candidateModifiers = AttributeGenerator.generate(stack, random);

            // Temporarily apply to calculate score (including fixed attributes)
            GunAttributeData.setModifiers(stack, candidateModifiers);
            if (CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) {
                WeaponTypeHandler.applyFixedAttributes(stack);
            }

            List<GunModifier> allMods = GunAttributeData.getAllModifiers(stack);
            int score = RarityHandler.calculateScore(allMods);
            Rarity rarity = RarityHandler.scoreToRarity(score);
            int rarityOrdinal = rarity.ordinal();

            if (matchesConstraint(rarityOrdinal, targetRarity, minRarity, maxRarity,
                    useTarget, useMin, useMax)) {
                // Perfect match — apply and return
                GunAttributeData.setScore(stack, score);
                GunAttributeData.setRarityOrdinal(stack, rarityOrdinal);
                return;
            }

            // Track best candidates for fallback
            if (useTarget) {
                if (bestModifiers == null || isCloserToTarget(rarityOrdinal, score,
                        bestRarityOrdinal, bestScore, targetRarity)) {
                    bestModifiers = candidateModifiers;
                    bestScore = score;
                    bestRarityOrdinal = rarityOrdinal;
                }
            } else {
                // For min/max constraints, track both closest overall and best within max
                if (useMax && rarityOrdinal <= maxRarity) {
                    if (bestMaxModifiers == null || score > bestMaxScore) {
                        bestMaxModifiers = candidateModifiers;
                        bestMaxScore = score;
                        bestMaxRarityOrdinal = rarityOrdinal;
                    }
                }
                if (score > bestScore) {
                    bestModifiers = candidateModifiers;
                    bestScore = score;
                    bestRarityOrdinal = rarityOrdinal;
                }
            }

            // Clean up temporary data for next iteration
            GunAttributeData.removeAllAddonData(stack);
        }

        // No perfect match found — use best candidate with forced rarity
        // For maxRarity: prefer a candidate that was already at or below max
        if (useMax && bestMaxModifiers != null) {
            GunAttributeData.setModifiers(stack, bestMaxModifiers);
            if (CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) {
                WeaponTypeHandler.applyFixedAttributes(stack);
            }
            GunAttributeData.setScore(stack, bestMaxScore);
            int forced = useMin ? Math.max(bestMaxRarityOrdinal, minRarity) : bestMaxRarityOrdinal;
            GunAttributeData.setRarityOrdinal(stack, forced);
        } else {
            GunAttributeData.setModifiers(stack, bestModifiers);
            if (CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) {
                WeaponTypeHandler.applyFixedAttributes(stack);
            }
            GunAttributeData.setScore(stack, bestScore);
            int forcedRarity;
            if (useTarget) {
                forcedRarity = targetRarity;
            } else if (useMin) {
                forcedRarity = Math.max(bestRarityOrdinal, minRarity);
            } else {
                forcedRarity = Math.min(bestRarityOrdinal, maxRarity);
            }
            GunAttributeData.setRarityOrdinal(stack, forcedRarity);
        }
    }

    /**
     * Standard generation without any rarity constraint.
     */
    public static void generateStandard(ItemStack stack, RandomSource random) {
        CommonConfig.FixedAttributeMode fixedMode = CommonConfig.FIXED_ATTRIBUTE_MODE.get();
        boolean applyRandom = fixedMode != CommonConfig.FixedAttributeMode.FIXED_ONLY;

        if (applyRandom) {
            List<GunModifier> modifiers = AttributeGenerator.generate(stack, random);
            GunAttributeData.setModifiers(stack, modifiers);
        }

        if (CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) {
            WeaponTypeHandler.applyFixedAttributes(stack);
        }

        if (CommonConfig.ENABLE_RARITY_SCORING.get()) {
            RarityHandler.calculateAndApplyRarity(stack);
        }
    }

    private static boolean matchesConstraint(int rarityOrdinal, int targetRarity, int minRarity, int maxRarity,
                                             boolean useTarget, boolean useMin, boolean useMax) {
        if (useTarget) {
            return rarityOrdinal == targetRarity;
        }
        if (useMin && rarityOrdinal < minRarity) return false;
        if (useMax && rarityOrdinal > maxRarity) return false;
        return true;
    }

    private static boolean isCloserToTarget(int candidateOrdinal, int candidateScore,
                                            int bestOrdinal, int bestScore, int targetRarity) {
        int candidateDist = Math.abs(candidateOrdinal - targetRarity);
        int bestDist = Math.abs(bestOrdinal - targetRarity);
        if (candidateDist != bestDist) {
            return candidateDist < bestDist;
        }
        if (candidateOrdinal <= targetRarity) {
            return candidateScore > bestScore;
        } else {
            return candidateScore < bestScore;
        }
    }
}
