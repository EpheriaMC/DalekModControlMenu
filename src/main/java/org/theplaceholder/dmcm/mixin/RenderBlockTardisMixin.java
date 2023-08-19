package org.theplaceholder.dmcm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.swdteam.client.render.tileentity.RenderBlockTardis;
import com.swdteam.client.tardis.data.ClientTardisCache;
import com.swdteam.client.tardis.data.ExteriorModels;
import com.swdteam.common.tardis.Tardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.DMTileEntityBase;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.model.javajson.JSONModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.theplaceholder.dmcm.utils.Utils;

@Mixin(value = RenderBlockTardis.class, remap = false)
public abstract class RenderBlockTardisMixin {

    @Shadow protected abstract void renderTardis(IVertexBuilder ivertexbuilder, Tardis tardisData, TardisData data, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, TardisTileEntity tardis, float partialTicks, int combinedLightIn, int combinedOverlayIn, float tardisDematPulse);

    @Shadow public static JSONModel MODEL_TARDIS;

    @Inject(method = "render(Lcom/swdteam/common/tileentity/DMTileEntityBase;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;II)V", at = @At("HEAD"), cancellable = true)
    private static void render(DMTileEntityBase dmTileEntityBase, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn, CallbackInfo ci){
        if (dmTileEntityBase instanceof TardisTileEntity){
            TardisTileEntity tardisTileEntity = (TardisTileEntity) dmTileEntityBase;
            TardisData data = ClientTardisCache.getTardisData(tardisTileEntity.globalID);
            if(Utils.getRenderTardis("thePlaceholder", data, "tardis_capsule", 0)) {
                MODEL_TARDIS = ExteriorModels.getModel(new ResourceLocation("dmcm", "models/tileentity/tardis/placeholder_tardis_capsule.json"));
            }
        }
    }


}
