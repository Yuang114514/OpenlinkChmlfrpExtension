package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ProxyManagement {
    private static int caughtPort = -1;
    private static int caughtProxyId = -1;

    public static String createProxy(int localPort, @Nullable String remotePort) throws Exception {
        OpenlinkChmlfrpExtension.LOGGER.info("Creating proxy...");
        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("chat.openlink_chmlfrp_extension.creating_proxy.ing").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC));

        JsonObject postQuery = new JsonObject();
        postQuery.addProperty("tunnelname", "openlink_mc_" + localPort);
        postQuery.addProperty("localip", "127.0.0.1");
        postQuery.addProperty("localport", localPort);
        postQuery.addProperty("porttype", "tcp");
        postQuery.addProperty("encryption", true);
        postQuery.addProperty("compression", true);

        String token = OpenlinkChmlfrpExtension.PREFERENCES.get("token", "InvalidToken");
        if (token.equals("InvalidToken"))
            throw new NullPointerException("[Openlink Chmlfrp Extension] You have not logged in.");
        postQuery.addProperty("token", token);

        int preferNodeId = OpenlinkChmlfrpExtension.PREFERENCES.getInt("last_node", -1);
        String preferNodeName = "Not Selected";
        List<Node> nodes = NodeUtil.genNodeList();
        if (nodes == null) throw new NullPointerException("[Openlink Chmlfrp Extension] Unable to get Node List.");
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
        postQuery.addProperty("node", preferNodeName);

        JsonObject preferNodeApiInfo = JsonParser.parseString(Network.get(URLs.api +
                        "nodeinfo?token=" +
                        token +
                        "&node=" +
                        preferNodeName))
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

        for (int j = 0; j < 5; j++) {
            postQuery.addProperty("remoteport", preferRemotePort);
            try {
                caughtPort = preferRemotePort;
                return preferNodeApiInfo.get("ip")
                        .toString()
                        .replace("\"", "")
                        + ":" +
                        JsonParser.parseString(Network.post(URLs.api + "create_tunnel", postQuery.toString()))
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

        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("chat.openlink_chmlfrp_extension.creating_proxy.port_not_found")
                .withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.ITALIC));
        throw new RuntimeException("Proxy Creation Failed with no possible remote port after 5 tries");
    }

    public static int getProxyIdByPort(@Nullable String localPort, @Nullable String remotePort, String token) throws Exception {
        JsonArray userProxies = JsonParser.parseString(Network.get(URLs.api + "tunnel?token=" + token))
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

    public static void deleteProxy(int id, String token) throws Exception {
        Network.post(
                URLs.api
                + "delete_tunnel?token="
                + token
                + "&tunnelid="
                + id
                , null
        );
    }
}
