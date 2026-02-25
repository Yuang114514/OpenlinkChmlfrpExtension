package cn.Yuang2714.OpenlinkChmlfrpExtension;

import cn.Yuang2714.OpenlinkChmlfrpExtension.Util.FrpcManagement;
import cn.Yuang2714.OpenlinkChmlfrpExtension.GUI.LoginScreen;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.*;
import fun.moystudio.openlink.frpc.Frpc;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;

public class ChmlfrpFrpcImpl implements Frpc {
    Logger logger = OpenlinkChmlfrpExtension.LOGGER;

    public boolean isArchive() {
        return true;
    }

    public List<String> getUpdateFileUrls() {
        return FrpcManagement.getUpdateFileUrls();
    }

    public void init() throws Exception {
        logger.info("Initializing ChmlfrpFrpcImpl");
        FrpcManagement.initUserEnv();
    }

    public String id() {
        return FrpcImplInfo.id;
    }

    public String name() {
        return FrpcImplInfo.name;
    }

    public boolean isOutdated(@Nullable Path path) {
        return !FrpcManagement.comparateFrpcVersion(path);
    }

    public Process createFrpcProcess(Path path, int i, @Nullable String s) throws Exception {
        return null;
    }

    public String createProxy(int i, @Nullable String s) throws Exception {
        return "";
    }

    public String getFrpcVersion(Path path) {
        return FrpcManagement.getCurrentFrpcVersion(path);
    }

    public void stopFrpcProcess(@Nullable Process frpcProcess) {
        Frpc.super.stopFrpcProcess(frpcProcess);
    }

    public Screen getNodeSelectionScreen(@Nullable Screen lastScreen) {
        return Frpc.super.getNodeSelectionScreen(lastScreen);
    }

    public Screen getLoginScreen(@Nullable Screen lastScreen) {
        return new LoginScreen(lastScreen);
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(OpenlinkChmlfrpExtension.MODID,"textures/chmlfrp_icon.png");
    }

    public boolean isLoggedIn() {
        return Frpc.super.isLoggedIn();
    }

    public void logOut() {
        Frpc.super.logOut();
    }

    public String getPanelUrl() {
        return URLs.panel;
    }
}