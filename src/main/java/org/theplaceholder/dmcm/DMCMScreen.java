package org.theplaceholder.dmcm;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.theplaceholder.dmcm.handlers.CoordHandler;
import org.theplaceholder.dmcm.handlers.DimHandler;

import java.util.function.Predicate;

public class DMCMScreen extends Screen {
    private TextFieldWidget x;
    private TextFieldWidget y;
    private TextFieldWidget z;
    private Button done;

    private Slider dimList;

    protected DMCMScreen(){
        super(new StringTextComponent("DMCM"));
    }


    public void init(){
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        FontRenderer font = Minecraft.getInstance().font;
        Predicate<String> isInteger = s -> (s != null && (s.isEmpty() || s.matches("-?[0-9]+") || s.equals("-")));
        int j = this.height / 4 + 48;
        x = new TextFieldWidget(font, (this.width / 2) - 49, j, 98, 20, new StringTextComponent("X Coordinate"));
        x.setFilter(isInteger);
        x.setSuggestion("X Coord");
        x.setBordered(true);

        y = new TextFieldWidget(font, (this.width / 2 )- 49, j + 24, 98, 20, new StringTextComponent("Y Coordinate"));
        y.setFilter(isInteger);
        x.setSuggestion("Y Coord");
        y.setBordered(true);

        z = new TextFieldWidget(font, (this.width / 2) - 49, j + 24 * 2, 98, 20, new StringTextComponent("Z Coordinate"));
        z.setFilter(isInteger);
        x.setSuggestion("Z Coord");
        z.setBordered(true);

        done = new Button((this.width / 2) - 49, j + 72 + 12 + 24, 98, 20, new StringTextComponent("Done"), (button) -> {
            if (!(x.getValue().isEmpty() || y.getValue().isEmpty() || z.getValue().isEmpty() || x.getValue().isEmpty() || y.getValue().isEmpty() || z.getValue().equals("-"))){
                if (!CoordHandler.isNoCoordPanel())
                    CoordHandler.handle(Integer.parseInt(x.getValue()), Integer.parseInt(y.getValue()), Integer.parseInt(z.getValue()));
                if (!DimHandler.isNoDimPanel())
                    DimHandler.handle((int)dimList.sliderValue);
                this.onClose();
            }
        });

        this.addWidget(x);
        this.addWidget(y);
        this.addWidget(z);
        this.addButton(done);

        dimList = new Slider((this.width / 2) - 49, j + 72 + 12, 98, 20, new StringTextComponent("Dimension:"), new StringTextComponent(""), 0, 4, 0, true, true,(s) -> {});
        this.addWidget(dimList);
    }

    public void render(MatrixStack matrixStack, int xi, int yi, float tick){
        x.render(matrixStack, xi, yi, tick);
        y.render(matrixStack, xi, yi, tick);
        z.render(matrixStack, xi, yi, tick);
        done.render(matrixStack, xi, yi, tick);
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void tick(){
        boolean coordPanel = !CoordHandler.isNoCoordPanel();
        boolean dimPanel = !DimHandler.isNoDimPanel();

        done.active = coordPanel && dimPanel;
        x.active = coordPanel;
        y.active = coordPanel;
        z.active = coordPanel;

        dimList.active = dimPanel;
        ResourceLocation rl = DimHandler.getKey(DimHandler.dimensionReg, (int)dimList.sliderValue);
        dimList.setMessage(new StringTextComponent("Dimension:" + rl.getPath().replace("_", " ").toUpperCase()));
    }
}
