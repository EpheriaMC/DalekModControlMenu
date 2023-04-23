package org.theplaceholder.dmcm.handlers;

import com.swdteam.client.tardis.data.ClientTardisFlightCache;
import com.swdteam.common.block.RotatableTileEntityBase;
import com.swdteam.common.block.tardis.CoordPanelBlock;
import com.swdteam.common.block.tardis.DimensionSelectorPanelBlock;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.tardis.TardisFlightData;
import com.swdteam.common.tileentity.tardis.CoordPanelTileEntity;
import com.swdteam.common.tileentity.tardis.DimensionSelectorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import org.theplaceholder.dmcm.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.theplaceholder.dmcm.handlers.ButtonHandler.pressButton;

public class DimHandler {
    public static boolean isNoDimPanel() {
        return (Utils.getBlockPanelAroundPlayer(Minecraft.getInstance().player, DMBlocks.DIMENSION_SELECTOR_PANEL.get()) == BlockPos.ZERO);
    }

    public static void handle(int id){
        if (!isNoDimPanel()){
            Minecraft mc = Minecraft.getInstance();

            BlockPos panelPos = Utils.getBlockPanelAroundPlayer(mc.player, DMBlocks.DIMENSION_SELECTOR_PANEL.get());
            TardisFlightData tData = ClientTardisFlightCache.getTardisFlightData(panelPos);
            Direction dir = mc.level.getBlockState(panelPos).getValue(RotatableTileEntityBase.FACING);

            int orId = dimensionReg.get(tData.dimensionWorldKey());

            Thread t = new Thread(() -> {
                if (isNoDimPanel()) return;
                try {
                    handleThread(id, orId, panelPos, dir);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            t.start();
        }
    }

    public static void handleThread(int id, int orId, BlockPos pos, Direction dir) throws NoSuchFieldException, IllegalAccessException {
        if(id > orId)
            for(int i = 0; i < Math.abs(id - orId); i++){
                pressButton(Hand.MAIN_HAND, getButtonBlockRayTraceResult(pos, dir, DimensionSelectorPanelBlock.DimensionPanelButtons.BTN_RIGHT));
            }
        else
            for (int i = Math.abs(id - orId); i > 0; i--)
                pressButton(Hand.MAIN_HAND, getButtonBlockRayTraceResult(pos, dir, DimensionSelectorPanelBlock.DimensionPanelButtons.BTN_LEFT));
    }

    public static ResourceLocation getKey(Map<ResourceLocation, Integer> map, int value) {
        for (ResourceLocation rl : map.keySet())
            if (value == map.get(rl))
                return rl;
        return null;
    }

    public static BlockRayTraceResult getButtonBlockRayTraceResult(BlockPos pos, Direction side, DimensionSelectorPanelBlock.DimensionPanelButtons button) throws IllegalAccessException, NoSuchFieldException {
        Field fValues =  DimensionSelectorPanelBlock.DimensionPanelButtons.class.getDeclaredField("values");
        fValues.setAccessible(true);
        Map<Direction, Vector2f> values = (Map<Direction, Vector2f>) fValues.get(button);
        Vector2f vec = values.get(side);

        Field fHeight = DimensionSelectorPanelBlock.DimensionPanelButtons.class.getDeclaredField("height");
        fHeight.setAccessible(true);
        float height = (float) fHeight.get(button);

        float hitX = vec.x;
        float hitY = height / 2.0F;
        float hitZ = vec.y;
        return new BlockRayTraceResult(new Vector3d(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ), side, pos, false);
    }

    private static List<DimensionReg> dimensions = new ArrayList<>();
    public static Map<ResourceLocation, Integer> dimensionReg = new HashMap<>();
    public enum DimensionReg {
        OVERWORLD(Dimension.OVERWORLD.location(), 0),
        CAVEGAME(DMDimensions.CAVE_GAME.location(), 1),
        NETHER(Dimension.NETHER.location(), 2),
        CLASSIC(DMDimensions.CLASSIC.location(), 3),
        INFDEV(DMDimensions.INFDEV.location(), 4);

        ResourceLocation location;
        int id;

        DimensionReg(ResourceLocation location, int id){
            this.location = location;
            this.id = id;
            dimensions.add(this);
            dimensionReg.put(location, id);
        }
    }
}
