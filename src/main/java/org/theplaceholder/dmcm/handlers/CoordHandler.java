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
import org.theplaceholder.dmcm.interfaces.ButtonsAccessor;
import org.theplaceholder.dmcm.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import static org.theplaceholder.dmcm.utils.Utils.getButtonBlockRayTraceResult;
import static org.theplaceholder.dmcm.utils.Utils.pressButton;

public class CoordHandler {
    private static final int MAX_LOOP = 4;
    private static final int[] POWERS_OF_TEN = {1, 10, 100, 1000};

    public static boolean isRunning = false;
    private static Map<Integer, Integer> xList, yList, zList, txList, tyList, tzList;
    private static CoordPanelTileEntity tile;
    private static Direction direction;
    private static int LOOP = 0;
    private static boolean isIncTick = true;
    private static int x, y, z;

    public static boolean isNoCoordPanel() {
        return Utils.getBlockPanelAroundPlayer(Minecraft.getInstance().player, DMBlocks.COORD_PANEL.get()) == BlockPos.ZERO;
    }

    public static void tick() {
        if (isNoCoordPanel()) {
            isRunning = false;
            return;
        }

        if (LOOP <= MAX_LOOP) {
            int powerOfTen = (int) Math.pow(10, LOOP);

            if (x == 0 && y == 0 && z == 0) {
                x = xList.getOrDefault(powerOfTen, 0);
                y = yList.getOrDefault(powerOfTen, 0);
                z = zList.getOrDefault(powerOfTen, 0);
            }

            int tx = txList.getOrDefault(powerOfTen, 0);
            int ty = tyList.getOrDefault(powerOfTen, 0);
            int tz = tzList.getOrDefault(powerOfTen, 0);

            Hand hand = Hand.MAIN_HAND;

            if (tile.incrementValue != powerOfTen) {
                if (isIncTick) {
                    pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, (ButtonsAccessor)(Object) CoordPanelBlock.CoordPanelButtons.INCREMENT));
                    isIncTick = false;
                } else {
                    isIncTick = true;
                }
            } else if (x != tx) {
                CoordPanelBlock.CoordPanelButtons buttonType = (x < tx) ? CoordPanelBlock.CoordPanelButtons.SUB_X : CoordPanelBlock.CoordPanelButtons.ADD_X;
                pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, (ButtonsAccessor)(Object)  buttonType));
                x += (x < tx) ? 1 : -1;
            } else if (y != ty) {
                CoordPanelBlock.CoordPanelButtons buttonType = (y < ty) ? CoordPanelBlock.CoordPanelButtons.SUB_Y : CoordPanelBlock.CoordPanelButtons.ADD_Y;
                pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, (ButtonsAccessor)(Object)  buttonType));
                y += (y < ty) ? 1 : -1;
            } else if (z != tz) {
                CoordPanelBlock.CoordPanelButtons buttonType = (z < tz) ? CoordPanelBlock.CoordPanelButtons.SUB_Z : CoordPanelBlock.CoordPanelButtons.ADD_Z;
                pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, (ButtonsAccessor)(Object) buttonType));
                z += (z < tz) ? 1 : -1;
            } else {
                LOOP++;
                x = 0;
                y = 0;
                z = 0;
            }
        } else {
            LOOP = 0;
            isRunning = false;
        }
    }

    public static void handle(int x, int y, int z) {
        if (!isNoCoordPanel()) {
            Minecraft mc = Minecraft.getInstance();

            BlockPos panelPos = Utils.getBlockPanelAroundPlayer(mc.player, DMBlocks.COORD_PANEL.get());
            tile = (CoordPanelTileEntity) mc.player.level.getBlockEntity(panelPos);

            direction = mc.level.getBlockState(panelPos).getValue(RotatableTileEntityBase.FACING);

            BlockPos tPos = ClientTardisFlightCache.getTardisFlightData(panelPos).getPos();

            xList = getPowerMap(x);
            yList = getPowerMap(y);
            zList = getPowerMap(z);

            txList = getPowerMap(tPos.getX());
            tyList = getPowerMap(tPos.getY());
            tzList = getPowerMap(tPos.getZ());

            isRunning = true;
        }
    }

    private static Map<Integer, Integer> getPowerMap(int value) {
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

        for (int i = 0; i < Math.min(ints.length, POWERS_OF_TEN.length); i++) {
            map.put(POWERS_OF_TEN[i], ints[i]);
        }

        if (ints.length > POWERS_OF_TEN.length) {
            int multiplier = 1;
            for (int i = POWERS_OF_TEN.length; i < ints.length; i++) {
                map.put(10 * multiplier, ints[i]);
                multiplier *= 10;
            }
        }

        if (isNegative) {
            map.replaceAll((k, v) -> -v);
        }

        return map;
    }

    private static void invertIntArray(int[] arr) {
        int length = arr.length;
        for (int i = 0; i < length / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[length - 1 - i];
            arr[length - 1 - i] = temp;
        }
    }
}