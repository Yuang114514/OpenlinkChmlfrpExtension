package cn.Yuang2714.OpenlinkChmlfrpExtension;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.prefs.Preferences;

// The value here should match an entry in the META-INF/mods.toml file
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

//    @SubscribeEvent
//    public static void onRegisterCommands(RegisterCommandsEvent event) {
//        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
//
//        // /selectnode <node_id>
//        dispatcher.register(Commands.literal("selectnode")
//                .then(Commands.argument("node_id", IntegerArgumentType.integer()))
//                .executes(context -> {
//                    int selectedId;
//                    try {
//                        selectedId = IntegerArgumentType.getInteger(context, "node_id");
//                    } catch (IllegalArgumentException e) {
//                        selectedId = -1;
//                    }
//
//                    if (selectedId == -1) {
//                        OpenlinkChmlfrpExtension.PREFERENCES.putInt("last_node", -1);
//                        context.getSource().sendSuccess(() -> Component.translatable("gui.openlink_chmlfrp_extension.node_selection.auto")
//                                .withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)
//                                , false);
//                        return 1;
//                    }
//
//                    context.getSource().sendSystemMessage(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.ing")
//                            .withStyle(ChatFormatting.AQUA)
//                            .withStyle(ChatFormatting.ITALIC));
//
//                    List<Node> nodeList = NodeUtil.genNodeList();
//                    if (nodeList == null) {
//                        context.getSource().sendFailure(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.list_exception")
//                                .withStyle(ChatFormatting.AQUA)
//                                .withStyle(ChatFormatting.ITALIC));
//                        return 0;
//                    }
//
//                    for (Node listNode : nodeList) {
//                        if (listNode.id == selectedId) {
//                            OpenlinkChmlfrpExtension.PREFERENCES.putInt("last_node", selectedId);
//                            context.getSource().sendSuccess(() -> Component.translatable("gui.openlink_chmlfrp_extension.node_selection.success")
//                                    .withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC), false);
//                            return 1;
//                        }
//                    }
//
//                    context.getSource().sendFailure(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.fail")
//                            .withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
//                    return 0;
//                })
//        );
//    }
}
