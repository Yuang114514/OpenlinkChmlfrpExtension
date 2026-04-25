package cn.yuang2714.openlink_chmlfrp_extension.datatypes;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

public class AdvancedNode extends Node {
    public Location coordinates;
    public String domain;
    public volatile int delayMillis;
    
    public AdvancedNode(int id, int bandwidthUsage, int cpuUsage, int delayMillis, String name, String description, String location, String domain, boolean ipv6, boolean inChina, Location coordinates) {
        super(id, 114514, bandwidthUsage, cpuUsage, name, description, location, ipv6, inChina);
        this.delayMillis = delayMillis;
        this.domain = domain;
        this.coordinates = coordinates;
    }
    
    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", group=" + group +
                ", bandwidthUsage=" + bandwidthUsage +
                ", cpuUsage=" + cpuUsage +
                ", ipv6=" + ipv6 +
                ", inChina=" + inChina +
                ", coordinates=" + coordinates.toString() +
                ", delayMillis=" + delayMillis +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
