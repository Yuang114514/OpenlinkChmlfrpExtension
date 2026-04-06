package cn.Yuang2714.OpenlinkChmlfrpExtension.GUI;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Statics.URLs;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Tools.LoggingManagement;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.Log;

public class LoginScreen extends Screen {
    private final Screen parentScreen;
    private int startedTicks;
    private Button button;
    private Thread getTokenThread;
    private boolean isDelaying = false;
    private int delayedTicks = 0;
    private String[] tokens;

    public LoginScreen(Screen lastScreen) {
        super(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.title"));
        parentScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        button =
                Button.builder(
                        Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_0"),
                        this::onPress
                )
                .bounds(
                        width / 2 - 100,
                        height / 2 - 12,
                        200,
                        20)
                .build();
        addRenderableWidget(button);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderDirtBackground(graphics);
        graphics.drawString(
                font,
                Component.translatable("gui.openlink_chmlfrp_extension.login_screen.info"),
                width / 2 - font.width(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.info")) / 2,
                height / 2 + 12,
                0xFFFFFF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        startedTicks++;
        if (isDelaying) {
            delayedTicks++;
            if (delayedTicks >= 30) minecraft.setScreen(parentScreen);
        }

        if (!getTokenThread.isAlive() && tokens.length == 2) {
            button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_success"));
            OpenlinkChmlfrpExtension.PREFERENCES.put("access_token", tokens[0]);
            OpenlinkChmlfrpExtension.PREFERENCES.put("refresh_token", tokens[1]);
        }
    }

    @Override
    public void onClose() {
        getTokenThread.interrupt();
        minecraft.setScreen(parentScreen);
    }

    private void onPress(Button button) {
        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_1"));
        String[] deviceCodes;
        try {
            deviceCodes = LoggingManagement.fetchDeviceCode();
        } catch (Exception e) {
            button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
            isDelaying = true;
            return;
        }

        Util.getPlatform().openUri(deviceCodes[1]);
        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2"));

        startedTicks = 0;
        getTokenThread = new Thread(() -> {
            while (true) {
                if (startedTicks % 100 == 0) {
                    try {
                        tokens = LoggingManagement.intervalToken(deviceCodes[0]);
                    } catch (Exception e) {
                        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                        return;
                    }

                    if (tokens[0].equals("authorization_pending")) continue;
                    return;
                }
            }
        });
        getTokenThread.start();
    }
}
