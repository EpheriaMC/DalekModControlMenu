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
import org.theplaceholder.dmcm.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import static org.theplaceholder.dmcm.utils.Utils.pressButton;

public class CoordHandler {
    public static boolean isRunning = false;
    public static Map<Integer, Integer> xList, yList, zList, txList, tyList, tzList;
    public static CoordPanelTileEntity tile;
    public static Direction direction;
    public static int LOOP = 0;
    public static boolean isIncTick = true;

    public static int x, y, z;

    private static final int MAX_LOOP = 4;

    public static boolean isNoCoordPanel() {
        return Utils.getBlockPanelAroundPlayer(Minecraft.getInstance().player, DMBlocks.COORD_PANEL.get()) == BlockPos.ZERO;
    }

    public static void tick() {
        if (isNoCoordPanel()) {
            isRunning = false;
            return;
        }

        if (LOOP <= MAX_LOOP) {
            int i = (int) Math.pow(10, LOOP);

            if (x == 0 && y == 0 && z == 0){
                x = xList.getOrDefault(i, 0);
                y = yList.getOrDefault(i, 0);
                z = zList.getOrDefault(i, 0);
            }

            int tx = txList.getOrDefault(i, 0);
            int ty = tyList.getOrDefault(i, 0);
            int tz = tzList.getOrDefault(i, 0);

            Hand hand = Hand.MAIN_HAND;

            if (tile.incrementValue != i) {
                if (isIncTick) {
                    pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.INCREMENT));
                    isIncTick = false;
                } else {
                    isIncTick = true;
                }
            } else if (x != tx) {
                if (x < tx) {
                    pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.SUB_X));
                    x++;
                } else {
                    pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.ADD_X));
                    x--;
                }
            } else if (y != ty) {
                if (y < ty) {
                    pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.SUB_Y));
                    y++;
                } else {
                    pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.ADD_Y));
                    y--;
                }
            } else if (z != tz) {
                if (z < tz) {
                    pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.SUB_Z));
                    z++;
                } else {
                    pressButton(hand, getButtonBlockRayTraceResult(tile.getBlockPos(), direction, CoordPanelBlock.CoordPanelButtons.ADD_Z));
                    z--;
                }
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

    public static BlockRayTraceResult getButtonBlockRayTraceResult(BlockPos pos, Direction side, CoordPanelBlock.CoordPanelButtons button) {
        ButtonsAccessor accessor = (ButtonsAccessor) (Object) button;

        Vector2f vec = accessor.getValues().get(side);
        float height = accessor.getHeight();

        float hitX = vec.x;
        float hitY = height / 2.0F;
        float hitZ = vec.y;
        return new BlockRayTraceResult(new Vector3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ), side, pos, false);
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

    private static void invertIntArray(int[] arr) {
        int length = arr.length;
        for (int i = 0; i < length / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[length - 1 - i];
            arr[length - 1 - i] = temp;
        }
    }
}
