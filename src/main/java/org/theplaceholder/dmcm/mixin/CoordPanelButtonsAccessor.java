package org.theplaceholder.dmcm.mixin;

import com.swdteam.common.block.tardis.CoordPanelBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = CoordPanelBlock.CoordPanelButtons.class, remap = false)
public interface CoordPanelButtonsAccessor {
    @Accessor
    Map<Direction, Vector2f> getValues();

    @Accessor
    float getHeight();
}
