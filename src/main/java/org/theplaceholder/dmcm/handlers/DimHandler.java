package org.theplaceholder.dmcm.handlers;

import com.swdteam.common.block.RotatableTileEntityBase;
import com.swdteam.common.block.tardis.DimensionSelectorPanelBlock;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.tileentity.tardis.DimensionSelectorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.theplaceholder.dmcm.utils.Utils;

import java.lang.reflect.Field;
import java.util.Map;

import static org.theplaceholder.dmcm.utils.ButtonUtils.pressButton;

public class DimHandler {
    public static boolean isNoDimPanel() {
        return (Utils.getBlockPanelAroundPlayer(Minecraft.getInstance().player, DMBlocks.DIMENSION_SELECTOR_PANEL.get()) == BlockPos.ZERO);
    }

    public static void handle(int id){
        if (!isNoDimPanel()){
            Minecraft mc = Minecraft.getInstance();

            BlockPos panelPos = Utils.getBlockPanelAroundPlayer(mc.player, DMBlocks.DIMENSION_SELECTOR_PANEL.get());
            Direction dir = mc.level.getBlockState(panelPos).getValue(RotatableTileEntityBase.FACING);
            DimensionSelectorTileEntity tile = (DimensionSelectorTileEntity) mc.level.getBlockEntity(panelPos);

            Thread t = new Thread(() -> {
                if (isNoDimPanel()) return;
                try {
                    handleThread(id, tile.getIndex(), panelPos, dir);
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
            for (int i = Math.abs(id - orId); i > 0; i--) {
                pressButton(Hand.MAIN_HAND, getButtonBlockRayTraceResult(pos, dir, DimensionSelectorPanelBlock.DimensionPanelButtons.BTN_LEFT));
            }
        pressButton(Hand.MAIN_HAND, getButtonBlockRayTraceResult(pos, dir, DimensionSelectorPanelBlock.DimensionPanelButtons.BTN_SELECT));
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
}
