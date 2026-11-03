package com.github.leopoko.tacz_attributes_addon.compat.apotheosis;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.mojang.logging.LogUtils;
import com.tacz.guns.api.item.IGun;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

/**
 * Registers a custom LootCategory for TACZ guns so Apotheosis
 * recognizes them as socketable items.
 *
 * In Apotheosis 1.21.1:
 * - LootCategory is a synced NeoForge registry ({@code apotheosis:loot_category})
 * - Constructor: LootCategory(Predicate, EntitySlotGroup, int)
 * - LootCategory.forItem() iterates a sorted list that Apotheosis rebuilds in the
 *   registry's bake callback, so a normally registered entry is picked up automatically.
 *
 * The category is registered through a DeferredRegister during RegisterEvent, i.e. before
 * the registry is frozen. Earlier versions unfroze the registry with reflection during
 * common setup and inserted the entry afterwards. That entry was missing from NeoForge's
 * startup snapshot, so when a client joined a dedicated server and the server's registry
 * IDs were applied, the client's LootCategory IDs no longer matched the server's. Affix
 * data is synced by category ID, so every affix (not just gun ones) broke on dedicated
 * servers while singleplayer worked fine.
 */
public class GunLootCategory {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceKey<Registry<LootCategory>> LOOT_CATEGORY_REGISTRY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("apotheosis", "loot_category"));

    private static final DeferredRegister<LootCategory> LOOT_CATEGORIES =
            DeferredRegister.create(LOOT_CATEGORY_REGISTRY, TaczAttributesAddon.MODID);

    /** Set when the registry entry is created (RegisterEvent). Compared by reference in LootControllerMixin. */
    public static LootCategory GUN;

    public static final DeferredHolder<LootCategory, LootCategory> GUN_HOLDER = LOOT_CATEGORIES.register("gun", () -> {
        GUN = new LootCategory(
                GunLootCategory::isGun,
                ALObjects.EquipmentSlotGroups.MAINHAND,
                100 // priority (lower = checked later, after standard categories)
        );
        return GUN;
    });

    /**
     * Must be called from the mod constructor, and only when Apotheosis is loaded.
     * The config is not available yet at that point, so the config switch is checked
     * at match time instead (see {@link #isGun}).
     */
    public static void registerDeferred(IEventBus modEventBus) {
        LOOT_CATEGORIES.register(modEventBus);
    }

    private static boolean isGun(ItemStack stack) {
        if (IGun.getIGunOrNull(stack) == null) return false;
        try {
            return CommonConfig.ENABLE_APOTHEOSIS.get();
        } catch (IllegalStateException e) {
            // Config not loaded yet
            return false;
        }
    }

    public static boolean isRegistered() {
        boolean bound = GUN_HOLDER.isBound();
        if (!bound) {
            LOGGER.warn("GUN LootCategory is not bound in the Apotheosis registry");
        }
        return bound;
    }
}
