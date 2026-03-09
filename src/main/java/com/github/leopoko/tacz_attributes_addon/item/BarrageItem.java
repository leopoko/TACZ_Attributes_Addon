package com.github.leopoko.tacz_attributes_addon.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * A special item "???" that doubles gun RPM when held in the offhand.
 * Crafted from a Vector surrounded by enchanted golden apples.
 */
public class BarrageItem extends Item {

    private static final UUID RPM_MODIFIER_UUID =
            UUID.nameUUIDFromBytes("tacz_addon_barrage_rpm".getBytes());
    private static final UUID DRAW_MODIFIER_UUID =
            UUID.nameUUIDFromBytes("tacz_addon_barrage_draw".getBytes());
    private static final UUID ADS_MODIFIER_UUID =
            UUID.nameUUIDFromBytes("tacz_addon_barrage_ads".getBytes());
    private static final UUID reload_MODIFIER_UUID =
            UUID.nameUUIDFromBytes("tacz_addon_barrage_reload".getBytes());

    // Lazily built modifier map (attribute may not be registered at class load time)
    private Multimap<Attribute, AttributeModifier> cachedModifiers;

    public BarrageItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.OFFHAND) {
            if (cachedModifiers == null) {
                cachedModifiers = buildModifiers();
            }
            if (cachedModifiers != null) {
                return cachedModifiers;
            }
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    @Nullable
    private Multimap<Attribute, AttributeModifier> buildModifiers() {
        Attribute rpmAttr = ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("tacz_attributes", "rpm_multiplier"));
        if (rpmAttr == null) {
            return null; // TACZ Attributes not loaded yet

        }
        Attribute drawAttr = ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("tacz_attributes", "draw_speed"));
        if (drawAttr == null) {
            return null; // TACZ Attributes not loaded yet
        }
        Attribute adsAttr = ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("tacz_attributes", "ads_speed"));
        if (adsAttr == null) {
            return null; // TACZ Attributes not loaded yet
        }
        Attribute reloadAttr = ForgeRegistries.ATTRIBUTES.getValue(
                new ResourceLocation("tacz_attributes", "reload_speed"));
        if (reloadAttr == null) {
            return null; // TACZ Attributes not loaded yet
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(rpmAttr, new AttributeModifier(
                RPM_MODIFIER_UUID,
                "Barrage RPM boost",
                1.0, // +100% → doubles the RPM (base 1.0 × (1 + 1.0) = 2.0)
                AttributeModifier.Operation.MULTIPLY_BASE
        ));
        builder.put(drawAttr, new AttributeModifier(
                DRAW_MODIFIER_UUID,
                "Barrage draw speed boost",
                0.5, // +50% → 1.5× draw speed (base 1.0 × (1 + 0.5) = 1.5)
                AttributeModifier.Operation.MULTIPLY_BASE
        ));
        builder.put(adsAttr, new AttributeModifier(
                ADS_MODIFIER_UUID,
                "Barrage ADS speed boost",
                0.5, // +50% → 1.5× ADS speed (base 1.0 × (1 + 0.5) = 1.5)
                AttributeModifier.Operation.MULTIPLY_BASE
        ));
        builder.put(reloadAttr, new AttributeModifier(
                reload_MODIFIER_UUID,
                "Barrage reload speed penalty",
                1.0,
                AttributeModifier.Operation.MULTIPLY_BASE
        ));
        return builder.build();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Always show enchantment glint
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.tacz_attributes_addon.barrage.desc")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
    }
}
