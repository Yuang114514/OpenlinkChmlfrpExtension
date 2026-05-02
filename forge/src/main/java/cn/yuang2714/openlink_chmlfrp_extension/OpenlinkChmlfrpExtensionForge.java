package cn.yuang2714.openlink_chmlfrp_extension;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
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
                                                    OCECommand.readProxyCreationMaxRetry()), true);
                                            return 1;
                                        }
                                )
                                
                                .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                        .executes(
                                                context -> {
                                                    int value = context.getArgument("value", int.class);
                                                    
                                                    if (OCECommand.setProxyCreationMaxRetry(value) == OCECommand.FAILURE) {
                                                        context.getSource().sendFailure(Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.fail").withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    } else {
                                                        context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.success", value), true);
                                                        return 1;
                                                    }
                                                }
                                        )
                                )
                        )
                        
                        .then(Commands.literal("reloadUserInfo")
                                .executes(
                                        context -> {
                                            if (OCECommand.reloadUserInfo() == OCECommand.FAILURE) {
                                                context.getSource().sendFailure(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.fail").withStyle(ChatFormatting.RED));
                                                return 0;
                                            } else {
                                                context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.success"), true);
                                                return 1;
                                            }
                                        }
                                )
                        )
                        
                        .then(Commands.literal("setDoAdvancedNodeSort")
                                .executes(context -> {
                                    context.getSource().sendSuccess(() -> Component.translatable(
                                            "chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.read",
                                            OCECommand.readDoAdvancedNodeSort()
                                    ), true);
                                    return 1;
                                })
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(
                                                context -> {
                                                    boolean value = context.getArgument("value", boolean.class);
                                                    if (OCECommand.setDoAdvancedNodeSort(value) == OCECommand.FAILURE) {
                                                        context.getSource().sendFailure(Component.translatable("chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.fail").withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    } else {
                                                        context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.config_advanced_node_sort.success", value), true);
                                                        return 1;
                                                    }
                                                }
                                        )
                                )
                        )
        );
        OCECommand.logger.info("Registered Command.");
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        OpenlinkChmlfrpExtension.clientTickCallback();
    }
}