package com.github.leopoko.tacz_attributes_addon.handler;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.github.leopoko.tacz_attributes_addon.random.AttributeGenerator;
import com.tacz.guns.api.item.IGun;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Feature 1: Automatically applies random attributes to guns when obtained.
 * Scans player inventory each tick for guns without addon data.
 */
@Mod.EventBusSubscriber(modid = TaczAttributesAddon.MODID)
public class GunObtainHandler {

    // Only check every N ticks to reduce overhead
    private static final int CHECK_INTERVAL = 10;
    // Maximum generation attempts when rarity preset is specified
    private static final int MAX_RARITY_ATTEMPTS = 50;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!CommonConfig.ENABLE_RANDOM_ON_OBTAIN.get()) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        // Use player's own tick count for multiplayer-safe interval checking
        if (player.tickCount % CHECK_INTERVAL != 0) return;

        // Check if fixed-only mode (skip random in that case)
        CommonConfig.FixedAttributeMode fixedMode = CommonConfig.FIXED_ATTRIBUTE_MODE.get();
        boolean applyRandom = fixedMode != CommonConfig.FixedAttributeMode.FIXED_ONLY;

        Inventory inv = player.getInventory();
        RandomSource random = player.getRandom();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            IGun iGun = IGun.getIGunOrNull(stack);
            if (iGun == null) continue;

            // Skip if already has addon data
            if (GunAttributeData.hasAddonData(stack)) continue;

            boolean hasPreset = GunAttributeData.hasPreset(stack);

            if (hasPreset && applyRandom) {
                generateWithRarityConstraint(stack, random);
            } else {
                // Standard generation (no preset)
                if (applyRandom) {
                    List<GunModifier> modifiers = AttributeGenerator.generate(stack, random);
                    GunAttributeData.setModifiers(stack, modifiers);
                }

                // Apply fixed attributes if enabled
                if (CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) {
                    WeaponTypeHandler.applyFixedAttributes(stack);
                }

                // Calculate rarity
                if (CommonConfig.ENABLE_RARITY_SCORING.get()) {
                    RarityHandler.calculateAndApplyRarity(stack);
                }
            }

            // Mark as sealed
            GunAttributeData.setSealed(stack, true);
        }
    }

    /**
     * Generate attributes with rarity constraint from TaczPreset NBT.
     * Tries up to MAX_RARITY_ATTEMPTS to find attributes matching the rarity requirement.
     * If no match is found, uses the best candidate and forces the rarity value.
     */
    private static void generateWithRarityConstraint(ItemStack stack, RandomSource random) {
        int targetRarity = GunAttributeData.getPresetTargetRarity(stack);
        int minRarity = GunAttributeData.getPresetMinRarity(stack);

        // TargetRarity takes precedence over MinRarity
        boolean useTarget = targetRarity >= 0 && targetRarity <= 3;
        boolean useMin = !useTarget && minRarity >= 0 && minRarity <= 3;

        // Remove preset tag before generation (it has been read)
        GunAttributeData.removePreset(stack);

        if (!useTarget && !useMin) {
            // Invalid preset values, fall back to standard generation
            List<GunModifier> modifiers = AttributeGenerator.generate(stack, random);
            GunAttributeData.setModifiers(stack, modifiers);
            if (CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) {
                WeaponTypeHandler.applyFixedAttributes(stack);
            }
            if (CommonConfig.ENABLE_RARITY_SCORING.get()) {
                RarityHandler.calculateAndApplyRarity(stack);
            }
            return;
        }

        List<GunModifier> bestModifiers = null;
        int bestScore = -1;
        int bestRarityOrdinal = 0;

        for (int attempt = 0; attempt < MAX_RARITY_ATTEMPTS; attempt++) {
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

            if (useTarget) {
                // Exact rarity match
                if (rarityOrdinal == targetRarity) {
                    applyFinalResult(stack, candidateModifiers, score, rarityOrdinal);
                    return;
                }
                // Track closest candidate (prefer matching or closest score)
                if (bestModifiers == null || isCloserToTarget(rarityOrdinal, score, bestRarityOrdinal, bestScore, targetRarity)) {
                    bestModifiers = candidateModifiers;
                    bestScore = score;
                    bestRarityOrdinal = rarityOrdinal;
                }
            } else {
                // Minimum rarity check
                if (rarityOrdinal >= minRarity) {
                    applyFinalResult(stack, candidateModifiers, score, rarityOrdinal);
                    return;
                }
                // Track highest scoring candidate
                if (score > bestScore) {
                    bestModifiers = candidateModifiers;
                    bestScore = score;
                    bestRarityOrdinal = rarityOrdinal;
                }
            }

            // Clean up temporary data for next iteration
            GunAttributeData.removeAllAddonData(stack);
        }

        // No perfect match found after all attempts — use best candidate with forced rarity
        GunAttributeData.setModifiers(stack, bestModifiers);
        if (CommonConfig.ENABLE_WEAPON_TYPE_ATTRIBUTES.get()) {
            WeaponTypeHandler.applyFixedAttributes(stack);
        }
        GunAttributeData.setScore(stack, bestScore);
        int forcedRarity = useTarget ? targetRarity : Math.max(bestRarityOrdinal, minRarity);
        GunAttributeData.setRarityOrdinal(stack, forcedRarity);
    }

    /**
     * Apply the final chosen modifiers and rarity to the item.
     */
    private static void applyFinalResult(ItemStack stack, List<GunModifier> modifiers, int score, int rarityOrdinal) {
        // Modifiers and fixed attributes are already applied from the last attempt
        GunAttributeData.setScore(stack, score);
        GunAttributeData.setRarityOrdinal(stack, rarityOrdinal);
    }

    /**
     * Determine if a candidate is closer to the target rarity than the current best.
     * Prefers exact ordinal match, then closer ordinal, then higher score within same ordinal.
     */
    private static boolean isCloserToTarget(int candidateOrdinal, int candidateScore,
                                            int bestOrdinal, int bestScore, int targetRarity) {
        int candidateDist = Math.abs(candidateOrdinal - targetRarity);
        int bestDist = Math.abs(bestOrdinal - targetRarity);
        if (candidateDist != bestDist) {
            return candidateDist < bestDist;
        }
        // Same distance — prefer higher score if target is above, lower if target is below
        if (candidateOrdinal <= targetRarity) {
            return candidateScore > bestScore;
        } else {
            return candidateScore < bestScore;
        }
    }
}
