package com.github.leopoko.tacz_attributes_addon.network;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TaczAttributesAddon.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        int id = 0;
        CHANNEL.registerMessage(id++, EnhancementChoicesPacket.class,
                EnhancementChoicesPacket::encode,
                EnhancementChoicesPacket::decode,
                EnhancementChoicesPacket::handle);

        CHANNEL.registerMessage(id++, EnhancementActionPacket.class,
                EnhancementActionPacket::encode,
                EnhancementActionPacket::decode,
                EnhancementActionPacket::handle);
    }
}
