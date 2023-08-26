package org.theplaceholder.dmcm.event;

import com.swdteam.client.tardis.data.ClientTardisCache;
import com.swdteam.client.tardis.data.ClientTardisFlightCache;
import com.swdteam.common.tardis.TardisData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.theplaceholder.dmcm.DMCM;
import org.theplaceholder.dmcm.handlers.CoordHandler;
import org.theplaceholder.dmcm.handlers.DimHandler;
import org.theplaceholder.dmcm.screen.DMCMScreen;

import static org.theplaceholder.dmcm.DMCM.modid;

@Mod.EventBusSubscriber(modid = modid, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBus {
    private static final int TICKS_BETWEEN_ACTIONS = 8;
    private static int ticks = 0;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (DMCM.key.isDown() && mc.player != null){
            TardisData tardisData = ClientTardisCache.getTardisData(mc.player.blockPosition());
            if (tardisData != null) {
                mc.setScreen(new DMCMScreen());
            } else {
                mc.player.displayClientMessage(new StringTextComponent("Waiting for ClientTardisCache to Update"), true);
            }
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.CLIENT) {
            ticks++;
            if (ticks >= TICKS_BETWEEN_ACTIONS) {
                if (DimHandler.isRunning) {
                    DimHandler.tick();
                }else if (CoordHandler.isRunning) {
                    CoordHandler.tick();
                }

                ticks = 0;
            }
        }
    }
}