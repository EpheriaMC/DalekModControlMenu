package org.theplaceholder.dmcm.interfaces;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector2f;
import java.util.Map;

public interface ButtonsAccessor {

    Map<Direction, Vector2f> getValues();

    float getHeight();
}
