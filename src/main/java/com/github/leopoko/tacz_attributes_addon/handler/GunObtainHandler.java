package com.github.leopoko.tacz_attributes_addon.handler;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.github.leopoko.tacz_attributes_addon.random.RarityConstraintHelper;
import com.tacz.guns.api.item.IGun;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Feature 1: Automatically applies random attributes to guns when obtained.
 * Scans player inventory each tick for guns without addon data.
 */
@Mod.EventBusSubscriber(modid = TaczAttributesAddon.MODID)
public class GunObtainHandler {

    // Only check every N ticks to reduce overhead
    private static final int CHECK_INTERVAL = 10;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!CommonConfig.ENABLE_RANDOM_ON_OBTAIN.get()) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        // Use player's own tick count for multiplayer-safe interval checking
        if (player.tickCount % CHECK_INTERVAL != 0) return;

        Inventory inv = player.getInventory();
        RandomSource random = player.getRandom();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            IGun iGun = IGun.getIGunOrNull(stack);
            if (iGun == null) continue;

            // Skip if already has addon data
            if (GunAttributeData.hasAddonData(stack)) continue;

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

            // Mark as sealed
            GunAttributeData.setSealed(stack, true);
        }
    }
}
