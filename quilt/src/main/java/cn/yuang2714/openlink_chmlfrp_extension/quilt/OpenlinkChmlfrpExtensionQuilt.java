package cn.yuang2714.openlink_chmlfrp_extension.quilt;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OCECommand;
import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.tools.Utility;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public final class OpenlinkChmlfrpExtensionQuilt implements ClientModInitializer {
    @Override
    public void onInitializeClient(ModContainer mod) {
        // Run our common setup.
        OpenlinkChmlfrpExtension.init();
        
        ClientCommandManager.getDispatcher().register(
                ClientCommandManager.literal("oce")
                        .then(ClientCommandManager.literal("setProxyCreationMaxRetry")
                                .executes(
                                        context -> {
                                            context.getSource().sendFeedback(Utility.translatableText("chat.openlink_chmlfrp_extension.command.config_max_retry.read",
                                                    OCECommand.readProxyCreationMaxRetry()));
                                            return 1;
                                        }
                                )
                                
                                .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(1))
                                        .executes(
                                                context -> {
                                                    int value = context.getArgument("value", int.class);
                                                    
                                                    if (OCECommand.setProxyCreationMaxRetry(value) == OCECommand.FAILURE) {
                                                        context.getSource().sendError(Utility.translatableText("chat.openlink_chmlfrp_extension.command.config_max_retry.fail").withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    } else {
                                                        context.getSource().sendFeedback(Utility.translatableText("chat.openlink_chmlfrp_extension.command.config_max_retry.success", value));
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
                                                context.getSource().sendError(Utility.translatableText("chat.openlink_chmlfrp_extension.command.reload_user_info.fail").withStyle(ChatFormatting.RED));
                                                return 0;
                                            } else {
                                                context.getSource().sendFeedback(Utility.translatableText("chat.openlink_chmlfrp_extension.command.reload_user_info.success"));
                                                return 1;
                                            }
                                        }
                                )
                        )
                        
                        .then(ClientCommandManager.literal("setDoAdvancedNodeSort")
                                .executes(
                                        context -> {
                                            context.getSource().sendFeedback(Utility.translatableText(
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
                                                        context.getSource().sendError(Utility.translatableText("chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.fail").withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    } else {
                                                        context.getSource().sendFeedback(Utility.translatableText("chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.success", value));
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
                                                context.getSource().sendError(Utility.translatableText("chat.openlink_chmlfrp_extension.command.clear_proxy.fail").withStyle(ChatFormatting.RED));
                                                return 0;
                                            } else {
                                                context.getSource().sendFeedback(Utility.translatableText("chat.openlink_chmlfrp_extension.command.clear_proxy.success"));
                                                return 1;
                                            }
                                        }
                                )
                        )
                        
                        .then(ClientCommandManager.literal("trick")
                                .executes(context -> {
                                    Util.getPlatform().openUri("https://www.bilibili.com/video/BV1GJ411x7h7"); //你 被 骗 了！！！
                                    context.getSource().sendFeedback(Utility.literalText("你 被 骗 了！！！").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
                                    OpenlinkChmlfrpExtension.LOGGER.warn("User is tricked!");
                                    return 1;
                                })
                        )
        );
        OCECommand.logger.info("Registered Command from Quilt.");
        
        ClientTickEvents.END.register(instance -> OpenlinkChmlfrpExtension.clientTickCallback());
    }
}
