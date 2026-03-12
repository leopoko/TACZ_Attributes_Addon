package com.github.leopoko.tacz_attributes_addon.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Represents a single attribute modifier stored on a gun item.
 */
public class GunModifier {
    private final String attributeId;
    private final double value;
    private final AttributeModifier.Operation operation;

    public GunModifier(String attributeId, double value, AttributeModifier.Operation operation) {
        this.attributeId = attributeId;
        this.value = value;
        this.operation = operation;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public double getValue() {
        return value;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Attr", attributeId);
        tag.putDouble("Val", value);
        tag.putInt("Op", operation.ordinal());
        return tag;
    }

    public static GunModifier fromNbt(CompoundTag tag) {
        String attr = tag.getString("Attr");
        double val = tag.getDouble("Val");
        int op = tag.getInt("Op");
        AttributeModifier.Operation[] ops = AttributeModifier.Operation.values();
        AttributeModifier.Operation operation = (op >= 0 && op < ops.length) ? ops[op] : AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        return new GunModifier(attr, val, operation);
    }

    /**
     * Check if this modifier is a buff (positive effect).
     * For multiplier operations (base 1.0), values > 0 are buffs for damage/speed attributes.
     * For recoil/inaccuracy, lower values are buffs (inverted).
     */
    public boolean isBuff(double buffThreshold) {
        return value > buffThreshold;
    }
}
