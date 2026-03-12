package com.github.leopoko.tacz_attributes_addon.init;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.block.AttributeStationBlockEntity;
import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TaczAttributesAddon.MODID);

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AttributeStationBlockEntity>> ATTRIBUTE_STATION =
            BLOCK_ENTITIES.register("attribute_station",
                    () -> BlockEntityType.Builder.of(
                            AttributeStationBlockEntity::new,
                            ModBlocks.ATTRIBUTE_STATION.get()
                    ).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnhancementStationBlockEntity>> ENHANCEMENT_STATION =
            BLOCK_ENTITIES.register("enhancement_station",
                    () -> BlockEntityType.Builder.of(
                            EnhancementStationBlockEntity::new,
                            ModBlocks.ENHANCEMENT_STATION.get()
                    ).build(null));
}
