package com.github.leopoko.tacz_attributes_addon.init;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.item.BarrageItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, TaczAttributesAddon.MODID);

    public static final DeferredHolder<Item, Item> ATTRIBUTE_STATION = ITEMS.register("attribute_station",
            () -> new BlockItem(ModBlocks.ATTRIBUTE_STATION.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> ENHANCEMENT_STATION = ITEMS.register("enhancement_station",
            () -> new BlockItem(ModBlocks.ENHANCEMENT_STATION.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> BARRAGE = ITEMS.register("barrage",
            BarrageItem::new);
}
