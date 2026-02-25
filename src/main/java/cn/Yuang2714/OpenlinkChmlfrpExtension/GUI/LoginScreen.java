package cn.Yuang2714.OpenlinkChmlfrpExtension.GUI;

import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.LoggingManagement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class LoginScreen extends Screen {
    private final Screen parentScreen;
    private Button doneButton;
    private EditBox tokenBox;
    private boolean startDelay = false;
    private String delayFunction;
    private int delayed = 0;

    public LoginScreen(Screen lastScreen) {
        super(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.title"));
        parentScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        doneButton = Button.builder(
                Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_1"),
                button -> {
                    doneButton.active = false;
                    doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2"));

                    if (LoggingManagement.login(tokenBox.getValue())) {
                        doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_success"));
                        delayFunction = "BACK";
                        startDelay = true;
                    } else {
                        doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                        delayFunction = "RETRY";
                        startDelay = true;
                    }
                })
                .bounds(width / 2 - 100, height / 2 + 12, 200, 20)
                .build();
        addRenderableWidget(doneButton);

        tokenBox = new EditBox(
                font,
                width / 2 - 90,
                height / 2 - 12,
                180,
                20,
                Component.translatable("gui.openlink_chmlfrp_extension.login_screen.edit_box.description")
        );
        addRenderableWidget(tokenBox);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        graphics.drawString(font, title, width / 2 - font.width(title) / 2, 20, 0xFFFFFF);
        graphics.drawString(
                font,
                Component.translatable("gui.openlink_chmlfrp_extension.login_screen.edit_box.description"),
                width / 2 - font.width(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.edit_box.description")) / 2,
                height / 2 - 30,
                0xFFFFFF
                );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();

        if (startDelay) {
            delayed++;

            if (delayed >= 25) {
                if (delayFunction.equals("BACK")) {
                    minecraft.setScreen(parentScreen);
                    startDelay = false;
                } else if (delayFunction.equals("RETRY")) {
                    doneButton.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_1"));
                    doneButton.active = true;
                    startDelay = false;
                }
            }
        }
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parentScreen);
    }
}
