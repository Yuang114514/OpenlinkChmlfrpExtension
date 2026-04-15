package cn.yuang2714.openlink_chmlfrp_extension.datatypes;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

public class Node {
    public int id, group, bandwidthUsage, cpuUsage;
    public String name, description, location;
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
    
    @Override
    public String toString() {
        return
                "{" +
                "id=" + id +
                ", group=" + group +
                ", bandwidthUsage=" + bandwidthUsage +
                ", cpuUsage=" + cpuUsage +
                ", ipv6=" + ipv6 +
                ", inChina=" + inChina +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}