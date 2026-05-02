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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public class OCECommandHelper {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(
                ClientCommandManager.literal("oce")
                        .then(ClientCommandManager.literal("setProxyCreationMaxRetry")
                                .executes(context -> {
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
                                .executes(context -> {
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
        );
        OCECommand.logger.info("Registered Command.");
    }
}
