package cn.yuang2714.openlink_chmlfrp_extension;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

public class OCECommandHelper {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                ClientCommandManager.literal("oce")
                        .then(ClientCommandManager.literal("setProxyCreationMaxRetry")
                                .executes(
                                        context -> {
                                            context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.read",
                                                    OCECommand.readProxyCreationMaxRetry()));
                                            return 1;
                                        }
                                )
                                
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(1))
                                        .executes(
                                                context -> {
                                                    int value = context.getArgument("value", int.class);
                                                    
                                                    if (OCECommand.setProxyCreationMaxRetry(value) == OCECommand.FAILURE) {
                                                        context.getSource().sendError(Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.fail").withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    } else {
                                                        context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.success", value));
                                                        return 1;
                                                    }
                                                }
                                        )
                                )
                        )
                        
                        .then(ClientCommandManager.literal("reloadUserInfo")
                                .executes(
                                        context -> {
                                            if (OCECommand.reloadUserInfo() == OCECommand.FAILURE) {
                                                context.getSource().sendError(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.fail").withStyle(ChatFormatting.RED));
                                                return 0;
                                            } else {
                                                context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.success"));
                                                return 1;
                                            }
                                        }
                                )
                        )
                        
                        .then(ClientCommandManager.literal("setDoAdvancedNodeSort")
                                .executes(
                                        context -> {
                                    context.getSource().sendFeedback(Component.translatable(
                                            "chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.read",
                                            OCECommand.readDoAdvancedNodeSort()
                                    ));
                                    return 1;
                                })
                                .then(ClientCommandManager.argument("value", BoolArgumentType.bool())
                                        .executes(
                                                context -> {
                                                    boolean value = context.getArgument("value", boolean.class);
                                                    if (OCECommand.setDoAdvancedNodeSort(value) == OCECommand.FAILURE) {
                                                        context.getSource().sendError(Component.translatable("chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.fail").withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    } else {
                                                        context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.success", value));
                                                        return 1;
                                                    }
                                                }
                                        )
                                )
                        )
                
                        .then(ClientCommandManager.literal("clearProxy")
                                .executes(
                                        context -> {
                                            if (OCECommand.clearProxy() == OCECommand.FAILURE) {
                                                context.getSource().sendError(Component.translatable("chat.openlink_chmlfrp_extension.command.clear_proxy.fail").withStyle(ChatFormatting.RED));
                                                return 0;
                                            } else {
                                                context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.clear_proxy.success"));
                                                return 1;
                                            }
                                        }
                                )
                        )
                        
                        .then(ClientCommandManager.literal("trick")
                                .executes(context -> {
                                    Util.getPlatform().openUri("https://www.bilibili.com/video/BV1GJ411x7h7"); //你 被 骗 了！！！
                                    context.getSource().sendFeedback(Component.literal("你 被 骗 了！！！").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
                                    OpenlinkChmlfrpExtension.LOGGER.warn("User is tricked!");
                                    return 1;
                                })
                        )
        );
        OCECommand.logger.info("Registered Command from Fabric.");
    }
}
