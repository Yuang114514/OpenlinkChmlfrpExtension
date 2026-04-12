package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.Node;
import cn.yuang2714.openlink_chmlfrp_extension.statics.URLs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ProxyManagement {
    private static int caughtPort = -1;
    private static int caughtProxyId = -1;
    static Logger logger = LogUtils.getLogger();

    public static String createProxy(int localPort, @Nullable String remotePort) throws Exception {
        logger.info("Creating proxy...");
        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("chat.openlink_chmlfrp_extension.creating_proxy.ing").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));

        JsonObject postQuery = new JsonObject();
        postQuery.addProperty("tunnelname", "openlink_mc_" + localPort);
        postQuery.addProperty("localip", "127.0.0.1");
        postQuery.addProperty("localport", localPort);
        postQuery.addProperty("porttype", "tcp");
        postQuery.addProperty("encryption", true);
        postQuery.addProperty("compression", true);
        
        int preferNodeId = OpenlinkChmlfrpExtension.PREFERENCES.getInt("last_node", -1);
        String preferNodeName = "Not Selected";
        List<Node> nodes;
        try {
            nodes = NodeUtil.genNodeList();
        } catch (Exception e) {
            throw new NullPointerException("[Openlink Chmlfrp Extension] Unable to get Node List.");
        }
        if (preferNodeId == -1) preferNodeName = NodeUtil.sortNode(nodes).name;
        else {
            for (Node nodeInList : nodes) {
                if (nodeInList.id == preferNodeId) {
                    preferNodeName = nodeInList.name;
                }
            }
        }
        if (preferNodeName.equals("Not Selected"))
            throw new NullPointerException("[Openlink Chmlfrp Extension] Node not found in got node list.");
        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("chat.openlink_chmlfrp_extension.creating_proxy.got_node", preferNodeId));
        postQuery.addProperty("node", preferNodeName);

        JsonObject preferNodeApiInfo = JsonParser.parseString(Network.get(URLs.api +
                        "nodeinfo?node="
                        + preferNodeName,
                        true))
                .getAsJsonObject()
                .get("data")
                .getAsJsonObject();
        int preferRemotePort;
        Random randomer = new Random(System.currentTimeMillis());
        int[] portRange = new int[2];
        if (remotePort != null && !remotePort.isBlank()) {
            preferRemotePort = Integer.parseInt(remotePort);
        } else {
            String[] portRangeStr = preferNodeApiInfo
                    .get("rport")
                    .toString()
                    .replace("\"", "")
                    .split("-", 2);
            for (int j = 0; j < 2; j++) portRange[j] = Integer.parseInt(portRangeStr[j]);
            preferRemotePort = randomer.nextInt(portRange[0], portRange[1]);
        }

        for (int j = 0; j < OpenlinkChmlfrpExtension.PREFERENCES.getInt("config_max_retry", 5); j++) {
            postQuery.addProperty("remoteport", preferRemotePort);
            try {
                logger.info("Trying to create proxy. Attempt {}, Remote port:{}", j+1, preferRemotePort);
                caughtPort = preferRemotePort;
                return preferNodeApiInfo.get("ip")
                        .toString()
                        .replace("\"", "")
                        + ":" +
                        JsonParser.parseString(Network.post(URLs.api + "create_tunnel", postQuery.toString(), Network.CONTENT_TYPE_JSON, true))
                                .getAsJsonObject()
                                .get("data")
                                .getAsJsonObject()
                                .get("dorp")
                                .toString()
                                .replace("\"", "");
            } catch (IOException ignored) {}
            postQuery.remove("remoteport");
            preferRemotePort = randomer.nextInt(portRange[0], portRange[1]);
        }

        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("chat.openlink_chmlfrp_extension.creating_proxy.port_not_found", OpenlinkChmlfrpExtension.PREFERENCES.getInt("config_max_retry", 5))
                .withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.ITALIC));
        throw new Exception("Proxy Creation Failed with no possible remote port after some tries");
    }

    public static int getProxyIdByPort(@Nullable String localPort, @Nullable String remotePort) throws Exception {
        JsonArray userProxies = JsonParser.parseString(Network.get(URLs.api + "tunnel", true))
                .getAsJsonObject()
                .get("data")
                .getAsJsonArray();

        int possibleRemotePort = -1;
        if (caughtPort != -1) possibleRemotePort = caughtPort;
        if (remotePort != null && !remotePort.isBlank()) possibleRemotePort = Integer.parseInt(remotePort);

        for (JsonElement proxyAsElement : userProxies) {
            JsonObject proxy = proxyAsElement.getAsJsonObject();

            if (
                    (localPort == null || proxy.get("nport").getAsInt() == Integer.parseInt(localPort)) &&
                    (possibleRemotePort == -1 || proxy.get("dorp").getAsInt() == possibleRemotePort) &&
                    proxy.get("name").toString().matches("\"openlink_mc_\\d*\"")
            ) return caughtProxyId = proxy.get("id").getAsInt();
        }

        if (caughtProxyId != -1) return caughtProxyId;
        throw new NullPointerException("Failed to get Proxy by id.");
    }

    public static void deleteProxy(int id) throws Exception {
        Network.post(
                URLs.api
                + "delete_tunnel?tunnelid="
                + id,
                null,
                Network.CONTENT_TYPE_JSON,
                true);
    }
}
