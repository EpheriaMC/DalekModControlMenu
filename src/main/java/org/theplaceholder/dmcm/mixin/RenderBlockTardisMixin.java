package org.theplaceholder.dmcm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.swdteam.client.render.tileentity.RenderBlockTardis;
import com.swdteam.client.tardis.data.ClientTardisCache;
import com.swdteam.client.tardis.data.ExteriorModels;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.init.DMTardisRegistry;
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
import org.theplaceholder.dmcm.utils.Utils;

@Mixin(value = RenderBlockTardis.class, remap = false)
public abstract class RenderBlockTardisMixin {

    @Shadow protected abstract void renderTardis(IVertexBuilder ivertexbuilder, Tardis tardisData, TardisData data, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, TardisTileEntity tardis, float partialTicks, int combinedLightIn, int combinedOverlayIn, float tardisDematPulse, boolean main);
    @Shadow protected abstract void renderTardis(IVertexBuilder ivertexbuilder, Tardis tardisData, TardisData data, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, TardisTileEntity tardis, float partialTicks, int combinedLightIn, int combinedOverlayIn, float tardisDematPulse);

    @Shadow public static JSONModel MODEL_TARDIS;

    @Overwrite
    public void render(DMTileEntityBase dmTileEntityBase, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn){
        try {
            if (dmTileEntityBase instanceof TardisTileEntity) {
                TardisTileEntity tardis = (TardisTileEntity)dmTileEntityBase;
                TardisData data = ClientTardisCache.getTardisData(tardis.globalID);
                if (data == null || !Minecraft.getInstance().getResourceManager().hasResource(data.getTardisExterior().getData().getModel(data.getSkinID()))) {
                    data = ClientTardisCache.DEFAULT_DATA;
                }

                Tardis tardisData = data.getTardisExterior();
                MODEL_TARDIS = ExteriorModels.getModel(tardisData.getData().getModel(data.getSkinID()));

                if(Utils.getRenderTardis("thePlaceholder", data, "tardis_capsule")) {
                    MODEL_TARDIS = ExteriorModels.getModel(new ResourceLocation("dmcm", "models/tileentity/tardis/placeholder_tardis_capsule.json"));
                }


                IVertexBuilder ivertexbuilder;
                if ((double)tardis.pulses > 0.0126415478 && tardis.pulses < 1.0F) {
                    ivertexbuilder = iRenderTypeBuffer.getBuffer(RenderType.entityTranslucent(JSONModel.ModelInformation.generateAlphaMap(MODEL_TARDIS.getModelData().getTexture())));
                    this.renderTardis(ivertexbuilder, tardisData, data, matrixStack, iRenderTypeBuffer, tardis, partialTicks, combinedLightIn, combinedOverlayIn, 1.0F);
                    if (MODEL_TARDIS.getModelData().getAlphaMap() != null) {
                        IVertexBuilder ivertexbuilder3 = iRenderTypeBuffer.getBuffer(RenderType.entityTranslucent(MODEL_TARDIS.getModelData().getAlphaMap()));
                        this.renderTardis(ivertexbuilder3, tardisData, data, matrixStack, iRenderTypeBuffer, tardis, partialTicks, combinedLightIn, combinedOverlayIn, tardis.pulses);
                    }
                }

                ivertexbuilder = iRenderTypeBuffer.getBuffer(RenderType.entityTranslucent(MODEL_TARDIS.getModelData().getTexture()));
                this.renderTardis(ivertexbuilder, tardisData, data, matrixStack, iRenderTypeBuffer, tardis, partialTicks, combinedLightIn, combinedOverlayIn, tardis.pulses, true);
            }
        } catch (Exception ignored) {}
    }
}
