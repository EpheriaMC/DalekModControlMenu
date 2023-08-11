package org.theplaceholder.dmcm.mixin;

import com.swdteam.common.block.tardis.DimensionSelectorPanelBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = DimensionSelectorPanelBlock.DimensionPanelButtons.class, remap = false)
public interface DimensionPanelButtonsAccessor {
    @Accessor
    Map<Direction, Vector2f> getValues();

    @Accessor
    float getHeight();
}
