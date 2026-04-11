package cn.yuang2714.openlink_chmlfrp_extension.gui;

import cn.yuang2714.openlink_chmlfrp_extension.datatypes.Node;
import cn.yuang2714.openlink_chmlfrp_extension.tools.NodeUtil;
import cn.yuang2714.openlink_chmlfrp_extension.tools.Utils;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class LoadingNodeSelectionScreen extends Screen {
    private final Screen parentScreen;
    private List<Node> nodes = null;
    private Thread requestThread;
    private boolean isFailed = false;
    private Component status = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.loading");
    private final Logger logger = LogUtils.getLogger();
    private boolean isDelaying = false;
    private int delayed = 0;

    public LoadingNodeSelectionScreen(Screen parentScreen) {
        super(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.loading"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        requestThread = new Thread(this::run);
        requestThread.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (isDelaying) {
            delayed++;
            if (delayed >= 20) Minecraft.getInstance().setScreen(parentScreen);
        }
        if (!requestThread.isAlive() && nodes != null) {
            if (!isFailed) {
                logger.info("Got generated node list. Starting selection Screen.");
                Minecraft.getInstance().setScreen(new NodeSelectionScreen(parentScreen, nodes));
            } else {
                status = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.list_exception").withStyle(ChatFormatting.RED);
                logger.info("Failed to get node list.");
                isDelaying = true;
            }
        }
    }

    @Override
    public void onClose() {
        requestThread.interrupt();
        Minecraft.getInstance().setScreen(parentScreen);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawString(
                font,
                status,
                width / 2 - font.width(status) / 2,
                height / 2 - 5,
                0xFFFFFF
        );
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void run() {
        logger.info("Node List getter thread started.");
        try {
            nodes = NodeUtil.genNodeList();
        } catch (Exception e) {
            isFailed = true;
            logger.error("Failed to get node list. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
        }
    }
}
