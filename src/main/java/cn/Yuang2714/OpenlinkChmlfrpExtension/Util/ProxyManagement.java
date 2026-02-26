package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProxyManagement {
    public static String createProxy(int i, @Nullable String s) throws NullPointerException {
        /*
        {
    "token": "labor ut do lore", DID
    "tunnelname": "openlink_...",DID
    "node": "",
    "localip": "127.0.0.1",
    "porttype": tcp",
    "localport": 70,             DID
    "remoteport": 93,
    "encryption": true,
    "compression": true
}
         */
        JsonObject postQuery = new JsonObject();
        postQuery.addProperty("tunnelname", "openlink_mc_" + i);
        postQuery.addProperty("localport", i);

        String token = OpenlinkChmlfrpExtension.PREFERENCES.get("token", "Invalid Token");
        if (token.equals("Invalid Token")) throw new NullPointerException("[Openlink Chmlfrp Extension] You have not logged in.");
        postQuery.addProperty("token", token);

        int preferNodeId = OpenlinkChmlfrpExtension.PREFERENCES.getInt("last_node", -1);
        String preferNodeName = "Not Selected";
        List<Node> nodes = NodeUtil.genNodeList();
        if (nodes == null) throw new NullPointerException("[Openlink Chmlfrp Extension] Unable to get Node List.");
        for (Node nodeInList : nodes) {
            if (nodeInList.id == preferNodeId) {
                preferNodeName = nodeInList.name;
            }
        }
        if (preferNodeName.equals("Not Selected")) throw new NullPointerException("[Openlink Chmlfrp Extension] Node not found in got node list.");
        postQuery.addProperty("node", preferNodeName);

    }
}
