package cn.Yuang2714.OpenlinkChmlfrpExtension.GUI;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.Node;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.NodeUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NodeSelectionScreen extends Screen {
    private final Screen parentScreen;
    private EditBox idBox;
    private Button doneButton;
    private boolean startDelay;
    private int delayed = 0;

    public NodeSelectionScreen(Screen lastScreen) {
        super(Component.translatable("gui.openlink.nodeselectionscreentitle"));
        parentScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();

        idBox = new EditBox(
                font,
                width / 2 + 60,
                height / 2 - 12,
                40,
                20,
                Component.translatable("text.openlink_chmlfrp_extension.none")
        );
        idBox.setValue(String.valueOf(OpenlinkChmlfrpExtension.PREFERENCES.getInt("last_node", -1)));
        idBox.setFilter(text -> text.matches("-*\\d*"));
        addRenderableWidget(idBox);

        Button panelButton = Button.builder(
                        Component.translatable("gui.openlink_chmlfrp_extension.node_selection.panel_button_text"),
                        button -> Util.getPlatform().openUri(URLs.nodes))
                .bounds(width / 2 - 20, height / 2 - 12, 80, 20)
                .build();
        addRenderableWidget(panelButton);

        Button autoSelectButton = Button.builder(
                        Component.translatable("gui.openlink_chmlfrp_extension.node_selection.auto_button_text"),
                        button -> idBox.setValue("-1"))
                .bounds(width / 2 - 100, height / 2 - 12, 80, 20)
                .build();
        addRenderableWidget(autoSelectButton);

        doneButton = Button.builder(
                Component.translatable("gui.done"),
                button -> {
                    doneButton.active = false;
                    doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.ing"));

                    String selection = idBox.getValue();
                    if (selection.isBlank()) {
                        doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.fail"));
                        doneButton.active = true;
                        return;
                    }
                    int selectedId = Integer.parseInt(selection);

                    if (selectedId == -1) {
                        doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.auto"));
                        startDelay = true;
                        return;
                    }

                    List<Node> nodeList = NodeUtil.genNodeList();
                    if (nodeList == null) {
                        doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.list_exception"));
                        doneButton.active = true;
                        return;
                    }

                    for (Node nodeInList : nodeList) {
                        if (nodeInList.id == selectedId) {
                            OpenlinkChmlfrpExtension.PREFERENCES.putInt("last_node", selectedId);
                            doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.success"));
                            startDelay = true;
                            return;
                        }
                    }

                    doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.fail"));
                    doneButton.active = true;
                })
                .bounds(width / 2 - 100, height / 2 + 20, 200, 20)
                .build();
        addRenderableWidget(doneButton);
    }

    @Override
    public void tick() {
        super.tick();

        if (startDelay) {
            delayed++;
            if (delayed >= 25) Minecraft.getInstance().setScreen(parentScreen);
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parentScreen);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        graphics.drawString(font, title, width / 2 - font.width(title) / 2, 20, 0xFFFFFF);
        graphics.drawString(
                font,
                Component.translatable("gui.openlink_chmlfrp_extension.node_selection.tip"),
                width / 2 - font.width(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.tip")) / 2,
                height / 2 - 45,
                0xFFFFFF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {
        graphics.fillGradient(0, 0, width, height, 0x70707070,  0x70707070);
    }
}
