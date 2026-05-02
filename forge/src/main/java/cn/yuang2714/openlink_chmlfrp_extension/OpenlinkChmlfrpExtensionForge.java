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
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
        //OpenlinkChmlfrpExtension.LOGGER.info("Hello Forge!");
        OpenlinkChmlfrpExtension.init();
    }
    
    @SubscribeEvent
    public static void onClientCommandRegistering(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("oce")
                        .then(Commands.literal("setProxyCreationMaxRetry")
                                .executes(context -> {
                                            context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.read",
                                                    OpenlinkChmlfrpExtension.PREFERENCES.getInt("config_max_retry", 5)), true);
                                            return 1;
                                        }
                                )
                                
                                .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                                    int value = context.getArgument("value", int.class);
                                                    if (value <= 0) {
                                                        context.getSource().sendFailure(Component.literal("Not a correct value"));
                                                        return 0;
                                                    }
                                                    OpenlinkChmlfrpExtension.PREFERENCES.putInt("config_max_retry", value);
                                                    Utils.flushPreferences(OpenlinkChmlfrpExtension.LOGGER, "changing settings");
                                                    
                                                    context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.success", value), true);
                                                    return 1;
                                                }
                                        )
                                )
                        )
                        
                        .then(Commands.literal("reloadUserInfo")
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
                                                
                                                context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.success"), true);
                                                return 1;
                                            } catch (Exception e) {
                                                OpenlinkChmlfrpExtension.LOGGER.error("Failed to reload user info. {}", e.toString());
                                                context.getSource().sendFailure(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.fail"));
                                                return 0;
                                            }
                                        }
                                )
                        )
                        
                        .then(Commands.literal("setDoAdvancedNodeSort")
                                .executes(context -> {
                                    context.getSource().sendSuccess(() -> Component.translatable(
                                            "chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.read",
                                            OpenlinkChmlfrpExtension.PREFERENCES.getBoolean("advanced_node_sort", false)
                                    ), true);
                                    return 1;
                                })
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(
                                                context -> {
                                                    boolean value = context.getArgument("value", boolean.class);
                                                    OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("advanced_node_sort", value);
                                                    Utils.flushPreferences(OpenlinkChmlfrpExtension.LOGGER, "changing settings");
                                                    
                                                    context.getSource().sendSuccess(() -> Component.translatable(
                                                                    "chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.success", value),
                                                            true);
                                                    return 1;
                                                }
                                        )
                                )
                        )
        );
        OpenlinkChmlfrpExtension.LOGGER.info("Registered Command.");
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        OpenlinkChmlfrpExtension.clientTickCallback();
    }
}