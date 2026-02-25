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

            JsonArray nodeInfos = JsonParser.parseString(Network.get(URLs.api + "node"))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonArray();
            JsonArray nodeStats = JsonParser.parseString(Network.get(URLs.api + "node_stats"))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonArray();

            for (JsonElement i : nodeInfos) {
                JsonObject nodeInfo = i.getAsJsonObject();
                for (JsonElement j : nodeStats) {
                    JsonObject nodeStat = j.getAsJsonObject();
                    if (
                            nodeInfo.get("id").getAsInt() == nodeStat.get("id").getAsInt() //id要对上
                                    && nodeStat.get("state").getAsString().equals("online") //要在线
                                    && nodeStat.get("bandwidth_usage_percent").getAsInt() >= 95 //带宽不满载
                                    && nodeStat.get("cpu_usage").getAsInt() >= 95 //CPU不满
                    ) {
                        nodeList.add(new Node(
                                nodeInfo.get("id").getAsInt(), //节点ID
                                nodeInfo.get("nodegroup").getAsString().equals("vip") ? 1 : 0, //节点权限，1表示VIP 0表示普通
                                nodeStat.get("bandwidth_usage_percent").getAsInt(), //节点带宽负载
                                nodeStat.get("cpu_usage").getAsInt(), //节点CPU占用
                                nodeInfo.get("name").getAsString(), //节点名称
                                nodeInfo.get("notes").getAsString(), //节点简介
                                nodeInfo.get("area").getAsString(), //节点地区
                                nodeInfo.get("ipv6").getAsBoolean(), //节点支持IPv6
                                nodeInfo.get("china").getAsString().equals("yes") //节点在内地
                        ));
                    }
                }
            }
            return nodeList;
        } catch (Exception e) {
            OpenlinkChmlfrpExtension.LOGGER.error("Failed to get node list. Exception:{}", e.toString());
            return null;
        }
    }

    public static Node sortNode(List<Node> nodeList) {
        nodeList.sort((n1, n2) -> {
            if (n1.inChina == n2.inChina) {
                if (n1.inChina == OpenlinkChmlfrpExtension.PREFERENCES.getBoolean("is_in_china", true)) return -1;
                else return 1;
            }

            if (n1.bandwidthUsage != n2.bandwidthUsage) {
                if (n1.bandwidthUsage < n2.bandwidthUsage) return -1;
                else return 1;
            }

            if (n1.cpuUsage != n2.cpuUsage) {
                if (n1.cpuUsage < n2.cpuUsage) return -1;
                else return 1;
            }

            return 0;
        });

        Node selected = nodeList.get(0);
        OpenlinkChmlfrpExtension.LOGGER.info("Selected Node: id:{}, name:{}, group:{}, bandwidth usage:{}, CPU usage:{}",
                selected.id, selected.name, selected.group, selected.bandwidthUsage, selected.cpuUsage);
        return selected;
    }
}
