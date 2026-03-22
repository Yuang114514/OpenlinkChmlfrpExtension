package cn.Yuang2714.OpenlinkChmlfrpExtension.GUI;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Tools.Node;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Tools.NodeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoadingNodeSelectionScreen extends Screen {
    private final Screen parentScreen;
    private List<Node> nodes = null;
    private Thread requestThread;

    public LoadingNodeSelectionScreen(Screen parentScreen) {
        super(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.loading"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();
        requestThread = new Thread(() -> {
            OpenlinkChmlfrpExtension.LOGGER.info("Node List getter thread started.");
            nodes = NodeUtil.genNodeList();
        });
        requestThread.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (!requestThread.isAlive() && nodes != null) {
            OpenlinkChmlfrpExtension.LOGGER.info("Got generated node list. Starting selection Screen.");
            Minecraft.getInstance().setScreen(new NodeSelectionScreen(parentScreen, nodes));
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
                Component.translatable("gui.openlink_chmlfrp_extension.node_selection.loading"),
                width / 2 - font.width(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.loading")) / 2,
                height / 2 - 5,
                0xFFFFFF
        );
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
