package cn.Yuang2714.OpenlinkChmlfrpExtension.Tools;

import cn.Yuang2714.OpenlinkChmlfrpExtension.OpenlinkChmlfrpExtension;
import cn.Yuang2714.OpenlinkChmlfrpExtension.Statics.URLs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeUtil {
    static Logger logger = LogUtils.getLogger();

    public static List<Node> genNodeList() throws Exception {
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
                                    && (OpenlinkChmlfrpExtension.PREFERENCES.getBoolean("has_real_named", false) || !nodeInfo.get("china").getAsString().equals("yes")) //实名认证要对上
                                    && (OpenlinkChmlfrpExtension.PREFERENCES.getBoolean("is_vip", false) || !nodeInfo.get("nodegroup").getAsString().equals("vip")) //VIP要对上
                                    && nodeStat.get("state").getAsString().equals("online") //要在线
                                    && nodeStat.get("bandwidth_usage_percent").getAsInt() <= 95 //带宽不满载
                                    && nodeStat.get("cpu_usage").getAsInt() <= 95 //CPU不满
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

            if (!nodeList.isEmpty()) return nodeList;
            else throw new NullPointerException("Unable to get any node???");
        } catch (Exception e) {
            logger.error("Failed to get node list. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            throw e;
        }
    }

    public static List<Node> genAdvancedNodeList() throws Exception {
        try {
            List<Node> baseList = genNodeList();
            if (baseList.isEmpty()) throw new NullPointerException("Unable to get any node???");

            List<Node> advancedList = new ArrayList<>();
            for (Node n : baseList) {
                if (n.group == 0 || OpenlinkChmlfrpExtension.PREFERENCES.getBoolean("is_vip", false)) continue;
                JsonObject nodeDetail = JsonParser.parseString(Network.get(
                        URLs.api
                        + "nodeinfo?token="
                        + OpenlinkChmlfrpExtension.PREFERENCES.get("token", "Invalid")
                        + "&node="
                        + n.name
                )).getAsJsonObject()
                        .get("data")
                        .getAsJsonObject();

                String[] coordinatesRaw = nodeDetail.get("coordinates").getAsString().split(",",2);
                double[] coordinates = new double[]{Double.parseDouble(coordinatesRaw[0]), Double.parseDouble(coordinatesRaw[1])};
                if (Arrays.equals(coordinates, new double[]{0.0, 0.0})) coordinates = URLs.exchangeLocation(n.location);

                advancedList.add(new Node(
                        n.id, //节点ID
                        n.bandwidthUsage, //节点带宽负载
                        n.cpuUsage, //节点CPU占用
                        coordinates[1], //纬度
                        coordinates[0], //经度
                        n.name, //节点名称
                        n.description, //节点简介
                        n.location, //节点地区
                        nodeDetail.get("ip").getAsString(), //节点域名
                        n.ipv6, //节点支持IPv6
                        n.inChina //节点在内地
                ));
            }
            return advancedList;
        } catch (Exception e) {
            logger.error("Failed to get advanced node list. Exception:{}", e.toString());
            Utils.printExceptionStackTrace(logger, e);
            throw e;
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

            if (n1.ipv6 != n2.ipv6) {
                if (n1.ipv6) return -1;
                else return 1;
            }

            return 0;
        });

        Node selected = nodeList.get(0);
        logger.info("Automatically Selected Node: id:{}, name:{}, group:{}, bandwidth usage:{}, CPU usage:{}",
                selected.id, selected.name, selected.group, selected.bandwidthUsage, selected.cpuUsage);
        return selected;
    }
}
