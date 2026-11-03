package com.github.leopoko.tacz_attributes_addon.mixin;

import com.github.leopoko.tacz_attributes_addon.compat.apotheosis.GunLootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Lets this addon's gun gems (gem_class "gun") work on guns that Apotheosis Modern Ragnarok
 * classifies into its own categories (full_auto, semi_auto, shotgun, bolt_action).
 *
 * Every gem check in Apotheosis 1.20.1 (socketing, validity, modifiers, tooltips) goes through
 * Gem#getBonus(LootCategory, LootRarity). When a gem has no bonus for an ApoMR gun category,
 * fall back to its bonus for our GUN category. Gems that do define a bonus for the ApoMR
 * category are left untouched.
 *
 * remap = false: Gem is an Apotheosis class. defaultRequire = 0 keeps this optional.
 */
@Mixin(value = Gem.class, remap = false)
public abstract class GemMixin {

    @Inject(method = "getBonus", at = @At("RETURN"), cancellable = true, remap = false)
    private void taczAddon$fallbackToGunBonus(LootCategory category, LootRarity rarity,
                                              CallbackInfoReturnable<Optional<GemBonus>> cir) {
        if (cir.getReturnValue().isPresent()) return;
        LootCategory gun = GunLootCategory.GUN;
        if (gun == null || !GunLootCategory.isForeignGunCategory(category)) return;
        cir.setReturnValue(((Gem) (Object) this).getBonus(gun, rarity));
    }

    /**
     * Socketing check (Gem#canApplyTo -> isValidIn) reads bonusMap directly instead of getBonus,
     * so it needs the same fallback or the gem could not be put into an ApoMR-classified gun.
     */
    @Inject(method = "isValidIn", at = @At("RETURN"), cancellable = true, remap = false)
    private void taczAddon$allowGunGemInForeignGunCategory(ItemStack socketed, ItemStack gem, LootRarity rarity,
                                                           CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) return;
        LootCategory gun = GunLootCategory.GUN;
        if (gun == null || !GunLootCategory.isForeignGunCategory(LootCategory.forItem(socketed))) return;
        if (((Gem) (Object) this).getBonus(gun, rarity).isPresent()) {
            cir.setReturnValue(true);
        }
    }
}
