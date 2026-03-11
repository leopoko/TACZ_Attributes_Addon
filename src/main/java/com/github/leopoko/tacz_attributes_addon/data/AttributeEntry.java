package com.github.leopoko.tacz_attributes_addon.data;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collections;
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
    private final Set<String> weaponWhitelist; // specific weapon IDs to additionally allow (e.g., "tacz:ak47")
    private final Set<String> weaponBlacklist; // specific weapon IDs to exclude (takes priority)

    public AttributeEntry(String attributeId, double minValue, double maxValue,
                          AttributeModifier.Operation operation, int weight, int rarityTier,
                          Set<String> applicableGunTypes, double buffThreshold, double scoreWeight) {
        this(attributeId, minValue, maxValue, operation, weight, rarityTier,
                applicableGunTypes, buffThreshold, scoreWeight, null,
                Collections.emptySet(), Collections.emptySet());
    }

    public AttributeEntry(String attributeId, double minValue, double maxValue,
                          AttributeModifier.Operation operation, int weight, int rarityTier,
                          Set<String> applicableGunTypes, double buffThreshold, double scoreWeight,
                          String linkedAttribute) {
        this(attributeId, minValue, maxValue, operation, weight, rarityTier,
                applicableGunTypes, buffThreshold, scoreWeight, linkedAttribute,
                Collections.emptySet(), Collections.emptySet());
    }

    public AttributeEntry(String attributeId, double minValue, double maxValue,
                          AttributeModifier.Operation operation, int weight, int rarityTier,
                          Set<String> applicableGunTypes, double buffThreshold, double scoreWeight,
                          String linkedAttribute, Set<String> weaponWhitelist, Set<String> weaponBlacklist) {
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
        this.weaponWhitelist = weaponWhitelist;
        this.weaponBlacklist = weaponBlacklist;
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
    public Set<String> getWeaponWhitelist() { return weaponWhitelist; }
    public Set<String> getWeaponBlacklist() { return weaponBlacklist; }

    public boolean isApplicableTo(String gunType) {
        return applicableGunTypes.isEmpty() || applicableGunTypes.contains(gunType);
    }

    /**
     * Check if this attribute is applicable to a specific weapon, considering
     * blacklist, whitelist, and gun type filters.
     *
     * Priority order:
     * 1. weaponBlacklist: if gunId is listed, always exclude
     * 2. weaponWhitelist: if gunId is listed, always allow (regardless of gun type)
     * 3. applicableGunTypes: standard gun type check (empty = all types)
     */
    public boolean isApplicable(String gunType, String gunId) {
        if (gunId != null && !weaponBlacklist.isEmpty() && weaponBlacklist.contains(gunId)) {
            return false;
        }
        if (gunId != null && !weaponWhitelist.isEmpty() && weaponWhitelist.contains(gunId)) {
            return true;
        }
        if (gunType == null) {
            return applicableGunTypes.isEmpty();
        }
        return isApplicableTo(gunType);
    }

    /**
     * Check if a generated value is a buff.
     */
    public boolean isValueBuff(double value) {
        return value > buffThreshold;
    }
}
