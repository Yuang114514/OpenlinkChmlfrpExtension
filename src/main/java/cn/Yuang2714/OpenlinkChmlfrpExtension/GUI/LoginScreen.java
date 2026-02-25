package cn.Yuang2714.OpenlinkChmlfrpExtension.GUI;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LoginScreen extends Screen {
    private final Screen parentScreen;
    private EditBox tokenBox;

    public LoginScreen(Screen lastScreen) {
        super(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.title"));
        parentScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                button -> {

                })
                .bounds(this.width / 2 - 50, this.height / 2 + 10, 100, 20)
                .build()
        );
    }
}
