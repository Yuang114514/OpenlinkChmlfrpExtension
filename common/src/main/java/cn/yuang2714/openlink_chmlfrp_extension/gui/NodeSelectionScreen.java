package cn.yuang2714.openlink_chmlfrp_extension.gui;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.Node;
import cn.yuang2714.openlink_chmlfrp_extension.statics.URLs;
import cn.yuang2714.openlink_chmlfrp_extension.tools.Utility;
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
    private final List<Node> nodeList;
    private Component nodeDescription_name = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.name","");
    private Component nodeDescription_description = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.description","");
    private Component nodeDescription_location = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.location","");
    private Component nodeDescription_bandwidthUsage = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.bandwidth_usage","");
    private Component nodeDescription_cpuUsage = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.cpu_usage","");
    private int START_Y;
    private int START_X;
    //第一行：自动选择按钮+面板按钮+节点输入框，40像素一个单位，单独留空
    //   80-2   4  80-2-2   4  32-2
    //+--------+  +--------+  +----+
    //|  Auto  |  |  Panel |  | _  |
    //+--------+  +--------+  +----+

    public NodeSelectionScreen(Screen lastScreen, List<Node> nodes) {
        super(Utility.translatableText("gui.openlink.nodeselectionscreentitle"));
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
                Utility.translatableText("text.openlink_chmlfrp_extension.none")
        );
        idBox.setValue(String.valueOf(OpenlinkChmlfrpExtension.PREFERENCES.getInt("last_node", -1)));
        idBox.setFilter(text -> {
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return text.isEmpty() || text.equals("-");
            }
        });
        idBox.setResponder(this::idBoxResponder);
        addRenderableWidget(idBox);

        Button panelButton = Button.builder(
                        Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.panel_button_text"),
                        button -> Util.getPlatform().openUri(URLs.nodes))
                .bounds(START_X + 82, //80+(4/2)
                        START_Y,
                        76, //80 -2-2
                        20)
                .build();
        addRenderableWidget(panelButton);

        Button autoSelectButton = Button.builder(
                        Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.auto_button_text"),
                        button -> idBox.setValue("-1"))
                .bounds(START_X,
                        START_Y,
                        78, //80 -2
                        20)
                .build();
        addRenderableWidget(autoSelectButton);

        doneButton = Button.builder(
                Utility.translatableText("gui.done"),
                        this::onDoneButtonPress)
                .bounds(START_X,
                        START_Y + 98, //20 + 4 + 70 + 4 + 20
                        200,
                        20)
                .build();
        addRenderableWidget(doneButton);
        
        idBoxResponder(String.valueOf(OpenlinkChmlfrpExtension.PREFERENCES.getInt("last_node", -1)));
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
                Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.tip"),
                width / 2 - font.width(Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.tip")) / 2,
                height / 2 - 75,
                0xFFFFFF
        );

        //绘制节点信息
        graphics.drawString(
                font,
                Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.title"),
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

    private void onDoneButtonPress(Button button) {
        doneButton.setMessage(Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.ing"));
        doneButton.active = false;

        String selection = idBox.getValue();
        if (selection.isBlank()) {
            doneButton.setMessage(Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.fail"));
            doneButton.active = true;
            return;
        }
        int selectedId = Integer.parseInt(selection);

        if (selectedId == -1) {
            doneButton.setMessage(Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.auto"));
            OpenlinkChmlfrpExtension.PREFERENCES.putInt("last_node", -1);
            startDelay = true;
            return;
        }

        for (Node nodeInList : nodeList) {
            if (nodeInList.id == selectedId) {
                OpenlinkChmlfrpExtension.PREFERENCES.putInt("last_node", selectedId);
                doneButton.setMessage(Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.success"));
                startDelay = true;
                return;
            }
        }

        doneButton.setMessage(Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.fail"));
        doneButton.active = true;
    }

    private void idBoxResponder(String text) {
        try {
            nodeDescription_name = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.name", "");
            nodeDescription_description = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.description", "");
            nodeDescription_location = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.location", "");
            nodeDescription_bandwidthUsage = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.bandwidth_usage", "");
            nodeDescription_cpuUsage = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.cpu_usage", "");
            int enteredId = Integer.parseInt(text);
            nodeList.forEach(entered -> {
                if (enteredId != -1 && entered.id == enteredId) {
                    nodeDescription_name = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.name", entered.name);
                    nodeDescription_description = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.description",
                            entered.description.length() > 12 ? entered.description.substring(0, 13) + "..." : entered.description);
                    nodeDescription_location = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.location", entered.location);
                    nodeDescription_bandwidthUsage = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.bandwidth_usage", String.valueOf(entered.bandwidthUsage));
                    nodeDescription_cpuUsage = Utility.translatableText("gui.openlink_chmlfrp_extension.node_selection.node_info.cpu_usage", String.valueOf(entered.cpuUsage));
                }
            });
        } catch (NumberFormatException ignored) {
        }
    }
}
