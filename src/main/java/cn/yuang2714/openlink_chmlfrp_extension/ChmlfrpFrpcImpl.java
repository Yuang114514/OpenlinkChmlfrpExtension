package cn.yuang2714.openlink_chmlfrp_extension;

import cn.yuang2714.openlink_chmlfrp_extension.gui.LoadingNodeSelectionScreen;
import cn.yuang2714.openlink_chmlfrp_extension.gui.LoginScreen;
import cn.yuang2714.openlink_chmlfrp_extension.statics.FrpcImplInfo;
import cn.yuang2714.openlink_chmlfrp_extension.statics.URLs;
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
                OpenlinkChmlfrpExtension.PREFERENCES.getInt("expires_in", 0) > System.currentTimeMillis() / 1000);
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_in_china",
                LoggingManagement.userIsInChina());
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_vip",
                LoggingManagement.userIsVIP(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("has_real_named",
                LoggingManagement.userHasRealnamed(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
        Network.setUpCookieManager();
        try {
            OpenlinkChmlfrpExtension.PREFERENCES.flush();
        } catch (Exception e) {
            OpenlinkChmlfrpExtension.LOGGER.error("Failed to save in preferences on initializing. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(OpenlinkChmlfrpExtension.LOGGER, e);
            throw e;
        }
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
        return FrpcManagement.runFrpc(path, ProxyManagement.getProxyIdByPort(String.valueOf(i), s, token), token);
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

            String token = OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken");
            try {
                ProxyManagement.deleteProxy(ProxyManagement.getProxyIdByPort(null, null, token), token);
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