package cn.Yuang2714.OpenlinkChmlfrpExtension.Util;

public class Node {
    public int id,group;
    public String name,description,location;
    public boolean ipv6;
    public Node(int id, int group, String name, String description, String location, boolean ipv6) {
        this.id = id;
        this.group = group;
        this.name = name;
        this.description = description;
        this.location = location;
        this.ipv6 = ipv6;
    }
}