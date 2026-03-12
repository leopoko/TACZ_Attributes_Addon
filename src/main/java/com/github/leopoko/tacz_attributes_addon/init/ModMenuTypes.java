package com.github.leopoko.tacz_attributes_addon.init;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.block.AttributeStationMenu;
import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, TaczAttributesAddon.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AttributeStationMenu>> ATTRIBUTE_STATION =
            MENU_TYPES.register("attribute_station",
                    () -> IMenuTypeExtension.create(AttributeStationMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<EnhancementStationMenu>> ENHANCEMENT_STATION =
            MENU_TYPES.register("enhancement_station",
                    () -> IMenuTypeExtension.create(EnhancementStationMenu::new));
}
