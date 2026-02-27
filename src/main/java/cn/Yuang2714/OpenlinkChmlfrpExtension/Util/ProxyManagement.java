package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ProxyManagement {
    public static String createProxy(int i, @Nullable String s) throws Exception {
        OpenlinkChmlfrpExtension.LOGGER.info("Creating proxy...");
        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("chat.openlink_chmlfrp_extension.creating_proxy.ing"));

        JsonObject postQuery = new JsonObject();
        postQuery.addProperty("tunnelname", "openlink_mc_" + i);
        postQuery.addProperty("localip", "127.0.0.1");
        postQuery.addProperty("localport", i);
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
        if (s != null && !s.isBlank()) {
            preferRemotePort = Integer.parseInt(s);
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

        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("chat.openlink_chmlfrp_extension.creating_proxy.port_not_found"));
        throw new RuntimeException("Proxy Creation Failed with no possible remote port after 5 tries");
    }
}
