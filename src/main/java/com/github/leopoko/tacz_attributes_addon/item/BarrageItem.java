package com.github.leopoko.tacz_attributes_addon.item;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Optional;

/**
 * A special item "???" that doubles gun RPM when held in the offhand.
 * Crafted from a Vector surrounded by enchanted golden apples.
 */
public class BarrageItem extends Item {

    // Lazily built modifier map (attribute may not be registered at class load time)
    private ItemAttributeModifiers cachedModifiers;

    public BarrageItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers() {
        if (cachedModifiers == null) {
            cachedModifiers = buildModifiers();
        }
        if (cachedModifiers != null) {
            return cachedModifiers;
        }
        return super.getDefaultAttributeModifiers();
    }

    private ItemAttributeModifiers buildModifiers() {
        Optional<Holder.Reference<Attribute>> rpmOpt = BuiltInRegistries.ATTRIBUTE.getHolder(
                ResourceLocation.fromNamespaceAndPath("tacz_attributes", "rpm_multiplier"));
        Optional<Holder.Reference<Attribute>> drawOpt = BuiltInRegistries.ATTRIBUTE.getHolder(
                ResourceLocation.fromNamespaceAndPath("tacz_attributes", "draw_speed"));
        Optional<Holder.Reference<Attribute>> adsOpt = BuiltInRegistries.ATTRIBUTE.getHolder(
                ResourceLocation.fromNamespaceAndPath("tacz_attributes", "ads_speed"));
        Optional<Holder.Reference<Attribute>> reloadOpt = BuiltInRegistries.ATTRIBUTE.getHolder(
                ResourceLocation.fromNamespaceAndPath("tacz_attributes", "reload_speed"));

        if (rpmOpt.isEmpty() || drawOpt.isEmpty() || adsOpt.isEmpty() || reloadOpt.isEmpty()) {
            return null; // TACZ Attributes not loaded yet
        }

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        builder.add(rpmOpt.get(), new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(TaczAttributesAddon.MODID, "barrage_rpm"),
                1.0, // +100% → doubles the RPM
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ), EquipmentSlotGroup.OFFHAND);

        builder.add(drawOpt.get(), new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(TaczAttributesAddon.MODID, "barrage_draw"),
                0.5, // +50% → 1.5× draw speed
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ), EquipmentSlotGroup.OFFHAND);

        builder.add(adsOpt.get(), new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(TaczAttributesAddon.MODID, "barrage_ads"),
                0.5, // +50% → 1.5× ADS speed
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ), EquipmentSlotGroup.OFFHAND);

        builder.add(reloadOpt.get(), new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath(TaczAttributesAddon.MODID, "barrage_reload"),
                1.0, // +100% reload speed
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ), EquipmentSlotGroup.OFFHAND);

        return builder.build();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Always show enchantment glint
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.tacz_attributes_addon.barrage.desc")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
    }
}
