package com.github.leopoko.tacz_attributes_addon;

import com.github.leopoko.tacz_attributes_addon.block.AttributeStationScreen;
import com.github.leopoko.tacz_attributes_addon.compat.apotheosis.ApotheosisCompat;
import com.github.leopoko.tacz_attributes_addon.command.ModCommands;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.AttributeRegistry;
import com.github.leopoko.tacz_attributes_addon.handler.WeaponTypeHandler;
import com.github.leopoko.tacz_attributes_addon.init.ModBlockEntities;
import com.github.leopoko.tacz_attributes_addon.init.ModBlocks;
import com.github.leopoko.tacz_attributes_addon.init.ModItems;
import com.github.leopoko.tacz_attributes_addon.init.ModMenuTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(TaczAttributesAddon.MODID)
public class TaczAttributesAddon {
    public static final String MODID = "tacz_attributes_addon";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup." + MODID))
                    .icon(() -> ModItems.ATTRIBUTE_STATION.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.ATTRIBUTE_STATION.get());
                    })
                    .build());

    public TaczAttributesAddon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register deferred registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register lifecycle events
        modEventBus.addListener(this::commonSetup);

        // Register Forge event bus
        MinecraftForge.EVENT_BUS.register(this);

        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);

        LOGGER.info("TACZ Attributes Addon initialized");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Load attribute pool config (must be before weapon type config)
            AttributeRegistry.loadConfig(FMLPaths.CONFIGDIR.get());

            // Load weapon type config
            WeaponTypeHandler.loadConfig(FMLPaths.CONFIGDIR.get());

            // Initialize Apotheosis compat
            ApotheosisCompat.init();

            LOGGER.info("TACZ Attributes Addon common setup complete");
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.ATTRIBUTE_STATION.get(), AttributeStationScreen::new);
            });
            LOGGER.info("TACZ Attributes Addon client setup complete");
        }
    }
}
