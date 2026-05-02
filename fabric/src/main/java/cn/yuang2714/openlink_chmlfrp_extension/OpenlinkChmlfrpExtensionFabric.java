package cn.yuang2714.openlink_chmlfrp_extension;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class OpenlinkChmlfrpExtensionFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        //OpenlinkChmlfrpExtension.LOGGER.info("Hello Fabric!");
        OpenlinkChmlfrpExtension.init();
        
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> OCECommandHelper.register(dispatcher));
        
        ClientTickEvents.END_CLIENT_TICK.register(instance -> OpenlinkChmlfrpExtension.clientTickCallback());
    }
}
