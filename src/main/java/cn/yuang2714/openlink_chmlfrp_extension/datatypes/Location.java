package cn.yuang2714.openlink_chmlfrp_extension.datatypes;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

public class Location {
    public double lon, lat;
    
    public Location(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }
    
    @Override
    public String toString() {
        return "{" +
                "lon=" + lon +
                ", lat=" + lat +
                '}';
    }
    
    public boolean equals(Location anotherLocation) {
        return this.lon == anotherLocation.lon && this.lat == anotherLocation.lat;
    }
    
    public static Location impossible() {
        return new Location(200,200);
    }
    
    public static boolean isImpossible(Location location) {
        return location.equals(impossible());
    }
}
