package org.theplaceholder.dmcm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.swdteam.client.render.tileentity.RenderBlockTardis;
import com.swdteam.client.tardis.data.ClientTardisCache;
import com.swdteam.client.tardis.data.ExteriorModels;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.DMTileEntityBase;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.model.javajson.JSONModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.theplaceholder.dmcm.utils.Utils;

@Mixin(value = RenderBlockTardis.class, remap = false)
public abstract class RenderBlockTardisMixin {

    @Inject(
            method = "renderTardisModel",
            at = @At(value = "HEAD")
    )
    private static void renderTardisModel(JSONModel model, float rotation, IVertexBuilder ivertexbuilder, TardisData data, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, CallbackInfo ci){
        if (Utils.getRenderTardis("thePlaceholder", data, "tardis_capsule", 0)) {
            model = ExteriorModels.getModel(new ResourceLocation("dmcm", "models/tileentity/tardis/placeholder_tardis_capsule.json"));
        }
    }
}
