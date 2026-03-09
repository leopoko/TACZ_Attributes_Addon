package com.github.leopoko.tacz_attributes_addon.random;

import com.github.leopoko.tacz_attributes_addon.data.AttributeEntry;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filters attribute entries based on gun type and available fire modes.
 */
public class GunTypeFilter {

    /**
     * Resolve the gun type string (pistol, rifle, etc.) from an ItemStack.
     */
    @Nullable
    public static String resolveGunType(ItemStack stack) {
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return null;

        ResourceLocation gunId = iGun.getGunId(stack);
        if (gunId == null) return null;

        Optional<CommonGunIndex> index = TimelessAPI.getCommonGunIndex(gunId);
        return index.map(CommonGunIndex::getType).orElse(null);
    }

    /**
     * Get the gun ID (ResourceLocation) from an ItemStack.
     */
    @Nullable
    public static ResourceLocation resolveGunId(ItemStack stack) {
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return null;
        return iGun.getGunId(stack);
    }

    /**
     * Get the available fire modes for a gun.
     */
    public static Set<String> getAvailableFireModes(ItemStack stack) {
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) return Set.of();

        ResourceLocation gunId = iGun.getGunId(stack);
        if (gunId == null) return Set.of();

        Optional<CommonGunIndex> index = TimelessAPI.getCommonGunIndex(gunId);
        if (index.isEmpty()) return Set.of();

        return index.get().getGunData().getFireModeSet().stream()
                .map(fm -> fm.name().toLowerCase())
                .collect(Collectors.toSet());
    }

    /**
     * Filter attribute entries to only those applicable to the given gun.
     *
     * @param entries   All available entries
     * @param gunType   Gun type string (pistol, rifle, etc.)
     * @param fireModes Available fire modes for the specific gun
     * @param adaptive  If true, also filter out fire-mode-specific attributes for modes the gun doesn't have
     */
    public static List<AttributeEntry> filter(List<AttributeEntry> entries, @Nullable String gunType,
                                              Set<String> fireModes, boolean adaptive) {
        return entries.stream()
                .filter(entry -> {
                    // Filter by gun type
                    if (gunType != null && !entry.isApplicableTo(gunType)) {
                        return false;
                    }

                    // In adaptive mode, filter out fire-mode-specific attributes
                    if (adaptive && !fireModes.isEmpty()) {
                        String attrId = entry.getAttributeId();
                        if (attrId.contains("auto_") && !fireModes.contains("auto")) return false;
                        if (attrId.contains("semi_") && !fireModes.contains("semi")) return false;
                        if (attrId.contains("burst_") && !fireModes.contains("burst")) return false;
                        // bolt_action_speed only for guns with bolt action (sniper, shotgun typically)
                        if (attrId.contains("bolt_action") && !Set.of("sniper", "shotgun").contains(gunType)) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }
}
