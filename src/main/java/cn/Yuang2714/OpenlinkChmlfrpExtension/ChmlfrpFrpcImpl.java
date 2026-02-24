package cn.Yuang2714.OpenlinkChmlfrpExtension;

import cn.Yuang2714.OpenlinkChmlfrpExtension.FrpcUtil.FrpcManagement;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.FrpcImplInfo;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
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
        return Frpc.super.getUpdateFileUrls();
    }

    public void init() throws Exception {
        FrpcManagement.initUserEnv();
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
        return Frpc.super.getLoginScreen(lastScreen);
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(Openlink_chmlfrp_extension.MODID,"textures/chmlfrp_icon.png");
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