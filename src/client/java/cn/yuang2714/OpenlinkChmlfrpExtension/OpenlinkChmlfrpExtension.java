package cn.Yuang2714.OpenlinkChmlfrpExtension;

import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.FrpcManagement;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.LoggingManagement;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.Preferences;

public class OpenlinkChmlfrpExtension implements ClientModInitializer {
    public static final String MODID = "openlink_chmlfrp_extension";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static Preferences PREFERENCES = Preferences.userNodeForPackage(OpenlinkChmlfrpExtension.class);
	@Override
	public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher,buildContext) -> {
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
                                            .executes(
                                                    context -> {
                                                        int value = context.getArgument("value", int.class);
                                                        if (value <= 0) {
                                                            context.getSource().sendError(Component.literal("Not a correct value"));
                                                            return 0;
                                                        }
                                                        OpenlinkChmlfrpExtension.PREFERENCES.putInt("config_max_retry", value);

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
                                                            LoggingManagement.checkToken(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
                                                    OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_in_china",
                                                            LoggingManagement.userIsInChina());
                                                    OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_vip",
                                                            LoggingManagement.userIsVIP(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
                                                    OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("has_real_named",
                                                            LoggingManagement.userHasRealnamed(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
                                                    context.getSource().sendFeedback(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.success"));
                                                    return 1;
                                                } catch (Exception e) {
                                                    OpenlinkChmlfrpExtension.LOGGER.error("{}", e.toString());
                                                    context.getSource().sendError(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.fail"));
                                                    return 0;
                                                }
                                            }
                                    )
                            )
            );
        });

		LOGGER.info("""
                
                  ____                       _       _         _        ____   _                  _    ______                 ______           _                       _
                 / __ \\                     | |     (_)       | |      / __ \\ | |                | |  | _____|               |  ____|        _| |_                    (_)
                | |  | | _ __    ___  _ __  | |      _  _ __  | | __  | |  \\_|| |___   _ ___ __  | |  | |____  _ __  _ __    | |____  _   _ |_   _|  ___  _ __   ____  _   ____   _ __ \s
                | |  | || '_ \\  / _ \\| '_ \\ | |     | || '_ \\ | |/ /  | |   _ | /__ \\ | '_  '_ \\ | |  |  ____|| '__|| '_ \\   |  ____|\\ \\_/ /  | |   / _ \\| '_ \\ / ___|| | / __ \\ | '_ \\
                | |__| || |_) ||  __/| | | || |____ | || | | ||   <   | |__/ || /  \\ || | | | | || |_ | |     | |   | |_) |  | |____  > _ <   | |__|  __/| | | |\\___ \\| || |__| || | | |
                 \\____/ | .__/  \\___||_| |_||______||_||_| |_||_|\\_\\   \\____/ |_|  |_||_| |_| |_||___||_|     |_|   | .__/   |______|/_/ \\_\\   \\__/ \\___||_| |_||____/|_| \\____/ |_| |_|
                        | |                                                                                         | |
                        |_|                                                                                         |_|
                """);
	}
}