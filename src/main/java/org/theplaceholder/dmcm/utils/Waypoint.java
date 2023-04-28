package org.theplaceholder.dmcm.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.theplaceholder.dmcm.DMCM.*;

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

    public void remove(){
        waypointsList.waypoints.remove(this);
    }

    public static void remove(Waypoint waypoint){
        waypointsList.waypoints.remove(waypoint);
    }

    public static class WaypointsList {
        public WaypointsList(){}

        public List<Waypoint> waypoints = new ArrayList<>();

        public void save() throws IOException {
            String string = gson.toJson(this);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(string);
            writer.close();
        }

        public void load() throws IOException{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String string = reader.readLine();
            waypointsList = gson.fromJson(string, WaypointsList.class);
            reader.close();
        }
    }
}
