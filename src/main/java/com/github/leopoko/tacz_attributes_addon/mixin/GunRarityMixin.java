package com.github.leopoko.tacz_attributes_addon.mixin;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.tacz.guns.api.item.IGun;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to override item rarity for gun items based on addon attribute scoring.
 *
 * Targets Item.class (where getRarity is declared) instead of
 * ModernKineticGunItem (which only inherits it). This ensures the injection
 * works correctly in both dev and production environments with proper remapping.
 *
 * The IGun check ensures only TACZ gun items are affected.
 */
@Mixin(Item.class)
public class GunRarityMixin {

    @Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
    private void taczAddon$overrideGunRarity(ItemStack stack, CallbackInfoReturnable<Rarity> cir) {
        // Only affect gun items
        if (IGun.getIGunOrNull(stack) == null) return;
        if (!CommonConfig.ENABLE_RARITY_SCORING.get()) return;
        if (!GunAttributeData.hasAddonData(stack)) return;

        cir.setReturnValue(GunAttributeData.getRarity(stack));
    }
}
