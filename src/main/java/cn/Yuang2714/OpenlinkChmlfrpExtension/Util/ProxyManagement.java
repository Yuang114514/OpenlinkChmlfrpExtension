package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ProxyManagement {
    public static String createProxy(int i, @Nullable String s) throws Exception {
        /*
        {
    "token": "labor ut do lore", DID
    "tunnelname": "openlink_...",DID
    "node": "",                  DID
    "localip": "127.0.0.1",      DID
    "porttype": tcp",            DID
    "localport": 70,             DID
    "remoteport": 93,
    "encryption": true,          DID
    "compression": true          DID
}
         */
            JsonObject postQuery = new JsonObject();
            postQuery.addProperty("tunnelname", "openlink_mc_" + i);
            postQuery.addProperty("localip", "127.0.0.1");
            postQuery.addProperty("localport", i);
            postQuery.addProperty("porttype", "tcp");
            postQuery.addProperty("encryption", true);
            postQuery.addProperty("compression", true);

            String token = OpenlinkChmlfrpExtension.PREFERENCES.get("token", "Invalid Token");
            if (token.equals("Invalid Token"))
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

            String[] portRangeStr = JsonParser.parseString(Network.get(URLs.api +
                            "nodeinfo?token=" +
                            OpenlinkChmlfrpExtension.PREFERENCES.get("token", "Invalid Token") +
                            "&node=" +
                            preferNodeName))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonObject()
                    .get("rport")
                    .getAsJsonPrimitive()
                    .getAsString()
                    .split("-", 2);
            int[] portRange = new int[2];
            for (int j = 0; j < 2; j++) portRange[j] = Integer.parseInt(portRangeStr[j]);
            Random randomer = new Random(System.currentTimeMillis());
            postQuery.addProperty("remoteport", randomer.nextInt(portRange[0], portRange[1]));

            return JsonParser.parseString(Network.post(URLs.api + "/create_tunnel", postQuery))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonObject()
                    .get("dorp")
                    .getAsJsonPrimitive()
                    .getAsString();
    }
}
