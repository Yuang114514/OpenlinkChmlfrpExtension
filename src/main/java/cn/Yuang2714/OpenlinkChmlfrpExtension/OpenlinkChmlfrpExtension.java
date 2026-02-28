package cn.Yuang2714.OpenlinkChmlfrpExtension;

import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.FrpcManagement;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.LoggingManagement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.prefs.Preferences;

// The value here should match an entry in the META-INF/mods.toml file
@Mod.EventBusSubscriber
@Mod(OpenlinkChmlfrpExtension.MODID)
public class OpenlinkChmlfrpExtension {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "openlink_chmlfrp_extension";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Preferences PREFERENCES = Preferences.userNodeForPackage(OpenlinkChmlfrpExtension.class);

    public OpenlinkChmlfrpExtension() {
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

    @SubscribeEvent
    public static void onClientCommandRegistering(RegisterClientCommandsEvent event) {
        // /oce <command>
        //   \--\ setProxyCreationMaxRetry
        //   |  \- 无参数 输出当前配置
        //   |  \- <value> 设置为value
        //   \- reloadUserInfo 重新加载用户信息
        event.getDispatcher().register(
                Commands.literal("oce")
                    .then(Commands.literal("setProxyCreationMaxRetry")
                        .executes(context -> {
                            context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.read",
                                OpenlinkChmlfrpExtension.PREFERENCES.getInt("config_max_retry", 5)), false);
                            return 1;
                            }
                        )

                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                            .executes(
                                    context -> {
                                    int value = context.getArgument("value", int.class);
                                        if (value <= 0) {
                                        context.getSource().sendFailure(Component.literal("Not a correct value"));
                                        return 0;
                                    }
                                    OpenlinkChmlfrpExtension.PREFERENCES.putInt("config_max_retry", value);

                                    context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.config_max_retry.success", value), false);
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
                                            LoggingManagement.checkToken(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
                                    OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_in_china",
                                            LoggingManagement.userIsInChina());
                                    OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_vip",
                                            LoggingManagement.userIsVIP(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
                                    OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("has_real_named",
                                            LoggingManagement.userHasRealnamed(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
                                    context.getSource().sendSuccess(() -> Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.success"), false);
                                    return 1;
                                } catch (Exception e) {
                                    OpenlinkChmlfrpExtension.LOGGER.error("{}", e.toString());
                                    context.getSource().sendFailure(Component.translatable("chat.openlink_chmlfrp_extension.command.reload_user_info.fail"));
                                    return 0;
                                }
                            }
                        )
                    )
        );
        OpenlinkChmlfrpExtension.LOGGER.info("Registered Command.");
    }
}
