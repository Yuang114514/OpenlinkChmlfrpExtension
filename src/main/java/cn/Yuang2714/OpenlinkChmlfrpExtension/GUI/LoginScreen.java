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
    private Component loginStatText = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_1");
    private boolean startDelay = false;
    private int delayed = 0;

    public LoginScreen(Screen lastScreen) {
        super(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.title"));
        parentScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        doneButton = Button.builder(
                loginStatText,
                button -> {
                    doneButton.active = false;
                    loginStatText = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2");
                    doneButton.setMessage(loginStatText);

                    if (LoggingManagement.login(tokenBox.getValue())) {
                        loginStatText = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_success");
                        doneButton.setMessage(loginStatText);
                        startDelay = true;
                    } else {
                        loginStatText = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail");
                        doneButton.setMessage(loginStatText);
                        doneButton.active = true;
                    }
                })
                .bounds(width / 2 - 100, height / 2 + 12, 200, 20)
                .build();
        addRenderableWidget(doneButton);

        tokenBox = new EditBox(
                font,
                width / 2 - 100,
                height / 2 - 12,
                200,
                20,
                Component.translatable("gui.openlink_chmlfrp_extension.login_screen.edit_box.description")
        );
        addRenderableWidget(tokenBox);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        graphics.drawString(font, title, width / 2 - font.width(title) / 2, 20, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();

        if (startDelay) {
            delayed++;
            if (delayed >= 40) {
                minecraft.setScreen(parentScreen);
                startDelay = false;
            }
        }
    }
}
