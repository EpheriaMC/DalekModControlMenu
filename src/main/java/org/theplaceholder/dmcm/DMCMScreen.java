package org.theplaceholder.dmcm;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Predicate;

public class DMCMScreen extends Screen {
    private TextFieldWidget x;
    private TextFieldWidget y;
    private TextFieldWidget z;
    private Button done;

    protected DMCMScreen(){
        super(new StringTextComponent("DMCM"));
    }


    public void init(){
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        FontRenderer font = Minecraft.getInstance().font;
        Predicate<String> isInteger = s -> ((s.length() > 1 && s.charAt(0) == '-' && s.replace("-", "").matches("\\d+")) || s.matches("\\d+"));
        int j = this.height / 4 + 48;
        x = new TextFieldWidget(font, (this.width / 2) - 49, j, 98, 20, new StringTextComponent("X Coordinate"));
        x.setFilter(isInteger);
        x.setBordered(true);

        y = new TextFieldWidget(font, (this.width / 2 )- 49, j + 24, 98, 20, new StringTextComponent("Y Coordinate"));
        y.setFilter(isInteger);
        y.setBordered(true);

        z = new TextFieldWidget(font, (this.width / 2) - 49, j + 24 * 2, 98, 20, new StringTextComponent("Z Coordinate"));
        z.setFilter(isInteger);
        z.setBordered(true);

        done = new Button((this.width / 2) - 49, j + 72 + 12, 98, 20, new StringTextComponent("Done"), (button) -> {
            if (!(x.getValue().equals("") || y.getValue().equals("") || z.getValue().equals(""))){
                try {
                    DMCM.handle(Integer.parseInt(x.getValue()), Integer.parseInt(y.getValue()), Integer.parseInt(z.getValue()));
                } catch (NoSuchFieldException | IllegalAccessException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                this.onClose();
            }
        });

        this.addWidget(x);
        this.addWidget(y);
        this.addWidget(z);
        this.addButton(done);
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
}
