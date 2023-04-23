package org.theplaceholder.dmcm;

import com.swdteam.client.tardis.data.ClientTardisFlightCache;
import com.swdteam.common.block.RotatableTileEntityBase;
import com.swdteam.common.block.tardis.CoordPanelBlock;
import com.swdteam.common.tileentity.tardis.CoordPanelTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;

import java.awt.event.KeyEvent;
import java.util.Map;

import static org.theplaceholder.dmcm.DMCM.modid;

@Mod(modid)
public class DMCM {

    public static final String modid = "dmcm";
    public static final KeyBinding key = new KeyBinding("key." + modid + ".openui", KeyEvent.VK_Y, "key.category." + modid);

    public DMCM() {}

    public static boolean isCoordPanel() {
        return (Utils.getCoordPanelAroundPlayer(Minecraft.getInstance().player) == BlockPos.ZERO);
    }

    public static void handle(int x, int y, int z) throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Minecraft mc = Minecraft.getInstance();
        if (Utils.getCoordPanelAroundPlayer(mc.player) != BlockPos.ZERO) {


            BlockPos panelPos = Utils.getCoordPanelAroundPlayer(mc.player);
            CoordPanelTileEntity tile = (CoordPanelTileEntity) mc.player.level.getBlockEntity(panelPos);

            Direction dir = mc.level.getBlockState(panelPos).getValue(RotatableTileEntityBase.FACING);

            BlockPos tPos = ClientTardisFlightCache.getTardisFlightData(panelPos).getPos();

            Map<Integer, Integer> xList = Utils.getPowerMap(x);
            Map<Integer, Integer> yList = Utils.getPowerMap(y);
            Map<Integer, Integer> zList = Utils.getPowerMap(z);

            Map<Integer, Integer> xtList = Utils.getPowerMap(tPos.getX());
            Map<Integer, Integer> ytList = Utils.getPowerMap(tPos.getY());
            Map<Integer, Integer> ztList = Utils.getPowerMap(tPos.getZ());

            Hand hand = Hand.MAIN_HAND;

            Thread t = new Thread(() -> {
                try {
                    if(isCoordPanel()) return;

                    handleThread(tile, hand, xList, yList, zList, dir, xtList, ytList, ztList);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        }
    }

    public static void handleThread(CoordPanelTileEntity tile, Hand hand, Map<Integer, Integer> xList, Map<Integer, Integer> yList, Map<Integer, Integer> zList, Direction direction, Map<Integer, Integer> txList, Map<Integer, Integer> tyList, Map<Integer, Integer> tzList) throws NoSuchFieldException, IllegalAccessException {
        for(int p = 0; p <= 4; p++){
            int i = (int) Math.pow(10, p);

            int x = xList.getOrDefault(i, 0);
            int y = yList.getOrDefault(i, 0);
            int z = zList.getOrDefault(i, 0);
            int tx = txList.getOrDefault(i, 0);
            int ty = tyList.getOrDefault(i, 0);
            int tz = tzList.getOrDefault(i, 0);

            if(x != tz && y != ty && z != tz)
                setToInc(tile, hand, direction, i);

            if(isCoordPanel()) return;

            if (x > tx) pressCoord(x - tx, hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_X, tile);
            else pressCoord(x - tx, hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_X, tile);

            if(isCoordPanel()) return;

            if (y > ty) pressCoord(y - ty, hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_Y, tile);
            else pressCoord(y - ty, hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_Y, tile);

            if(isCoordPanel()) return;

            if (z > tz) pressCoord(z - tz, hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_Z, tile);
            else pressCoord(z - tz, hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_Z, tile);

            if(isCoordPanel()) return;
        }
    }

    public static void sleep(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void pressButton(Hand hand, BlockPos pos, Direction direction, CoordPanelBlock.CoordPanelButtons buttons) throws NoSuchFieldException, IllegalAccessException {
        if(isCoordPanel()) return;
        Minecraft.getInstance().getConnection().send(new CPlayerTryUseItemOnBlockPacket(hand, Utils.getButtonBlockRayTraceResult(pos, direction, buttons)));
        sleep();
    }

    public static void pressCoord(int num, Hand hand, Direction direction, CoordPanelBlock.CoordPanelButtons button, CoordPanelTileEntity tile) throws NoSuchFieldException, IllegalAccessException {
        for(int i = 0; i < Math.abs(num); i++){
            if(isCoordPanel()) return;
            pressButton(hand, tile.getBlockPos(), direction, button);
        }
    }

    public static void setToInc(CoordPanelTileEntity tile, Hand hand, Direction direction, int i) throws NoSuchFieldException, IllegalAccessException {
        while (tile.incrementValue != i){
            if(isCoordPanel()) return;
            pressButton(hand, tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.INCREMENT);
        }
    }
}
