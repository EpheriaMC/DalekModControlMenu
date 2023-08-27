package org.theplaceholder.dmcm.utils;

import com.swdteam.common.block.tardis.CoordPanelBlock;
import com.swdteam.common.tardis.TardisData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.theplaceholder.dmcm.interfaces.ButtonsAccessor;

import java.util.Objects;

public class Utils {
    public static BlockPos getBlockPanelAroundPlayer(PlayerEntity player, Block block) {
        BlockPos blockPos = player.blockPosition();

        for (int x = blockPos.getX() - 2; x <= blockPos.getX() + 2; x++) {
            for (int y = blockPos.getY() - 2; y <= blockPos.getY() + 2; y++) {
                for (int z = blockPos.getZ() - 2; z <= blockPos.getZ() + 2; z++) {
                    if (player.level.getBlockState(new BlockPos(x, y, z)).is(block)) {
                        return new BlockPos(x, y, z);
                    }
                }
            }
        }
        return BlockPos.ZERO;
    }

    public static void pressButton(Hand hand, BlockRayTraceResult rayTraceResult){
        Minecraft.getInstance().getConnection().send(new CPlayerTryUseItemOnBlockPacket(hand, rayTraceResult));
    }

    public static BlockRayTraceResult getButtonBlockRayTraceResult(BlockPos pos, Direction side, ButtonsAccessor button) {

        Vector2f vec = button.getValues().get(side);
        float height = button.getHeight();

        float hitX = vec.x;
        float hitY = height / 2.0F;
        float hitZ = vec.y;
        return new BlockRayTraceResult(new Vector3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ), side, pos, false);
    }
}

