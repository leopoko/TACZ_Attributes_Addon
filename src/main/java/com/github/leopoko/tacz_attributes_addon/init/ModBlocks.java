package com.github.leopoko.tacz_attributes_addon.init;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.block.AttributeStationBlock;
import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, TaczAttributesAddon.MODID);

    public static final DeferredHolder<Block, Block> ATTRIBUTE_STATION = BLOCKS.register("attribute_station",
            AttributeStationBlock::new);

    public static final DeferredHolder<Block, Block> ENHANCEMENT_STATION = BLOCKS.register("enhancement_station",
            EnhancementStationBlock::new);
}
