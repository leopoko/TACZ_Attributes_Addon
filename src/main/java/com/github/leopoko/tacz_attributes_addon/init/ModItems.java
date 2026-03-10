package com.github.leopoko.tacz_attributes_addon.init;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.item.BarrageItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TaczAttributesAddon.MODID);

    public static final RegistryObject<Item> ATTRIBUTE_STATION = ITEMS.register("attribute_station",
            () -> new BlockItem(ModBlocks.ATTRIBUTE_STATION.get(), new Item.Properties()));

    public static final RegistryObject<Item> ENHANCEMENT_STATION = ITEMS.register("enhancement_station",
            () -> new BlockItem(ModBlocks.ENHANCEMENT_STATION.get(), new Item.Properties()));

    public static final RegistryObject<Item> BARRAGE = ITEMS.register("barrage",
            BarrageItem::new);
}
