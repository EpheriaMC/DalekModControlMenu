package org.theplaceholder.dmcm.event;

import com.swdteam.client.tardis.data.ClientTardisFlightCache;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

        if (shouldOpenDMCMScreen(mc)) {
            mc.setScreen(new DMCMScreen());
        }

        ticks++;
        if (ticks >= TICKS_BETWEEN_ACTIONS) {
            if (DimHandler.isRunning) {
                DimHandler.tick();
            }
            if (CoordHandler.isRunning) {
                CoordHandler.tick();
            }

            ticks = 0;
        }
    }

    private static boolean shouldOpenDMCMScreen(Minecraft mc) {
        return DMCM.key.isDown() && mc.player != null && !(mc.screen instanceof DMCMScreen) && mc.level != null &&
                ClientTardisFlightCache.hasTardisFlightData(mc.player.blockPosition());
    }
}