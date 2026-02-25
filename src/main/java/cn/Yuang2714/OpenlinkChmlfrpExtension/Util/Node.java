package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

public class Node {
    public int id,group,bandwidthUsage,cpuUsage;
    public String name,description,location;
    public boolean ipv6, inChina;
    public Node(int id, int group, int bandwidthUsage, int cpuUsage, String name, String description, String location, boolean ipv6, boolean inChina) {
        this.id = id;
        this.group = group;
        this.bandwidthUsage = bandwidthUsage;
        this.cpuUsage = cpuUsage;
        this.name = name;
        this.description = description;
        this.location = location;
        this.ipv6 = ipv6;
        this.inChina = inChina;
    }
}