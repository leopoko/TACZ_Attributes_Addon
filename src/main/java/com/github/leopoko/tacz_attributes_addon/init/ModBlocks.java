package com.github.leopoko.tacz_attributes_addon.init;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.block.AttributeStationBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TaczAttributesAddon.MODID);

    public static final RegistryObject<Block> ATTRIBUTE_STATION = BLOCKS.register("attribute_station",
            AttributeStationBlock::new);
}
