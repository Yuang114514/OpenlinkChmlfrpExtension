package cn.Yuang2714.OpenlinkChmlfrpExtension;

import cn.Yuang2714.OpenlinkChmlfrpExtension.GUI.LoginScreen;
import cn.Yuang2714.OpenlinkChmlfrpExtension.GUI.NodeSelectionScreen;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.FrpcImplInfo;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.FrpcManagement;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.LoggingManagement;
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
                LoggingManagement.checkToken(OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken")));
        OpenlinkChmlfrpExtension.PREFERENCES.putBoolean("is_in_china",
                LoggingManagement.userIsInChina());
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
        //TODO:IMPL
        return null;
    }

    public String createProxy(int i, @Nullable String s) throws Exception {
        //TODO:IMPL
        return "";
    }

    public String getFrpcVersion(Path path) {
        return FrpcManagement.getCurrentFrpcVersion(path);
    }

    public void stopFrpcProcess(@Nullable Process frpcProcess) {
        //TODO:IMPL
        Frpc.super.stopFrpcProcess(frpcProcess);
    }

    public Screen getNodeSelectionScreen(@Nullable Screen lastScreen) {
        return new NodeSelectionScreen(lastScreen);
    }

    public Screen getLoginScreen(@Nullable Screen lastScreen) {
        return new LoginScreen(lastScreen);
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(OpenlinkChmlfrpExtension.MODID,"textures/chmlfrp_icon.png");
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