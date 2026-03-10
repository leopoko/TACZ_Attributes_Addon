package com.github.leopoko.tacz_attributes_addon.init;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import com.github.leopoko.tacz_attributes_addon.block.AttributeStationBlockEntity;
import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TaczAttributesAddon.MODID);

    @SuppressWarnings("DataFlowIssue")
    public static final RegistryObject<BlockEntityType<AttributeStationBlockEntity>> ATTRIBUTE_STATION =
            BLOCK_ENTITIES.register("attribute_station",
                    () -> BlockEntityType.Builder.of(
                            AttributeStationBlockEntity::new,
                            ModBlocks.ATTRIBUTE_STATION.get()
                    ).build(null));

    @SuppressWarnings("DataFlowIssue")
    public static final RegistryObject<BlockEntityType<EnhancementStationBlockEntity>> ENHANCEMENT_STATION =
            BLOCK_ENTITIES.register("enhancement_station",
                    () -> BlockEntityType.Builder.of(
                            EnhancementStationBlockEntity::new,
                            ModBlocks.ENHANCEMENT_STATION.get()
                    ).build(null));
}
