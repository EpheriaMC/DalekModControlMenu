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
import org.theplaceholder.dmcm.interfaces.ButtonsAccessor;
import org.theplaceholder.dmcm.utils.Utils;

import static org.theplaceholder.dmcm.utils.Utils.getButtonBlockRayTraceResult;
import static org.theplaceholder.dmcm.utils.Utils.pressButton;

public class DimHandler {
  public static boolean isRunning = false;
  public static int id;
  public static int orId;
  public static BlockPos pos;
  public static Direction dir;

  public static boolean isNoDimPanel() {
    return Utils.getBlockPanelAroundPlayer(Minecraft.getInstance().player, DMBlocks.DIMENSION_SELECTOR_PANEL.get()) == BlockPos.ZERO;
  }

  public static void handle(int id) {
    if (!isNoDimPanel()) {
      Minecraft mc = Minecraft.getInstance();
      BlockPos panelPos = Utils.getBlockPanelAroundPlayer(mc.player, DMBlocks.DIMENSION_SELECTOR_PANEL.get());
      Direction dir = mc.level.getBlockState(panelPos).getValue(RotatableTileEntityBase.FACING);
      DimensionSelectorTileEntity tile = (DimensionSelectorTileEntity) mc.level.getBlockEntity(panelPos);

      isRunning = true;
      DimHandler.id = id;
      orId = tile.getIndex();
      pos = panelPos;
      DimHandler.dir = dir;
    }
  }

  public static void tick() {
    if (isNoDimPanel()) {
      isRunning = false;
      return;
    }

    if (id == orId) {
      isRunning = false;
      pressButton(Hand.MAIN_HAND, getButtonBlockRayTraceResult(pos, dir, (ButtonsAccessor)(Object) DimensionSelectorPanelBlock.DimensionPanelButtons.BTN_SELECT));
    } else if (id > orId) {
      pressButton(Hand.MAIN_HAND, getButtonBlockRayTraceResult(pos, dir, (ButtonsAccessor)(Object) DimensionSelectorPanelBlock.DimensionPanelButtons.BTN_RIGHT));
    } else {
      pressButton(Hand.MAIN_HAND, getButtonBlockRayTraceResult(pos, dir, (ButtonsAccessor)(Object) DimensionSelectorPanelBlock.DimensionPanelButtons.BTN_LEFT));
    }
  }
}