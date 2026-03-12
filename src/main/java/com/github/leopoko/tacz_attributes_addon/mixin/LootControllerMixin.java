package com.github.leopoko.tacz_attributes_addon.mixin;

import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Safety net for TACZ gun items in Apotheosis's affix generation system.
 *
 * Gun affixes ARE defined in our datapack, so normal affix generation works.
 * However, if affix generation fails (e.g. datapack reload, missing affix types),
 * Apotheosis throws a RuntimeException that crashes the game.
 *
 * This mixin injects right before the RuntimeException is created (the error
 * path when no affixes are found). For gun items, it returns the item unchanged
 * instead of crashing. Non-gun items are not affected.
 *
 * Uses remap = false because LootController is a modded class (Apotheosis).
 * defaultRequire = 0 in mixin config ensures this is silently skipped
 * when Apotheosis is not installed.
 */
@Mixin(value = LootController.class, remap = false)
public class LootControllerMixin {

    /**
     * Injects at the point where RuntimeException is about to be thrown
     * due to empty affix list. Only cancels for the "gun" category.
     */
    @Inject(
            method = "createLootItem(Lnet/minecraft/world/item/ItemStack;Ldev/shadowsoffire/apotheosis/loot/LootCategory;Ldev/shadowsoffire/apotheosis/loot/LootRarity;Ldev/shadowsoffire/apotheosis/tiers/GenContext;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(
                    value = "NEW",
                    target = "java/lang/RuntimeException"
            ),
            cancellable = true,
            remap = false
    )
    private static void taczAddon$preventGunAffixCrash(ItemStack stack, LootCategory cat, LootRarity rarity,
                                                        GenContext ctx, CallbackInfoReturnable<ItemStack> cir) {
        // Compare by reference to our registered GUN category
        if (cat == com.github.leopoko.tacz_attributes_addon.compat.apotheosis.GunLootCategory.GUN) {
            cir.setReturnValue(stack);
        }
    }
}
