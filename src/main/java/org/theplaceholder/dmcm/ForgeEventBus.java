package org.theplaceholder.dmcm;

import com.swdteam.client.tardis.data.ClientTardisFlightCache;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static org.theplaceholder.dmcm.DMCM.modid;

@Mod.EventBusSubscriber(modid = modid, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBus {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        Minecraft mc = Minecraft.getInstance();

        if (DMCM.key.isDown() && mc.player != null && !(mc.screen instanceof DMCMScreen) && mc.level != null && Utils.getCoordPanelAroundPlayer(mc.player) != BlockPos.ZERO) {
            if (ClientTardisFlightCache.hasTardisFlightData(mc.player.blockPosition()))
                mc.setScreen(new DMCMScreen());
            else
                mc.player.displayClientMessage(new StringTextComponent("Waiting for ClientTardisFlightCache to update..."), true);
        }
    }
}
