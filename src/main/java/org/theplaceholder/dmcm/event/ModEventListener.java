package org.theplaceholder.dmcm.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.theplaceholder.dmcm.DMCM;

import static org.theplaceholder.dmcm.DMCM.modid;

@Mod.EventBusSubscriber(modid = modid, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventListener {
    @SubscribeEvent
    public static void onClientRegistry(FMLClientSetupEvent event){
        ClientRegistry.registerKeyBinding(DMCM.key);
    }
}
