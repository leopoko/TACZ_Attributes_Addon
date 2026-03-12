package com.github.leopoko.tacz_attributes_addon.network;

import com.github.leopoko.tacz_attributes_addon.TaczAttributesAddon;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(TaczAttributesAddon.MODID)
                .versioned(PROTOCOL_VERSION);

        registrar.playToClient(
                EnhancementChoicesPacket.TYPE,
                EnhancementChoicesPacket.STREAM_CODEC,
                EnhancementChoicesPacket::handle
        );

        registrar.playToServer(
                EnhancementActionPacket.TYPE,
                EnhancementActionPacket.STREAM_CODEC,
                EnhancementActionPacket::handle
        );
    }
}
