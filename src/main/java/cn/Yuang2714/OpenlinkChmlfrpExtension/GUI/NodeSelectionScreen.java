package cn.Yuang2714.OpenlinkChmlfrpExtension.GUI;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Statics.URLs;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Tools.Node;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Tools.NodeUtil;
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
    private List<Node> nodeList;
    private Component nodeDescription_name = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.name","");
    private Component nodeDescription_description = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.description","");
    private Component nodeDescription_location = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.location","");
    private Component nodeDescription_bandwidthUsage = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.bandwidth_usage","");
    private Component nodeDescription_cpuUsage = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.cpu_usage","");
    private int START_Y;
    private int START_X;
    //第一行：自动选择按钮+面板按钮+节点输入框，40像素一个单位，单独留空
    //   80-2   4  80-2-2   4  32-2
    //+--------+  +--------+  +----+
    //|  Auto  |  |  Panel |  | _  |
    //+--------+  +--------+  +----+

    public NodeSelectionScreen(Screen lastScreen, List<Node> nodes) {
        super(Component.translatable("gui.openlink.nodeselectionscreentitle"));
        parentScreen = lastScreen;
        nodeList = nodes;
    }

    @Override
    protected void init() {
        super.init();

        START_X = width / 2 - 100; //200 /2
        START_Y = height / 2 - 59; //64 = 20 + 4 + (10 * 6)+5+5 + 4 + 20，再/2得到居中

        idBox = new EditBox(
                font,
                START_X + 161, // 80+80+(4/2) +1
                START_Y + 1,
                40 -2,
                20 -2,
                Component.translatable("text.openlink_chmlfrp_extension.none")
        );
        idBox.setValue(String.valueOf(OpenlinkChmlfrpExtension.PREFERENCES.getInt("last_node", -1)));
        idBox.setFilter(text -> text.matches("-*\\d*"));
        idBox.setResponder(text -> {
            try {
                nodeDescription_name = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.name","");
                nodeDescription_description = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.description","");
                nodeDescription_location = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.location","");
                nodeDescription_bandwidthUsage = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.bandwidth_usage","");
                nodeDescription_cpuUsage = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.cpu_usage","");
                int enteredId = Integer.parseInt(text);
                nodeList.forEach(entered -> {
                    if (enteredId != -1 && entered.id == enteredId) {
                        nodeDescription_name = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.name", entered.name);
                        nodeDescription_description = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.description",
                                entered.description.length() >= 15 ? entered.description.substring(0,16) + "..." : entered.description);
                        nodeDescription_location = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.location", entered.location);
                        nodeDescription_bandwidthUsage = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.bandwidth_usage", String.valueOf(entered.bandwidthUsage));
                        nodeDescription_cpuUsage = Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.cpu_usage", String.valueOf(entered.cpuUsage));
                    }
                });
            } catch (NumberFormatException ignored) {}
        });
        addRenderableWidget(idBox);

        Button panelButton = Button.builder(
                        Component.translatable("gui.openlink_chmlfrp_extension.node_selection.panel_button_text"),
                        button -> Util.getPlatform().openUri(URLs.nodes))
                .bounds(START_X + 82, //80+(4/2)
                        START_Y,
                        76, //80 -2-2
                        20)
                .build();
        addRenderableWidget(panelButton);

        Button autoSelectButton = Button.builder(
                        Component.translatable("gui.openlink_chmlfrp_extension.node_selection.auto_button_text"),
                        button -> idBox.setValue("-1"))
                .bounds(START_X,
                        START_Y,
                        78, //80 -2
                        20)
                .build();
        addRenderableWidget(autoSelectButton);

        doneButton = Button.builder(
                Component.translatable("gui.done"),
                button -> {
                    doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.node_selection.ing"));
                    doneButton.active = false;

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
                .bounds(START_X,
                        START_Y + 98, //20 + 4 + 70 + 4 + 20
                        200,
                        20)
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
                height / 2 - 75,
                0xFFFFFF
        );

        //绘制节点信息
        graphics.drawString(
                font,
                Component.translatable("gui.openlink_chmlfrp_extension.node_selection.node_info.title"),
                START_X + 5,
                START_Y + 29, //20 + 4 + 5
                0xFFFFFF
        );
        graphics.drawString(
                font,
                nodeDescription_name,
                START_X + 5,
                START_Y + 39, //20 + 4 + 5 + (10*1)
                0xFFFFFF
        );
        graphics.drawString(
                font,
                nodeDescription_description,
                START_X + 5,
                START_Y + 49, //20 + 4 + 5 + (10*2)
                0xFFFFFF
        );
        graphics.drawString(
                font,
                nodeDescription_location,
                START_X + 5,
                START_Y + 59, //20 + 4 + 5 + (10*3)
                0xFFFFFF
        );
        graphics.drawString(
                font,
                nodeDescription_bandwidthUsage,
                START_X + 5,
                START_Y + 69, //20 + 4 + 5 + (10*4)
                0xFFFFFF
        );
        graphics.drawString(
                font,
                nodeDescription_cpuUsage,
                START_X + 5,
                START_Y + 79, //20 + 4 + 5 + (10*5)
                0xFFFFFF
        );

        //绘制边框
        //上
        graphics.fill(
                START_X + 200,
                START_Y + 26, //20 + 4再加边框宽度2
                START_X,
                START_Y + 24, //20 + 4
                0xFFFFFFFF
        );
        //左
        graphics.fill(
                START_X,
                START_Y + 24, //20 + 4
                START_X + 2, //宽度2
                START_Y + 94, //边框大小
                0xFFFFFFFF
        );
        //下
        graphics.fill(
                START_X,
                START_Y + 94, //边框大小
                START_X + 200,
                START_Y + 92,
                0xFFFFFFFF
        );
        //右
        graphics.fill(
                START_X + 200,
                START_Y + 94,
                START_X + 198,
                START_Y + 24,
                0xFFFFFFFF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
