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

    public static void handle(int x, int y, int z) throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        Minecraft mc = Minecraft.getInstance();
        if (Utils.getCoordPanelAroundPlayer(mc.player) != BlockPos.ZERO) {
            BlockPos panelPos = Utils.getCoordPanelAroundPlayer(mc.player);
            CoordPanelTileEntity tile = (CoordPanelTileEntity) mc.player.level.getBlockEntity(panelPos);

            Direction dir = mc.level.getBlockState(panelPos).getValue(RotatableTileEntityBase.FACING);

            BlockPos tPos = ClientTardisFlightCache.getTardisFlightData(panelPos).getPos();

            System.out.println("x: " + x);
            System.out.println("y: " + y);
            System.out.println("z: " + z);

            Map<Integer, Integer> xList = Utils.getPowerMap(x);
            Map<Integer, Integer> yList = Utils.getPowerMap(y);
            Map<Integer, Integer> zList = Utils.getPowerMap(z);

            Map<Integer, Integer> xtList = Utils.getPowerMap(tPos.getX());
            Map<Integer, Integer> ytList = Utils.getPowerMap(tPos.getY());
            Map<Integer, Integer> ztList = Utils.getPowerMap(tPos.getZ());

            Hand hand = Hand.MAIN_HAND;

            Thread t = new Thread(() -> {
                try {
                    handleThread(tile, hand, xList, yList, zList, dir, xtList, ytList, ztList);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

            t.start();
        }
    }

    public static void handleThread(CoordPanelTileEntity tile, Hand hand, Map<Integer, Integer> xList, Map<Integer, Integer> yList, Map<Integer, Integer> zList, Direction direction, Map<Integer, Integer> txList, Map<Integer, Integer> tyList, Map<Integer, Integer> tzList) throws NoSuchFieldException, IllegalAccessException {
        System.out.println("xList: " + xList);
        System.out.println("yList: " + yList);
        System.out.println("zList: " + zList);
        for(int p = 0; p <= 4; p++){
            int i = (int) Math.pow(10, p);

            int x = xList.getOrDefault(i, 0);
            int y = yList.getOrDefault(i, 0);
            int z = zList.getOrDefault(i, 0);
            int tx = txList.getOrDefault(i, 0);
            int ty = tyList.getOrDefault(i, 0);
            int tz = tzList.getOrDefault(i, 0);

            if (x > tx) pressCoord(Math.abs(x - tx), hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_X, tile, p);
            else pressCoord(Math.abs(x - tx), hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_X, tile, p);

            if (y > ty) pressCoord(Math.abs(y - ty), hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_Y, tile, p);
            else pressCoord(Math.abs(y - ty), hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_Y, tile, p);

            if (z > tz) pressCoord(Math.abs(z - tz), hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_Z, tile, p);
            else pressCoord(Math.abs(z - tz), hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_Z, tile, p);
        }
    }

    public static void sleep(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void pressButton(Hand hand, BlockPos pos, Direction direction, CoordPanelBlock.CoordPanelButtons buttons) throws NoSuchFieldException, IllegalAccessException {
        Minecraft.getInstance().getConnection().send(new CPlayerTryUseItemOnBlockPacket(hand, Utils.getButtonBlockRayTraceResult(pos, direction, buttons)));
        sleep();
    }

    public static void pressCoord(int num, Hand hand, Direction direction, CoordPanelBlock.CoordPanelButtons button, CoordPanelTileEntity tile, int p) throws NoSuchFieldException, IllegalAccessException {
        setToInc(tile, hand, direction, p);

        num = Math.abs(num);

        for(int i = 0; i < num; i++){
            pressButton(hand, tile.getBlockPos(), direction, button);
        }
    }

    public static void setToInc(CoordPanelTileEntity tile, Hand hand, Direction direction, int p) throws NoSuchFieldException, IllegalAccessException {
        int j = tile.incrementValue;

        int k = 0;

        if (j == 1)
            k = 0;
        if (j == 10)
            k = 1;
        if (j == 100)
            k = 2;
        if (j == 1000)
            k = 3;
        if (j == 10000)
            k = 4;

        if(p != k){
            if(p < k){
                for(int l = 0; l < k - p; l++){
                    pressButton(hand, tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.INCREMENT);
                }
            } else {
                for(int l = 0; l < Math.abs(p - k); l++){
                    pressButton(hand, tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.INCREMENT);
                }
            }
        }
    }
}
