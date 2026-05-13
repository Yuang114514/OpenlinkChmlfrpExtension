package cn.yuang2714.openlink_chmlfrp_extension.gui;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.DeviceCode;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.IntervalledAccessToken;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.TokenIntervalFailedException;
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
    private IntervalledAccessToken tokens = null;
    private DeviceCode deviceCode = null;
    private Stats currentStat = Stats.WAITING;

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
                        this::onLoginButtonPress
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
                width / 2 - font.width(
                        Component.translatable("gui.openlink_chmlfrp_extension.login_screen.info")
                ) / 2,
                height / 2 - 30,
                0xFFFFFF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        
        switch (currentStat) {
            case FAILED -> {
                button.active = false;
                button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                isDelaying = true;
                return;
            }
            
            case STARTING_TO_FETCH_DEVICE_CODE -> {
                button.active = false;
                button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_1"));
                try {
                    deviceCode = LoggingManagement.fetchDeviceCode();
                } catch (Exception e) {
                    button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                    isDelaying = true;
                }
                currentStat = Stats.FETCHING_DEVICE_CODE;
                return;
            }
            
            case FETCHING_DEVICE_CODE -> {
                if (deviceCode != null) {
                    Util.getPlatform().openUri(deviceCode.verificationUriComplete());
                    button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2"));
                    currentStat = Stats.STARTING_INTERVAL_THREAD;
                }
                return;
            }
            
            case STARTING_INTERVAL_THREAD -> {
                button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2"));
                getTokenThread = new Thread(this::interval);
                getTokenThread.setName("Token Interval Thread");
                getTokenThread.start();
                currentStat = Stats.WAITING_FOR_AUTHORIZATION;
                return;
            }
        }
        
        if (isDelaying) {
            delayedTicks++;
            if (delayedTicks >= 30) minecraft.setScreen(parentScreen);
        }

        if (getTokenThread != null && !getTokenThread.isAlive() && tokens.length == 3 && !isLoggedIn) {
            try {
                LoggingManagement.login(tokens[0], tokens[1], Integer.parseInt(tokens[2]));
            } catch (Exception e) {
                button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                return;
            }
            button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_success"));
            isDelaying = true;
            isLoggedIn = true;
        }
    }

    @Override
    public void onClose() {
        getTokenThread.interrupt();
        minecraft.setScreen(parentScreen);
    }

    private void onLoginButtonPress(Button button) {
        currentStat = Stats.STARTING_TO_FETCH_DEVICE_CODE;
        button.active = false;
        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_1"));
        try {
            deviceCode = LoggingManagement.fetchDeviceCode();
        } catch (Exception e) {
            button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
            isDelaying = true;
            return;
        }

        Util.getPlatform().openUri(deviceCode[1]);
        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2"));

        getTokenThread = new Thread(this::interval);
        getTokenThread.setName("Token Interval Thread");
        getTokenThread.start();
    }

    @SuppressWarnings("BusyWait")
    private void interval() {
        int delay = 5000;
        while (true) {
            try {
                tokens = LoggingManagement.intervalToken(deviceCode[0]);
            } catch (TokenIntervalFailedException e) {
                switch (e.reason) {
                    case AUTHORIZATION_PENDING -> OpenlinkChmlfrpExtension.LOGGER.info("Authorization pending, waiting for user to authorize...");
                    case SLOW_DOWN -> {
                        OpenlinkChmlfrpExtension.LOGGER.warn("Polling too frequently, slowing down...");
                        delay += 500;
                    }
                    case EXPIRED_TOKEN -> {
                        OpenlinkChmlfrpExtension.LOGGER.error("Device code expired.");
                        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                        isDelaying = true;
                        return;
                    }
                    case ACCESS_DENIED -> {
                        OpenlinkChmlfrpExtension.LOGGER.error("User denied access.");
                        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                        isDelaying = true;
                        return;
                    }
                    case UNKNOWN -> {
                        OpenlinkChmlfrpExtension.LOGGER.error("Unknown error occurred during token interval.");
                        button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                        isDelaying = true;
                        return;
                    }
                }
            } catch (Exception e) {
                button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                isDelaying = true;
                return;
            }

            if (tokens[0].equals("slow_down")) delay += 500;
            
            if (tokens[0].equals("expired_token") || tokens[0].equals("access_denied")) {
                button.setMessage(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail"));
                isDelaying = true;
                return;
            }

            if (tokens[0].equals("authorization_pending")) {
                tokens = null;

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
                continue;
            }

            OpenlinkChmlfrpExtension.LOGGER.info("Got Token");
            return;
        }
    }
    
    private enum Stats {
        WAITING,
        STARTING_TO_FETCH_DEVICE_CODE,
        FETCHING_DEVICE_CODE,
        STARTING_INTERVAL_THREAD,
        WAITING_FOR_AUTHORIZATION,
        INTERVAL_TOKEN,
        WAITING_FOR_RETURN,
        FAILED
    }
}
