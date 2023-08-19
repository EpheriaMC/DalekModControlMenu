package org.theplaceholder.dmcm.mixin;

import com.swdteam.common.block.tardis.DimensionSelectorPanelBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.theplaceholder.dmcm.interfaces.ButtonsAccessor;

import java.util.Map;

@Mixin(value = DimensionSelectorPanelBlock.DimensionPanelButtons.class, remap = false)
public abstract class DimensionPanelButtonsAccessor implements ButtonsAccessor {

    @Shadow private Map<Direction, Vector2f> values;

    @Shadow private float height;

    public Map<Direction, Vector2f> getValues(){
        return values;
    };

    public float getHeight(){
        return height;
    };
}
