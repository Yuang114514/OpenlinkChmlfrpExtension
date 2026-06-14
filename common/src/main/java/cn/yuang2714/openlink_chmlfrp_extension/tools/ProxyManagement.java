package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.Node;
import cn.yuang2714.openlink_chmlfrp_extension.platform.PlatformServices;
import cn.yuang2714.openlink_chmlfrp_extension.statics.URLs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProxyManagement {
    private static int caughtPort = -1;
    static Logger logger = Utility.genLogger();
    
    @SuppressWarnings("BusyWait")
    public static String createProxy(int localPort, @Nullable String remotePort) throws Exception {
        logger.info("Creating proxy...");
        Minecraft.getInstance().gui.getChat().addMessage(Utility.translatableText("chat.openlink_chmlfrp_extension.creating_proxy.ing").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
        
        LoggingManagement.refreshUserInfo();
        if (
                OpenlinkChmlfrpExtension.PREFERENCES.getInt("current_tunnel_count", 0) + 1 >
                OpenlinkChmlfrpExtension.PREFERENCES.getInt("max_tunnel_count", 0)
        ) {
            Minecraft.getInstance().gui.getChat().addMessage(Utility.translatableText(
                    "chat.openlink_chmlfrp_extension.creating_proxy.maximum_tunnel_count_reached"
            ).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.ITALIC));
            throw new IllegalArgumentException("Maximum tunnel count reached. Please delete some tunnels before creating new ones.");
        }
        
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
        boolean isAdvanced = PlatformServices.CONFIG_PROVIDER.doAdvancedNodeSort().getter().get();
        try {
            nodes = NodeUtil.genNodeList();
        } catch (Exception e) {
            throw new NullPointerException("[Openlink Chmlfrp Extension] Unable to get Node List.");
        }
        //
        if (preferNodeId == -1) {
            Node autoNode = isAdvanced ? NodeUtil.sortAdvancedNode(NodeUtil.toAdvancedNodeList(nodes)) : NodeUtil.sortNode(nodes);
            preferNodeName = autoNode.name;
            preferNodeId = autoNode.id;
        }
        else {
            for (Node nodeInList : nodes) {
                if (nodeInList.id == preferNodeId) {
                    preferNodeName = nodeInList.name;
                }
            }
        }
        if (preferNodeName.equals("Not Selected"))
            throw new NullPointerException("[Openlink Chmlfrp Extension] Node not found in got node list.");
        Minecraft.getInstance().gui.getChat().addMessage(Utility.translatableText(
                "chat.openlink_chmlfrp_extension.creating_proxy.got_node",
                preferNodeId,
                preferNodeName
        ).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
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
        
        for (int j = 0; j < PlatformServices.CONFIG_PROVIDER.proxyCreationMaxRetryCount().getter().get(); j++) {
            postQuery.addProperty("remoteport", preferRemotePort);
            caughtPort = preferRemotePort;
            try {
                logger.info("Trying to create proxy. Attempt {}, Remote port:{}", j+1, preferRemotePort);
                Minecraft.getInstance().gui.getChat().addMessage(Utility.translatableText(
                        "chat.openlink_chmlfrp_extension.creating_proxy.trying",
                        j+1,
                        PlatformServices.CONFIG_PROVIDER.proxyCreationMaxRetryCount().getter().get(),
                        preferRemotePort
                ).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));
                
                String apiResponse = Network.post(URLs.api + "create_tunnel", postQuery.toString(), Network.CONTENT_TYPE_JSON, true);
                logger.debug("API Response: {}", apiResponse);
                return preferNodeApiInfo.get("ip")
                        .toString()
                        .replace("\"", "")
                        + ":" +
                        JsonParser.parseString(apiResponse)
                                .getAsJsonObject()
                                .get("data")
                                .getAsJsonObject()
                                .get("dorp")
                                .toString()
                                .replace("\"", "");
            } catch (Exception e) {
                logger.info("Failed to create proxy with remote port {}. Maybe it's occupied.", preferRemotePort);
                //Utility.printExceptionStackTrace(logger, e);
            }
            postQuery.remove("remoteport");
            preferRemotePort = randomer.nextInt(portRange[0], portRange[1]);
            Thread.sleep(1000);
        }

        Minecraft.getInstance().gui.getChat().addMessage(Utility.translatableText(
                "chat.openlink_chmlfrp_extension.creating_proxy.port_not_found",
                PlatformServices.CONFIG_PROVIDER.proxyCreationMaxRetryCount().getter().get()
        ).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.ITALIC));
        throw new Exception("Proxy Creation Failed with no possible remote port after some tries");
    }

    public static List<Integer> getProxyIdByPort(@Nullable Integer localPort, @Nullable String remotePort) throws Exception {
        JsonArray userProxies = JsonParser.parseString(Network.get(URLs.api + "tunnel", true))
                .getAsJsonObject()
                .get("data")
                .getAsJsonArray();

        //尝试使用caughtPort作为远程端口的回退方案，优先级低于传入的remotePort参数
        int possibleRemotePort = -1;
        try {
            if (remotePort != null) possibleRemotePort = Integer.parseInt(remotePort);
        } catch (NumberFormatException e) {
            if (caughtPort != -1) {
                possibleRemotePort = caughtPort;
                caughtPort = -1;
            }
        }
        
        logger.info("Searching for proxy IDs by Local port: {}, Remote port: {}", localPort, possibleRemotePort);
        //遍历用户代理列表，寻找匹配的代理ID
        List<Integer> proxyIds = new ArrayList<>();
        for (JsonElement proxyAsElement : userProxies) {
            JsonObject proxy = proxyAsElement.getAsJsonObject();

            if (
                    proxy.get("name").toString().matches("\"openlink_mc_\\d*\"") &&
                    (localPort == null || proxy.get("nport").getAsInt() == localPort) &&
                    (possibleRemotePort == -1 || proxy.get("dorp").getAsInt() == possibleRemotePort)
            ) {
                int id = proxy.get("id").getAsInt();
                logger.debug("Found proxy with id {} for local port {} and remote port {}", id, proxy.get("nport").getAsInt(), proxy.get("dorp").getAsInt());
                proxyIds.add(id);
                if (localPort != null) return proxyIds; //确定的localPort代表确定的proxyId，直接返回
            }
        }
        logger.info("Finished searching proxy IDs by port. Found {} possible matches.", proxyIds.size());
        if (!proxyIds.isEmpty()) return proxyIds;

        throw new NullPointerException("Failed to get Proxy by id.");
    }

    public static void clearProxy(List<Integer> ids) throws Exception {
        for (int id : ids) {
            logger.info("Deleting proxy with id {}...", id);
            Network.post(
                    URLs.api
                            + "delete_tunnel?tunnelid="
                            + id,
                    null,
                    Network.CONTENT_TYPE_JSON,
                    true);
        }
    }
}
