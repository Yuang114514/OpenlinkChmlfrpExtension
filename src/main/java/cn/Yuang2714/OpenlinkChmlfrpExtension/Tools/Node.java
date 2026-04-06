package cn.Yuang2714.OpenlinkChmlfrpExtension.Tools;

public class Node {
    public int id, group, bandwidthUsage, cpuUsage;
    public String name, description, location, domain="No Response";
    public boolean ipv6, inChina;
    public double lon=-1, lat=-1;//lon是经度lat是纬度

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

    public Node(int id, int bandwidthUsage, int cpuUsage, double lat, double lon, String name, String description, String location, String domain, boolean ipv6, boolean inChina) {
        this.id = id;
        this.bandwidthUsage = bandwidthUsage;
        this.cpuUsage = cpuUsage;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.description = description;
        this.location = location;
        this.domain = domain;
        this.ipv6 = ipv6;
        this.inChina = inChina;
    }

    boolean isAdvanced() {
        return lat != -1 && lon != -1 && !domain.equals("No Response");
    }
}