package org.theplaceholder.dmcm;

import com.swdteam.common.block.tardis.CoordPanelBlock;
import com.swdteam.common.init.DMBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

import java.lang.reflect.Field;
import java.util.*;

public class Utils {
    public static BlockPos getCoordPanelAroundPlayer(PlayerEntity player) {
        BlockPos blockPos = player.blockPosition();

        for (int x = blockPos.getX() - 2; x <= blockPos.getX() + 2; x++) {
            for (int y = blockPos.getY() - 2; y <= blockPos.getY() + 2; y++) {
                for (int z = blockPos.getZ() - 2; z <= blockPos.getZ() + 2; z++) {
                    if (player.level.getBlockState(new BlockPos(x, y, z)).is(DMBlocks.COORD_PANEL.get())) {
                        return new BlockPos(x, y, z);
                    }
                }
            }
        }

        return BlockPos.ZERO;
    }

    public static BlockRayTraceResult getButtonBlockRayTraceResult(BlockPos pos, Direction side, CoordPanelBlock.CoordPanelButtons button) throws IllegalAccessException, NoSuchFieldException {
        Field fValues = CoordPanelBlock.CoordPanelButtons.class.getDeclaredField("values");
        fValues.setAccessible(true);
        Map<Direction, Vector2f> values = (Map<Direction, Vector2f>) fValues.get(button);
        Vector2f vec = values.get(side);

        Field fHeight = CoordPanelBlock.CoordPanelButtons.class.getDeclaredField("height");
        fHeight.setAccessible(true);
        float height = (float) fHeight.get(button);

        float hitX = vec.x;
        float hitY = height / 2.0F;
        float hitZ = vec.y;
        return new BlockRayTraceResult(new Vector3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ), side, pos, false);
    }

    public static Map<Integer, Integer> getPowerMap(int value) {
        boolean isNegative = false;
        Map<Integer, Integer> map = new HashMap<>();

        String sValue = Integer.toString(value);
        if (sValue.contains("-"))
            isNegative = true;

        sValue = sValue.replace("-", "");

        int[] ints = new int[sValue.length()];
        for (int i = 0; i < sValue.length(); i++)
        {
            ints[i] = sValue.charAt(i) - '0';
        }
        invertIntArray(ints);

        map.put(1, ints[0]);
        if (ints.length >= 2)
            map.put(10, ints[1]);
        if (ints.length >= 3)
            map.put(100, ints[2]);
        if (ints.length >= 4)
            map.put(1000, ints[3]);

        if (ints.length >= 5){
            String s = "";
            for (int i = ints.length-1; i >= 4; i--)
                s += ints[i];
            if(!s.equals(""))
                map.put(10000, Integer.parseInt(s));
        }

        if (isNegative)
            map.replaceAll((k, v) -> -v);

        return map;
    }

    public static void invertIntArray(int[] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[arr.length - 1 - i];
            arr[arr.length - 1 - i] = temp;
        }
    }
}

