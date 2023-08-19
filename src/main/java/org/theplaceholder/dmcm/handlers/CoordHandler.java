package org.theplaceholder.dmcm.handlers;

import com.swdteam.client.tardis.data.ClientTardisFlightCache;
import com.swdteam.common.block.RotatableTileEntityBase;
import com.swdteam.common.block.tardis.CoordPanelBlock;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.tileentity.tardis.CoordPanelTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.theplaceholder.dmcm.interfaces.ButtonsAccessor;
import org.theplaceholder.dmcm.mixin.CoordPanelButtonsAccessor;
import org.theplaceholder.dmcm.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import static org.theplaceholder.dmcm.utils.ButtonUtils.pressButton;

public class CoordHandler{
    public static boolean isNoCoordPanel() {
        return (Utils.getBlockPanelAroundPlayer(Minecraft.getInstance().player, DMBlocks.COORD_PANEL.get()) == BlockPos.ZERO);
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

            setToInc(tile, hand, direction, i);

            if(isNoCoordPanel()) return;

            if (x > tx) pressCoord(x - tx, hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_X, tile);
            else pressCoord(x - tx, hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_X, tile);

            if(isNoCoordPanel()) return;

            if (y > ty) pressCoord(y - ty, hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_Y, tile);
            else pressCoord(y - ty, hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_Y, tile);

            if(isNoCoordPanel()) return;

            if (z > tz) pressCoord(z - tz, hand, direction, CoordPanelBlock.CoordPanelButtons.ADD_Z, tile);
            else pressCoord(z - tz, hand, direction, CoordPanelBlock.CoordPanelButtons.SUB_Z, tile);

            if(isNoCoordPanel()) return;
        }
    }

    public static void pressCoord(int num, Hand hand, Direction direction, CoordPanelBlock.CoordPanelButtons button, CoordPanelTileEntity tile) throws NoSuchFieldException, IllegalAccessException {
        for(int i = 0; i < Math.abs(num); i++){
            if(isNoCoordPanel()) return;
            pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, button));
        }
    }

    public static void setToInc(CoordPanelTileEntity tile, Hand hand, Direction direction, int i) throws NoSuchFieldException, IllegalAccessException {
        while (tile.incrementValue != i){
            if(isNoCoordPanel()) return;
            pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.INCREMENT));
        }
    }

    public static void handle(int x, int y, int z) {
        if (!isNoCoordPanel()) {
            Minecraft mc = Minecraft.getInstance();

            BlockPos panelPos = Utils.getBlockPanelAroundPlayer(mc.player, DMBlocks.COORD_PANEL.get());
            CoordPanelTileEntity tile = (CoordPanelTileEntity) mc.player.level.getBlockEntity(panelPos);

            Direction dir = mc.level.getBlockState(panelPos).getValue(RotatableTileEntityBase.FACING);

            BlockPos tPos = ClientTardisFlightCache.getTardisFlightData(panelPos).getPos();

            Map<Integer, Integer> xList = getPowerMap(x);
            Map<Integer, Integer> yList = getPowerMap(y);
            Map<Integer, Integer> zList = getPowerMap(z);

            Map<Integer, Integer> xtList = getPowerMap(tPos.getX());
            Map<Integer, Integer> ytList = getPowerMap(tPos.getY());
            Map<Integer, Integer> ztList = getPowerMap(tPos.getZ());

            Hand hand = Hand.MAIN_HAND;

            Thread t = new Thread(() -> {
                try {
                    if (isNoCoordPanel()) return;

                    handleThread(tile, hand, xList, yList, zList, dir, xtList, ytList, ztList);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        }
    }

    public static BlockRayTraceResult getButtonBlockRayTraceResult(BlockPos pos, Direction side, CoordPanelBlock.CoordPanelButtons button) throws IllegalAccessException, NoSuchFieldException {
        ButtonsAccessor accessor = (ButtonsAccessor)(Object) button;

        Vector2f vec = accessor.getValues().get(side);
        float height = accessor.getHeight();

        float hitX = vec.x;
        float hitY = height / 2.0F;
        float hitZ = vec.y;
        return new BlockRayTraceResult(new Vector3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ), side, pos, false);
    }

    public static Map<Integer, Integer> getPowerMap(int value) {
        boolean isNegative = false;
        if (value < 0) {
            isNegative = true;
            value = -value;
        }

        Map<Integer, Integer> map = new HashMap<>();
        String sValue = Integer.toString(value);

        int[] ints = new int[sValue.length()];
        for (int i = 0; i < sValue.length(); i++) {
            ints[i] = sValue.charAt(i) - '0';
        }
        invertIntArray(ints);

        int[] powers = {1, 10, 100, 1000};
        for (int i = 0; i < Math.min(ints.length, powers.length); i++) {
            map.put(powers[i], ints[i]);
        }

        if (ints.length > powers.length) {
            int multiplier = 1;
            for (int i = powers.length; i < ints.length; i++) {
                map.put(10 * multiplier, ints[i]);
                multiplier *= 10;
            }
        }

        if (isNegative) {
            map.replaceAll((k, v) -> -v);
        }

        return map;
    }


    public static void invertIntArray(int[] arr) {
        int length = arr.length;
        for (int i = 0; i < length / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[length - 1 - i];
            arr[length - 1 - i] = temp;
        }
    }
}
