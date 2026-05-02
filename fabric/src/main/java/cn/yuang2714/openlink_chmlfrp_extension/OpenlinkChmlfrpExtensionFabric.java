package cn.yuang2714.openlink_chmlfrp_extension;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.tools.FrpcManagement;
import cn.yuang2714.openlink_chmlfrp_extension.tools.LoggingManagement;
import cn.yuang2714.openlink_chmlfrp_extension.tools.Utils;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

public class OpenlinkChmlfrpExtensionFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        //OpenlinkChmlfrpExtension.LOGGER.info("Hello Fabric!");
        OpenlinkChmlfrpExtension.init();
        
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            dispatcher.register(
                    ClientCommandManager.literal("oce")
                            .then(ClientCommandManager.literal("setProxyCreationMaxRetry")
                                    .executes(context -> {
                                                context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.read",
                                                        OpenlinkChmlfrpExtension.PREFERENCES.getInt("config_max_retry", 5)));
                                                return 1;
                                            }
                                    )
                                    
                                    .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(1))
                                            .executes(context -> {
                                                        int value = context.getArgument("value", int.class);
                                                        if (value <= 0) {
                                                            context.getSource().sendError(Component.literal("Not a correct value"));
                                                            return 0;
                                                        }
                                                        OpenlinkChmlfrpExtension.PREFERENCES.putInt("config_max_retry", value);
                                                        Utils.flushPreferences(OpenlinkChmlfrpExtension.LOGGER, "changing settings");
                                                        
                                                        context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.success", value));
                                                        return 1;
                                                    }
                                            )
                                    )
                            )
                            
                            .then(ClientCommandManager.literal("reloadUserInfo")
                                    .executes(
                                            context -> {
                                                try {
                                                    FrpcManagement.initUserEnv();
                                                    OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in",
                                                            LoggingManagement.refreshToken()
                                                    );
                                                    LoggingManagement.reloadUserAddress();
                                                    LoggingManagement.refreshUserInfo();
                                                    OpenlinkChmlfrpExtension.PREFERENCES.flush();
                                                    
                                                    context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.success"));
                                                    return 1;
                                                } catch (Exception e) {
                                                    OpenlinkChmlfrpExtension.LOGGER.error("Failed to reload user info. {}", e.toString());
                                                    context.getSource().sendError(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.fail"));
                                                    return 0;
                                                }
                                            }
                                    )
                            )
                            
                            .then(ClientCommandManager.literal("setDoAdvancedNodeSort")
                                    .executes(context -> {
                                        context.getSource().sendFeedback(Component.translatable(
                                                "chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.read",
                                                OpenlinkChmlfrpExtension.PREFERENCES.getBoolean("advanced_node_sort", false)
                                        ));
                                        return 1;
                                    })
                                    .then(ClientCommandManager.argument("value", BoolArgumentType.bool())
                                            .executes(
                                                    context -> {
                                                        boolean value = context.getArgument("value", boolean.class);
                                                        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("advanced_node_sort", value);
                                                        Utils.flushPreferences(OpenlinkChmlfrpExtension.LOGGER, "changing settings");
                                                        
                                                        context.getSource().sendFeedback(Component.translatable(
                                                                        "chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.success", value));
                                                        return 1;
                                                    }
                                            )
                                    )
                            )
            );
            OpenlinkChmlfrpExtension.LOGGER.info("Registered Command.");
        });
        
        ClientTickEvents.END_CLIENT_TICK.register(instance -> OpenlinkChmlfrpExtension.clientTickCallback());
    }
}
