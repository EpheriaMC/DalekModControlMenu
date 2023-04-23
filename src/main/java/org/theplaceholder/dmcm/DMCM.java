package org.theplaceholder.dmcm;

import com.google.gson.Gson;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;

import java.awt.event.KeyEvent;

import static org.theplaceholder.dmcm.DMCM.modid;

@Mod(modid)
public class DMCM {

    public static final String modid = "dmcm";
    public static final KeyBinding key = new KeyBinding("key." + modid + ".openui", KeyEvent.VK_Y, "key.category." + modid);

    public static final Gson gson = new Gson();

    public DMCM() {}
}
