package com.github.leopoko.tacz_attributes_addon.bridge;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.tacz.guns.api.item.IGun;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;

/**
 * Bridges item-level gun attributes to player entity attributes.
 * When a player holds a gun with addon attributes, the modifiers are applied
 * to the player's TACZ Attributes as AttributeModifiers. When the player
 * switches to a different gun or unequips, the modifiers are removed.
 *
 * Also supports Apotheosis gem modifiers via a pluggable GemModifierSupplier.
 */
@EventBusSubscriber(modid = TaczAttributesAddon.MODID)
public class AttributeBridge {

    /**
     * Represents an attribute modifier pair extracted from an external source (e.g., Apotheosis gems).
     */
    public record AttributeModifierEntry(Holder<Attribute> attribute, AttributeModifier modifier) {}

    /**
     * Supplier for gem-based attribute modifiers. Set by Apotheosis compat layer.
     */
    @FunctionalInterface
    public interface GemModifierSupplier {
        List<AttributeModifierEntry> getGemModifiers(ItemStack stack);
    }

    // Track the last held gun's identity per player to detect changes
    private static final WeakHashMap<Player, GunIdentity> LAST_HELD = new WeakHashMap<>();

    // Set of all modifier ResourceLocations we've applied, per player
    private static final WeakHashMap<Player, Set<ResourceLocation>> APPLIED_IDS = new WeakHashMap<>();

    // Optional gem modifier supplier (set by ApotheosisCompat when loaded)
    private static GemModifierSupplier gemModifierSupplier = null;

    /**
     * Register a gem modifier supplier for Apotheosis integration.
     */
    public static void setGemModifierSupplier(GemModifierSupplier supplier) {
        gemModifierSupplier = supplier;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack held = player.getMainHandItem();
        GunIdentity currentIdentity = GunIdentity.of(held);
        GunIdentity lastIdentity = LAST_HELD.get(player);

        // Check if held gun changed
        if (Objects.equals(currentIdentity, lastIdentity)) return;

        // Remove old modifiers
        removeAllModifiers(player);

        // Apply new modifiers if holding a gun with addon data
        if (currentIdentity != null && GunAttributeData.hasAddonData(held)) {
            List<GunModifier> modifiers = GunAttributeData.getAllModifiers(held);
            applyModifiers(player, modifiers);
        }

        // Apply gem modifiers if Apotheosis integration is active
        if (currentIdentity != null && gemModifierSupplier != null) {
            applyGemModifiers(player, held);
        }

        LAST_HELD.put(player, currentIdentity);
    }

    private static void applyModifiers(ServerPlayer player, List<GunModifier> modifiers) {
        Set<ResourceLocation> ids = new HashSet<>();

        for (int i = 0; i < modifiers.size(); i++) {
            GunModifier mod = modifiers.get(i);
            ResourceLocation attrId = ResourceLocation.parse(mod.getAttributeId());
            Optional<Holder.Reference<Attribute>> holderOpt = BuiltInRegistries.ATTRIBUTE.getHolder(attrId);
            if (holderOpt.isEmpty()) continue;

            Holder<Attribute> holder = holderOpt.get();
            AttributeInstance instance = player.getAttribute(holder);
            if (instance == null) continue;

            // Use index-based ResourceLocation so the same attributeId can appear multiple times
            ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(
                    TaczAttributesAddon.MODID, "gun_modifier_" + i + "_" + attrId.getPath());
            AttributeModifier modifier = new AttributeModifier(
                    modifierId, mod.getValue(), mod.getOperation()
            );

            // Remove existing first to prevent stacking
            instance.removeModifier(modifierId);
            instance.addTransientModifier(modifier);
            ids.add(modifierId);
        }

        APPLIED_IDS.put(player, ids);
    }

    private static void applyGemModifiers(ServerPlayer player, ItemStack held) {
        List<AttributeModifierEntry> gemMods = gemModifierSupplier.getGemModifiers(held);
        if (gemMods.isEmpty()) return;

        Set<ResourceLocation> ids = APPLIED_IDS.computeIfAbsent(player, k -> new HashSet<>());

        for (AttributeModifierEntry entry : gemMods) {
            AttributeInstance instance = player.getAttribute(entry.attribute());
            if (instance == null) continue;

            ResourceLocation id = entry.modifier().id();
            // Remove existing first to prevent stacking
            instance.removeModifier(id);
            instance.addTransientModifier(entry.modifier());
            ids.add(id);
        }
    }

    private static void removeAllModifiers(Player player) {
        Set<ResourceLocation> ids = APPLIED_IDS.remove(player);
        if (ids == null || ids.isEmpty()) return;

        for (ResourceLocation id : ids) {
            // Remove from all attributes - iterate all registered attributes
            for (Holder<Attribute> holder : BuiltInRegistries.ATTRIBUTE.holders().toList()) {
                AttributeInstance instance = player.getAttribute(holder);
                if (instance != null) {
                    instance.removeModifier(id);
                }
            }
        }
    }

    /**
     * Force refresh modifiers for a player (e.g., after gun attributes change).
     */
    public static void refreshPlayer(ServerPlayer player) {
        LAST_HELD.remove(player);
    }

    /**
     * Identity of a held gun, used to detect changes.
     */
    private static class GunIdentity {
        private final int hashCode;

        private GunIdentity(int hashCode) {
            this.hashCode = hashCode;
        }

        static GunIdentity of(ItemStack stack) {
            if (stack.isEmpty()) return null;
            IGun iGun = IGun.getIGunOrNull(stack);
            if (iGun == null) return null;

            // Use the stack's custom data hash for identity
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            int hash = customData.copyTag().hashCode();
            return new GunIdentity(hash);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return hashCode == ((GunIdentity) o).hashCode;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
