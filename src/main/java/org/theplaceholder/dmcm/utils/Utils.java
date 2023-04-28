package org.theplaceholder.dmcm.utils;

import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.TardisTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

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

    public static boolean getRenderTardis(String playerName, TardisData data, String exterior){
        return (data.getOwner_name().equals(playerName) && data.getTardisExterior().getRegistryName() == new ResourceLocation("dalekmod", exterior));
    }
}

