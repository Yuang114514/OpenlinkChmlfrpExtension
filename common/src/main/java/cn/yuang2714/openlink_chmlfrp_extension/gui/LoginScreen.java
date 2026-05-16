package cn.yuang2714.openlink_chmlfrp_extension.gui;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.DeviceCode;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.IntervalledAccessToken;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.login.TokenIntervalFailedException;
import cn.yuang2714.openlink_chmlfrp_extension.tools.LoggingManagement;
import cn.yuang2714.openlink_chmlfrp_extension.tools.Utils;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class LoginScreen extends Screen {
    private final Screen parentScreen;
    private Button loginButton;
    private Component statusMessage = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.info");
    
    private Thread thread;
    
    private IntervalledAccessToken tokens = null;
    private DeviceCode deviceCode = null;
    private Stats currentStat = Stats.WAITING;
    
    private final Logger logger = Utils.genLogger();

    public LoginScreen(Screen lastScreen) {
        super(Component.translatable("gui.openlink_chmlfrp_extension.login_screen.title"));
        parentScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        loginButton =
                Button.builder(
                        Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_0"),
                        (btn) -> currentStat = Stats.STARTING_TO_FETCH_DEVICE_CODE
                )
                .bounds(
                        width / 2 - 100,
                        height / 2 + 12,
                        200,
                        20)
                .build();
        addRenderableWidget(loginButton);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderDirtBackground(graphics);
        graphics.drawString(
                font,
                statusMessage,
                width / 2 - font.width(
                        statusMessage
                ) / 2,
                height / 2 - 30,
                0xFFFFFF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        
        switch (currentStat) {
            case FAILED -> {
                loginButton.active = true;
                statusMessage = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_fail");
                currentStat = Stats.STARTING_RETURN;
            }
            
            case EXPIRED -> {
                loginButton.active = true;
                statusMessage = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_expired");
                currentStat = Stats.STARTING_RETURN;
            }
            
            case STARTING_TO_FETCH_DEVICE_CODE -> {
                loginButton.active = false;
                statusMessage = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_1");
                
                thread = new Thread(() -> {
                    try {
                        deviceCode = LoggingManagement.fetchDeviceCode();
                    } catch (Exception e) {
                        currentStat = Stats.FAILED;
                    }
                });
                thread.setName("Fetch Device Code Thread");
                thread.start();
                
                currentStat = Stats.FETCHING_DEVICE_CODE;
            }
            
            case FETCHING_DEVICE_CODE -> {
                if (deviceCode != null) {
                    thread.interrupt();
                    thread = null;
                    
                    Util.getPlatform().openUri(deviceCode.verificationUriComplete());
                    statusMessage = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2");
                    currentStat = Stats.STARTING_INTERVAL_THREAD;
                }
            }
            
            case STARTING_INTERVAL_THREAD -> {
                statusMessage = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_2");
                thread = new Thread(this::interval);
                thread.setName("Token Interval Thread");
                thread.start();
                currentStat = Stats.WAITING_FOR_AUTHORIZATION;
            }
            
            case WAITING_FOR_AUTHORIZATION -> {
                if (tokens != null) {
                    logger.info("Got Token");
                    
                    thread.interrupt();
                    thread = null;
                    
                    thread = new Thread(() -> {
                        try {
                            LoggingManagement.login(tokens);
                        } catch (Exception e) {
                            logger.error("Failed to login with access token.", e);
                            currentStat = Stats.FAILED;
                            return;
                        }
                        
                        statusMessage = Component.translatable("gui.openlink_chmlfrp_extension.login_screen.stat_success");
                        currentStat = Stats.STARTING_RETURN;
                    });
                    thread.setName("Login Thread");
                    thread.start();
                }
            }
            
            case STARTING_RETURN -> {
                removeWidget(loginButton);
                loginButton = Button.builder(
                                Component.translatable("gui.back"),
                                (btn) -> onClose()
                        )
                        .bounds(
                                width / 2 - 100,
                                height / 2 + 12,
                                200,
                                20)
                        .build();
                addRenderableWidget(loginButton);
                
                currentStat = Stats.WAITING_FOR_RETURN;
            }
        }
    }

    @Override
    public void onClose() {
        if (thread != null) thread.interrupt();
        minecraft.setScreen(parentScreen);
    }

    @SuppressWarnings("BusyWait")
    private void interval() {
        int delay = 5000;
        while (true) {
            try {
                tokens = LoggingManagement.intervalToken(deviceCode.deviceCode());
            } catch (TokenIntervalFailedException e) {
                switch (e.reason) {
                    case SLOW_DOWN -> {
                        logger.warn("Polling too frequently, slowing down...");
                        delay += 500;
                        continue;
                    }
                    
                    case AUTHORIZATION_PENDING -> {
                        logger.info("Authorization pending, waiting for user to authorize...");
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ex) {
                            currentStat = Stats.FAILED;
                            return;
                        }
                        continue;
                    }
                    
                    case EXPIRED_TOKEN -> {
                        logger.error("Device code expired.");
                        currentStat = Stats.EXPIRED;
                        return;
                    }
                    
                    case ACCESS_DENIED -> {
                        logger.error("User denied access.");
                        currentStat = Stats.FAILED;
                        return;
                    }
                    
                    case UNKNOWN -> {
                        logger.error("Unknown error occurred during token interval.");
                        currentStat = Stats.FAILED;
                        return;
                    }
                }
            } catch (Exception e) {
                currentStat = Stats.FAILED;
                return;
            }
            
            return;
        }
    }
    
    private enum Stats {
        WAITING,
        STARTING_TO_FETCH_DEVICE_CODE,
        FETCHING_DEVICE_CODE,
        STARTING_INTERVAL_THREAD,
        WAITING_FOR_AUTHORIZATION,
        STARTING_RETURN,
        WAITING_FOR_RETURN,
        FAILED,
        EXPIRED
    }
}
