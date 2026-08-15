package com.github.leopoko.tacz_attributes_addon;

import com.github.leopoko.tacz_attributes_addon.block.AttributeStationScreen;
import com.github.leopoko.tacz_attributes_addon.block.EnhancementStationScreen;
import com.github.leopoko.tacz_attributes_addon.network.ModNetwork;
import com.github.leopoko.tacz_attributes_addon.compat.apotheosis.ApotheosisCompat;
import com.github.leopoko.tacz_attributes_addon.command.ModCommands;
import com.github.leopoko.tacz_attributes_addon.config.CommonConfig;
import com.github.leopoko.tacz_attributes_addon.data.AttributeRegistry;
import com.github.leopoko.tacz_attributes_addon.data.GunAttributeOverrides;
import com.github.leopoko.tacz_attributes_addon.data.StationMaterialRegistry;
import com.github.leopoko.tacz_attributes_addon.handler.WeaponTypeHandler;
import com.github.leopoko.tacz_attributes_addon.init.ModBlockEntities;
import com.github.leopoko.tacz_attributes_addon.init.ModBlocks;
import com.github.leopoko.tacz_attributes_addon.init.ModItems;
import com.github.leopoko.tacz_attributes_addon.init.ModMenuTypes;
import com.github.leopoko.tacz_attributes_addon.item.ModIngredientTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(TaczAttributesAddon.MODID)
public class TaczAttributesAddon {
    public static final String MODID = "tacz_attributes_addon";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup." + MODID))
                    .icon(() -> ModItems.ATTRIBUTE_STATION.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.ATTRIBUTE_STATION.get());
                        output.accept(ModItems.ENHANCEMENT_STATION.get());
                        output.accept(ModItems.BARRAGE.get());
                    })
                    .build());

    public TaczAttributesAddon(IEventBus modEventBus, ModContainer container) {
        // Register deferred registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModIngredientTypes.INGREDIENT_TYPES.register(modEventBus);

        // Register lifecycle events
        modEventBus.addListener(this::commonSetup);

        // Register NeoForge event bus
        NeoForge.EVENT_BUS.register(this);

        // Register config
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);

        // Register network packets
        modEventBus.addListener(ModNetwork::register);

        LOGGER.info("TACZ Attributes Addon initialized");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Load attribute pool config (must be before weapon type config)
            AttributeRegistry.loadConfig(FMLPaths.CONFIGDIR.get());

            // Load weapon type config
            WeaponTypeHandler.loadConfig(FMLPaths.CONFIGDIR.get());

            // Load per-gun attribute overrides
            GunAttributeOverrides.loadConfig(FMLPaths.CONFIGDIR.get());

            // Load station materials config
            StationMaterialRegistry.loadConfig(FMLPaths.CONFIGDIR.get());

            // Initialize Apotheosis compat
            ApotheosisCompat.init();

            LOGGER.info("TACZ Attributes Addon common setup complete");
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SuppressWarnings("removal")
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.ATTRIBUTE_STATION.get(), AttributeStationScreen::new);
            event.register(ModMenuTypes.ENHANCEMENT_STATION.get(), EnhancementStationScreen::new);
        }
    }
}
