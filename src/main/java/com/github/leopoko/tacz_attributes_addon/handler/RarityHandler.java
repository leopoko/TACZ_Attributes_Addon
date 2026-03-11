package com.github.leopoko.tacz_attributes_addon.handler;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.AttributeEntry;
import com.github.leopoko.tacz_attributes_addon.data.AttributeRegistry;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeOverrides;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.github.leopoko.tacz_attributes_addon.random.GunTypeFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Feature 6: Scores gun attributes and determines rarity.
 */
public class RarityHandler {

    /**
     * Calculate score from all modifiers and set rarity on the item.
     * Resolves per-gun scoreWeight overrides from gun_attribute_overrides.json.
     */
    public static void calculateAndApplyRarity(ItemStack stack) {
        if (!CommonConfig.ENABLE_RARITY_SCORING.get()) return;

        // Resolve per-gun override for scoreWeight
        ResourceLocation gunId = GunTypeFilter.resolveGunId(stack);
        GunAttributeOverrides.GunOverride override = gunId != null
                ? GunAttributeOverrides.getOverride(gunId.toString()) : null;

        List<GunModifier> allMods = GunAttributeData.getAllModifiers(stack);
        int score = calculateScore(allMods, override);
        Rarity rarity = scoreToRarity(score);

        GunAttributeData.setScore(stack, score);
        GunAttributeData.setRarityOrdinal(stack, rarity.ordinal());
    }

    /**
     * Calculate a score based on the attribute modifiers.
     *
     * Formula: value * scoreWeight (NOT Math.abs!)
     *
     * For normal attributes (e.g., gun_damage, scoreWeight=+100):
     *   - value=+0.15 (buff)  → +0.15 * 100  = +15 (positive score)
     *   - value=-0.20 (debuff) → -0.20 * 100  = -20 (negative score)
     *
     * For inverted attributes (e.g., recoil, scoreWeight=-90):
     *   - value=-0.30 (buff/less recoil)  → -0.30 * -90 = +27 (positive score)
     *   - value=+0.20 (debuff/more recoil) → +0.20 * -90 = -18 (negative score)
     *
     * This correctly rewards buffs and penalizes debuffs for all attribute types.
     */
    public static int calculateScore(List<GunModifier> modifiers) {
        return calculateScore(modifiers, null);
    }

    public static int calculateScore(List<GunModifier> modifiers, GunAttributeOverrides.GunOverride override) {
        Map<String, AttributeEntry> entryMap = AttributeRegistry.getEntries().stream()
                .collect(Collectors.toMap(AttributeEntry::getAttributeId, e -> e, (a, b) -> a));

        double totalScore = 0;

        for (GunModifier mod : modifiers) {
            AttributeEntry entry = entryMap.get(mod.getAttributeId());
            if (entry == null) continue;

            // Use override scoreWeight if available, otherwise pool scoreWeight
            double scoreWeight = override != null
                    ? override.getScoreWeight(mod.getAttributeId(), entry.getScoreWeight())
                    : entry.getScoreWeight();
            double value = mod.getValue();

            // value * scoreWeight — sign naturally handles buff/debuff for both
            // normal and inverted attributes
            totalScore += value * scoreWeight;
        }

        return Math.max(0, (int) totalScore);
    }

    /**
     * Map a score to a vanilla Rarity tier.
     */
    public static Rarity scoreToRarity(int score) {
        int epicThreshold = CommonConfig.EPIC_THRESHOLD.get();
        int rareThreshold = CommonConfig.RARE_THRESHOLD.get();
        int uncommonThreshold = CommonConfig.UNCOMMON_THRESHOLD.get();

        if (score >= epicThreshold) return Rarity.EPIC;
        if (score >= rareThreshold) return Rarity.RARE;
        if (score >= uncommonThreshold) return Rarity.UNCOMMON;
        return Rarity.COMMON;
    }
}
