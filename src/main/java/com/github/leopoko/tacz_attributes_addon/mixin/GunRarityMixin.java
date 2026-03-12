package com.github.leopoko.tacz_attributes_addon.mixin;

import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeData;
import com.tacz.guns.api.item.IGun;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to override item rarity for gun items based on addon attribute scoring.
 *
 * In 1.21.1, getRarity() is on ItemStack, not Item.
 * We inject into ItemStack.getRarity() and override for TACZ guns with addon data.
 */
@Mixin(ItemStack.class)
public class GunRarityMixin {

    @Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
    private void taczAddon$overrideGunRarity(CallbackInfoReturnable<Rarity> cir) {
        ItemStack self = (ItemStack) (Object) this;

        // Only affect gun items
        if (IGun.getIGunOrNull(self) == null) return;
        if (!CommonConfig.ENABLE_RARITY_SCORING.get()) return;
        if (!GunAttributeData.hasAddonData(self)) return;

        cir.setReturnValue(GunAttributeData.getRarity(self));
    }
}
