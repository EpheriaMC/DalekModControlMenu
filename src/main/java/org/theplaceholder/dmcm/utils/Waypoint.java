package org.theplaceholder.dmcm.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import org.theplaceholder.dmcm.screen.DMCMScreen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.theplaceholder.dmcm.DMCM.*;

public class Waypoint {
    public String name;

    public int x;
    public int y;
    public int z;
    public int dimension;

    public Waypoint(String name, int x, int y, int z, int dimension){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public static class WaypointButton extends Button {
        public Waypoint waypoint;
        public static int index = 0;

        public WaypointButton(Waypoint waypoint, DMCMScreen screen, int j){
            super((screen.width / 2) - 49, j - 24, 98, 20, new StringTextComponent(waypoint.name), (button) -> {
                screen.x.setValue(String.valueOf(waypoint.x));
                screen.y.setValue(String.valueOf(waypoint.y));
                screen.z.setValue(String.valueOf(waypoint.z));
                screen.dimList.setValue(waypoint.dimension);
                screen.selected = waypoint;
                screen.deleteMode = true;
            });
            this.x = (screen.width / 2) + 54;
            this.y = (screen.height / 4) + 48 + (24 * index);
            this.waypoint = waypoint;
            index ++;
        }

        public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
            super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        }
    }

    public static class WaypointsList {
        public WaypointsList(){
            waypoints = new ArrayList<>();
        }
        public List<Waypoint> waypoints;

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
            if (waypointsList == null) {
                waypointsList = new WaypointsList();
            }
        }
    }


}
