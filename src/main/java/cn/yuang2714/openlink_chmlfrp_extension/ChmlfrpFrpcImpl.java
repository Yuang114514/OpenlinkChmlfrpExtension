package cn.yuang2714.openlink_chmlfrp_extension;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.gui.LoadingNodeSelectionScreen;
import cn.yuang2714.openlink_chmlfrp_extension.gui.LoginScreen;
import cn.yuang2714.openlink_chmlfrp_extension.statics.*;
import cn.yuang2714.openlink_chmlfrp_extension.tools.*;
import fun.moystudio.openlink.frpc.Frpc;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

public class ChmlfrpFrpcImpl implements Frpc {

    public boolean isArchive() {
        return true;
    }

    public List<String> getUpdateFileUrls() {
        return FrpcManagement.getUpdateFileUrls();
    }

    public void init() throws Exception {
        OpenlinkChmlfrpExtension.LOGGER.info("Initializing ChmlfrpFrpcImpl");
        FrpcManagement.initUserEnv();
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_logged_in",
                OpenlinkChmlfrpExtension.PREFERENCES.getInt("expires_in", 0) > System.currentTimeMillis());
        LoggingManagement.reloadUserAddress();
        try {
            LoggingManagement.refreshUserInfo();
        } catch (Exception e) {
            OpenlinkChmlfrpExtension.LOGGER.error("Failed to reload your info. maybe you haven't logged in.");
        }
        Network.setUpCookieManager();
        Utils.flushPreferences(OpenlinkChmlfrpExtension.LOGGER, "initialization");
    }

    public String id() {
        return FrpcImplInfo.id;
    }

    public String name() {
        return FrpcImplInfo.name;
    }

    public boolean isOutdated(@Nullable Path path) {
        return FrpcManagement.comparateFrpcVersion(path);
    }

    public Process createFrpcProcess(Path path, int i, @Nullable String s) throws Exception {
        String token = OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken");
        return FrpcManagement.runFrpc(path, ProxyManagement.getProxyIdByPort(String.valueOf(i), s), token);
    }

    public String createProxy(int i, @Nullable String s) throws Exception {
        return ProxyManagement.createProxy(i, s);
    }

    public String getFrpcVersion(Path path) {
        return FrpcManagement.getCurrentFrpcVersion(path);
    }

    public void stopFrpcProcess(@Nullable Process frpcProcess) {
        if (frpcProcess != null) {
            frpcProcess.destroy();
            
            try {
                ProxyManagement.deleteProxy(ProxyManagement.getProxyIdByPort(null, null));
            } catch (Exception e) {
                OpenlinkChmlfrpExtension.LOGGER.error("Failed to delete proxy. Exception:{}", e.toString());
            }
        }
    }

    public Screen getNodeSelectionScreen(@Nullable Screen lastScreen) {
        return new LoadingNodeSelectionScreen(lastScreen);
    }

    public Screen getLoginScreen(@Nullable Screen lastScreen) {
        return new LoginScreen(lastScreen);
    }

    public ResourceLocation getIcon() {
        return ResourceLocation.fromNamespaceAndPath(OpenlinkChmlfrpExtension.MODID,"textures/chmlfrp_icon.png");
    }

    public boolean isLoggedIn() {
        return OpenlinkChmlfrpExtension.PREFERENCES.getBoolean("is_logged_in", false);
    }

    public void logOut() {
        LoggingManagement.logout();
    }

    public String getPanelUrl() {
        return URLs.panel;
    }
}