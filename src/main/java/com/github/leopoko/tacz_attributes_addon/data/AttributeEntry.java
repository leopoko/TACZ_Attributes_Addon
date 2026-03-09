package com.github.leopoko.tacz_attributes_addon.data;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Set;

/**
 * Defines a single attribute available in the random generation pool.
 * Loaded from config JSON, or uses built-in defaults.
 */
public class AttributeEntry {
    private final String attributeId;
    private final double minValue;
    private final double maxValue;
    private final AttributeModifier.Operation operation;
    private final int weight;
    private final int rarityTier; // 1=common, 2=uncommon, 3=rare, 4=epic
    private final Set<String> applicableGunTypes; // empty = all types
    private final double buffThreshold; // values above this are buffs
    private final double scoreWeight; // score contribution per unit
    private final String linkedAttribute; // attribute that must be paired (e.g., chance needs amount)

    public AttributeEntry(String attributeId, double minValue, double maxValue,
                          AttributeModifier.Operation operation, int weight, int rarityTier,
                          Set<String> applicableGunTypes, double buffThreshold, double scoreWeight) {
        this(attributeId, minValue, maxValue, operation, weight, rarityTier,
                applicableGunTypes, buffThreshold, scoreWeight, null);
    }

    public AttributeEntry(String attributeId, double minValue, double maxValue,
                          AttributeModifier.Operation operation, int weight, int rarityTier,
                          Set<String> applicableGunTypes, double buffThreshold, double scoreWeight,
                          String linkedAttribute) {
        this.attributeId = attributeId;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.operation = operation;
        this.weight = weight;
        this.rarityTier = rarityTier;
        this.applicableGunTypes = applicableGunTypes;
        this.buffThreshold = buffThreshold;
        this.scoreWeight = scoreWeight;
        this.linkedAttribute = linkedAttribute;
    }

    public String getAttributeId() { return attributeId; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    public AttributeModifier.Operation getOperation() { return operation; }
    public int getWeight() { return weight; }
    public int getRarityTier() { return rarityTier; }
    public Set<String> getApplicableGunTypes() { return applicableGunTypes; }
    public double getBuffThreshold() { return buffThreshold; }
    public double getScoreWeight() { return scoreWeight; }
    public String getLinkedAttribute() { return linkedAttribute; }
    public boolean hasLinkedAttribute() { return linkedAttribute != null && !linkedAttribute.isEmpty(); }

    public boolean isApplicableTo(String gunType) {
        return applicableGunTypes.isEmpty() || applicableGunTypes.contains(gunType);
    }

    /**
     * Check if a generated value is a buff.
     */
    public boolean isValueBuff(double value) {
        return value > buffThreshold;
    }
}
