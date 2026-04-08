package cn.yuang2714.openlink_chmlfrp_extension.gui;

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.tools.LoggingManagement;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class LoginScreen extends Screen {
    private final Screen parentScreen;
    private Button button;
    private Thread getTokenThread;
    private boolean isDelaying = false;
    private boolean isLoggedIn = false;
    private int delayedTicks = 0;
    private String[] tokens;
    private String[] deviceCodes;

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
                        height / 2 + 12,
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
                height / 2 - 30,
                0xFFFFFF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        if (isDelaying) {
            delayedTicks++;
            if (delayedTicks >= 30) minecraft.setScreen(parentScreen);
        }

        if (getTokenThread != null && !getTokenThread.isAlive() && tokens.length == 3 && !isLoggedIn) {
            button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_success"));
            LoggingManagement.login(tokens[0], tokens[1], Integer.parseInt(tokens[2]));
            isDelaying = true;
            isLoggedIn = true;
        }
    }

    @Override
    public void onClose() {
        getTokenThread.interrupt();
        minecraft.setScreen(parentScreen);
    }

    private void onPress(Button button) {
        button.active = false;
        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_1"));
        try {
            deviceCodes = LoggingManagement.fetchDeviceCode();
        } catch (Exception e) {
            button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
            isDelaying = true;
            return;
        }

        Util.getPlatform().openUri(deviceCodes[1]);
        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2"));

        getTokenThread = new Thread(this::interval);
        getTokenThread.start();
    }

    private void interval() {
        while (true) {
            try {
                tokens = LoggingManagement.intervalToken(deviceCodes[0]);
            } catch (Exception e) {
                button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                isDelaying = true;
                return;
            }

            if (tokens[0].equals("authorization_pending")) {
                tokens = null;

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    return;
                }
                continue;
            }

            OpenlinkChmlfrpExtension.LOGGER.info("Got Token");
            return;
        }
    }
}
