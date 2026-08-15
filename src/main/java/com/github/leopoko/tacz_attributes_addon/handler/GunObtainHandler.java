package com.github.leopoko.tacz_attributes_addon.handler;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.compat.apotheosis.ApotheosisCompat;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.random.RarityConstraintHelper;
import com.tacz.guns.api.item.IGun;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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

        Inventory inv = player.getInventory();
        RandomSource random = player.getRandom();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            IGun iGun = IGun.getIGunOrNull(stack);
            if (iGun == null) continue;

            // Skip if already has addon data (but migrate sockets for existing guns)
            if (GunAttributeData.hasAddonData(stack)) {
                if (CommonConfig.ENABLE_APOTHEOSIS.get()) {
                    ApotheosisCompat.applyInitialSocketsIfMissing(stack);
                }
                continue;
            }

            boolean hasPreset = GunAttributeData.hasPreset(stack);

            if (hasPreset) {
                // Read and remove preset before generation
                int targetRarity = GunAttributeData.getPresetTargetRarity(stack);
                int minRarity = GunAttributeData.getPresetMinRarity(stack);
                GunAttributeData.removePreset(stack);

                RarityConstraintHelper.generateWithConstraint(stack, random, targetRarity, minRarity, -1);
            } else {
                // Standard generation (no preset)
                RarityConstraintHelper.generateStandard(stack, random);
            }

            // Set initial Apotheosis sockets (fixed at obtain time, expandable via Sigil of Socketing)
            if (CommonConfig.ENABLE_APOTHEOSIS.get()) {
                ApotheosisCompat.applyInitialSockets(stack);
            }

            // Mark as sealed
            GunAttributeData.setSealed(stack, true);
        }
    }
}
