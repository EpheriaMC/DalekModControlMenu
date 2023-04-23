package org.theplaceholder.dmcm;

import net.minecraft.util.ResourceLocation;

import static org.theplaceholder.dmcm.DMCM.gson;

public class Waypoint {
    private String name;

    private int x;
    private int y;
    private int z;
    private int dimension;

    public Waypoint(String name, int x, int y, int z, int dimension){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public String toJson(){
        return gson.toJson(this);
    }

    public static Waypoint fromJson(String json){
        return gson.fromJson(json, Waypoint.class);
    }
}
