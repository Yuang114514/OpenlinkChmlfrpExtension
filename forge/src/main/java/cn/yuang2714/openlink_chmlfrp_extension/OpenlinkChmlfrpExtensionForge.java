package cn.yuang2714.openlink_chmlfrp_extension;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(OpenlinkChmlfrpExtension.MODID)
@Mod.EventBusSubscriber
public class OpenlinkChmlfrpExtensionForge {
    
    public OpenlinkChmlfrpExtensionForge() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        OpenlinkChmlfrpExtension.LOGGER.info("Hello Forge!");
        OpenlinkChmlfrpExtension.init();
    }
    
    @SubscribeEvent
    public static void onClientCommandRegistering(RegisterClientCommandsEvent event) {
        OpenlinkChmlfrpExtension.registerOCECommand(event.getDispatcher());
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        OpenlinkChmlfrpExtension.clientTickCallback();
    }
}