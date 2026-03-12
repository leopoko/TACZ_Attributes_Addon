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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

/**
 * Feature 1: Automatically applies random attributes to guns when obtained.
 * Scans player inventory each tick for guns without addon data.
 */
@EventBusSubscriber(modid = TaczAttributesAddon.MODID)
public class GunObtainHandler {

    // Only check every N ticks to reduce overhead
    private static final int CHECK_INTERVAL = 10;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!CommonConfig.ENABLE_RANDOM_ON_OBTAIN.get()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

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

            // Apply attributes
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

            // Mark as sealed
            GunAttributeData.setSealed(stack, true);
        }
    }
}
