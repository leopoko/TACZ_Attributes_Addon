package com.github.leopoko.tacz_attributes_addon.init;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.block.AttributeStationMenu;
import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TaczAttributesAddon.MODID);

    public static final RegistryObject<MenuType<AttributeStationMenu>> ATTRIBUTE_STATION =
            MENU_TYPES.register("attribute_station",
                    () -> IForgeMenuType.create(AttributeStationMenu::new));

    public static final RegistryObject<MenuType<EnhancementStationMenu>> ENHANCEMENT_STATION =
            MENU_TYPES.register("enhancement_station",
                    () -> IForgeMenuType.create(EnhancementStationMenu::new));
}
