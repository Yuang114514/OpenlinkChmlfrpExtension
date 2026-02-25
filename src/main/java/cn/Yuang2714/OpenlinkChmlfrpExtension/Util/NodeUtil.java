package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.StaticFields.URLs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class NodeUtil {
    public static List<Node> genNodeList() {
        try {
            List<Node> nodeList = new ArrayList<>();

            JsonArray nodes = JsonParser.parseString(Network.get(URLs.api + "node"))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonArray();

            for (JsonElement i : nodes) {
                JsonObject node = i.getAsJsonObject();
                nodeList.add(new Node(
                        node.get("id").getAsInt(), //节点ID
                        node.get("nodegroup").getAsString().equals("vip") ? 1 : 0, //节点权限，1表示VIP 0表示普通
                        node.get("name").getAsString(), //节点名称
                        node.get("notes").getAsString(), //节点简介
                        node.get("area").getAsString(), //节点地区
                        node.get("ipv6").getAsBoolean() //节点支持IPv6
                ));
            }
            return nodeList;
        } catch (Exception e) {
            OpenlinkChmlfrpExtension.LOGGER.error("Failed to get node list. Exception:{}", e.toString());
            return null;
        }
    }
}
