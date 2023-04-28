package org.theplaceholder.dmcm;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import org.theplaceholder.dmcm.utils.Waypoint;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import static org.theplaceholder.dmcm.DMCM.modid;

@Mod(modid)
public class DMCM {

    public static final String modid = "dmcm";
    public static final KeyBinding key = new KeyBinding("key." + modid + ".openui", KeyEvent.VK_Y, "key.category." + modid);
    public static final File file = new File(Minecraft.getInstance().gameDirectory, "dmcm-waypoints.json");
    public static Waypoint.WaypointsList waypointsList = new Waypoint.WaypointsList();


    public static final Gson gson = new Gson();

    public DMCM() throws IOException {
        if (!file.exists())
            file.createNewFile();
    }
}
