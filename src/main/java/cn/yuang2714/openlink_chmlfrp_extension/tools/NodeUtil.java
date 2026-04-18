package cn.yuang2714.openlink_chmlfrp_extension.tools;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

import cn.yuang2714.openlink_chmlfrp_extension.OpenlinkChmlfrpExtension;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.AdvancedNode;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.Location;
import cn.yuang2714.openlink_chmlfrp_extension.datatypes.Node;
import cn.yuang2714.openlink_chmlfrp_extension.statics.URLs;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeUtil {
    static Logger logger = Utils.genLogger();

    public static List<Node> genNodeList() throws Exception {
        try {
            List<Node> nodeList = new ArrayList<>();

            JsonArray nodeInfos = JsonParser.parseString(Network.get(URLs.api + "node", true))
                    .getAsJsonObject()
                    .get("data")
                    .getAsJsonArray();
            JsonArray nodeStats = JsonParser.parseString(Network.get(URLs.api + "node_stats", true))
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

    public static List<AdvancedNode> toAdvancedNodeList(List<Node> baseList) throws Exception {
        try {
            CountDownLatch latch = new CountDownLatch(baseList.size());
            ExecutorService pool = Executors.newFixedThreadPool(5);
            List<AdvancedNode> advancedList = new CopyOnWriteArrayList<>();
            
            for (Node node : baseList) {
                pool.execute(() -> {
                    try {
                        JsonObject apiInfo = JsonParser.parseString(Network.get(URLs.api + "nodeinfo?node=" + node.name, true))
                                .getAsJsonObject()
                                .get("data")
                                .getAsJsonObject();
                        
                        String coordinatesStr = apiInfo.get("coordinates").getAsString();
                        Location coordinates;
                        if (coordinatesStr.equals("0,0") || coordinatesStr.isEmpty()) {
                            try {
                                coordinates = URLs.exchangeLocation(node.location);
                            } catch (Exception e) {
                                coordinates = Location.impossible();
                                logger.warn("Failed to exchange location for node {} (id:{}), using impossible coordinates. Exception:{}", node.name, node.id, e.toString());
                                Utils.printExceptionStackTrace(logger, e);
                            }
                        } else {
                            coordinates = new Location(
                                    Double.parseDouble(coordinatesStr.split(",",2)[0]),
                                    Double.parseDouble(coordinatesStr.split(",",2)[1])
                            );
                        }
                        
                        String domain = apiInfo.get("ip").getAsString();
                        int delay = Network.ping(domain);
                        advancedList.add(
                                new AdvancedNode(
                                        node.id,
                                        node.bandwidthUsage,
                                        node.cpuUsage,
                                        delay,
                                        node.name,
                                        node.description,
                                        node.location,
                                        domain,
                                        node.ipv6,
                                        node.inChina,
                                        coordinates
                                )
                        );
                        logger.info("Got details for node {} (id:{}): delay {}ms, coordinates {}, domain {}",
                                node.name, node.id, delay, coordinates, domain);
                    } catch (Exception e) {
                        logger.error("Failed to get details for node {} (id:{}), Skipping. Exception:{}", node.name, node.id, e.toString());
                        Utils.printExceptionStackTrace(logger, e);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            try {
                latch.await();
            } finally {
                pool.shutdown();
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
            if (n1.inChina != n2.inChina) {
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
        
        /*
        logger.info("Sorted Node List:");
        for (Node n : nodeList) {
            logger.info(n.toString());
        }
        */

        Node selected = nodeList.get(0);
        logger.info("Automatically Selected Node: id:{}, name:{}, group:{}, bandwidth usage:{}, CPU usage:{}",
                selected.id, selected.name, selected.group, selected.bandwidthUsage, selected.cpuUsage);
        return selected;
    }
    
    public static AdvancedNode sortAdvancedNode(List<AdvancedNode> nodeList) {
        nodeList.sort((n1, n2) -> {
            if (n1.delayMillis != n2.delayMillis && n1.delayMillis != -1 && n2.delayMillis != -1) {
                if (n1.delayMillis < n2.delayMillis) return -1;
                else return 1;
            }
            
            if (!n1.coordinates.equals(n2.coordinates)) {
                if (n1.coordinates.isImpossible()) return 1;
                if (n2.coordinates.isImpossible()) return -1;
                
                Location userLocation = new Location(
                        OpenlinkChmlfrpExtension.PREFERENCES.getDouble("lon", 0),
                        OpenlinkChmlfrpExtension.PREFERENCES.getDouble("lat", 0)
                );
                
                if (Utils.calculateDistance(n1.coordinates, userLocation) < Utils.calculateDistance(n2.coordinates, userLocation)) return -1;
                else return 1;
            }
            
            if (n1.inChina != n2.inChina) {
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
        
        logger.info("Sorted Advanced Node List:");
        for (AdvancedNode n : nodeList) {
            logger.info(n.toString());
        }

        AdvancedNode selected = nodeList.get(0);
        logger.info("Automatically Selected Node: id:{}, name:{}, group:{}, delay:{}ms, bandwidth usage:{}, CPU usage:{}",
                selected.id, selected.name, selected.group, selected.delayMillis, selected.bandwidthUsage, selected.cpuUsage);
        return selected;
    }
}
