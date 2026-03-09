package com.github.leopoko.tacz_attributes_addon.bridge;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.data.GunModifier;
import com.tacz.guns.api.item.IGun;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

/**
 * Bridges item-level gun attributes to player entity attributes.
 * When a player holds a gun with addon attributes, the modifiers are applied
 * to the player's TACZ Attributes as AttributeModifiers. When the player
 * switches to a different gun or unequips, the modifiers are removed.
 */
@Mod.EventBusSubscriber(modid = TaczAttributesAddon.MODID)
public class AttributeBridge {

    // Track the last held gun's identity per player to detect changes
    private static final WeakHashMap<Player, GunIdentity> LAST_HELD = new WeakHashMap<>();

    // Set of all modifier UUIDs we've applied, per player
    private static final WeakHashMap<Player, Set<UUID>> APPLIED_UUIDS = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!(event.player instanceof ServerPlayer player)) return;

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

        LAST_HELD.put(player, currentIdentity);
    }

    private static void applyModifiers(ServerPlayer player, List<GunModifier> modifiers) {
        Set<UUID> uuids = new HashSet<>();

        for (int i = 0; i < modifiers.size(); i++) {
            GunModifier mod = modifiers.get(i);
            ResourceLocation attrId = new ResourceLocation(mod.getAttributeId());
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attrId);
            if (attribute == null) continue;

            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            // Use index-based UUID so the same attributeId can appear multiple times
            // (e.g., once from FixedModifiers and once from Modifiers) and both apply
            UUID uuid = generateUUID(mod.getAttributeId(), i);
            AttributeModifier modifier = new AttributeModifier(
                    uuid, "tacz_addon_gun_modifier", mod.getValue(), mod.getOperation()
            );

            // Remove existing first to prevent stacking
            instance.removeModifier(uuid);
            instance.addTransientModifier(modifier);
            uuids.add(uuid);
        }

        APPLIED_UUIDS.put(player, uuids);
    }

    private static void removeAllModifiers(Player player) {
        Set<UUID> uuids = APPLIED_UUIDS.remove(player);
        if (uuids == null || uuids.isEmpty()) return;

        for (UUID uuid : uuids) {
            // Remove from all attributes - iterate all registered attributes
            for (Attribute attr : ForgeRegistries.ATTRIBUTES.getValues()) {
                AttributeInstance instance = player.getAttribute(attr);
                if (instance != null) {
                    instance.removeModifier(uuid);
                }
            }
        }
    }

    /**
     * Generate a deterministic UUID for an attribute modifier.
     * Uses index to ensure uniqueness when the same attributeId appears multiple times
     * (e.g., in both FixedModifiers and random Modifiers).
     */
    private static UUID generateUUID(String attributeId, int index) {
        return UUID.nameUUIDFromBytes(("tacz_addon_" + index + "_" + attributeId).getBytes());
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

            // Use the stack's NBT hash + addon data hash for identity
            int hash = Objects.hash(
                    stack.getTag() != null ? stack.getTag().hashCode() : 0
            );
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
