package org.theplaceholder.dmcm.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.swdteam.client.tardis.data.ClientTardisFlightCache;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.tardis.data.TardisLocationRegistry;
import com.swdteam.common.tileentity.tardis.DimensionSelectorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.theplaceholder.dmcm.DMCM;
import org.theplaceholder.dmcm.handlers.CoordHandler;
import org.theplaceholder.dmcm.handlers.DimHandler;
import org.theplaceholder.dmcm.utils.Utils;
import org.theplaceholder.dmcm.utils.Waypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DMCMScreen extends Screen {
    public TextFieldWidget x;
    public TextFieldWidget y;
    public TextFieldWidget z;
    private Button done;
    private Button save;

    public boolean deleteMode = false;

    private TextFieldWidget name;

    public Slider dimList;

    public Waypoint selected;

    public List<Waypoint.WaypointButton> waypoints = new ArrayList<>();

    public DMCMScreen(){
        super(new StringTextComponent("DMCM"));
    }


    public void init(){
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        FontRenderer font = Minecraft.getInstance().font;
        int j = this.height / 4 + 48;

        x = new TextFieldWidget(font, (this.width / 2) - 49, j, 98, 20, new StringTextComponent("X Coordinate"));
        x.setFilter(getValidCoord());
        x.setBordered(true);

        y = new TextFieldWidget(font, (this.width / 2 ) - 49, j + 24, 98, 20, new StringTextComponent("Y Coordinate"));
        y.setFilter(getValidCoord());
        y.setBordered(true);

        z = new TextFieldWidget(font, (this.width / 2) - 49, j + 24 * 2, 98, 20, new StringTextComponent("Z Coordinate"));
        z.setFilter(getValidCoord());
        z.setBordered(true);

        done = new Button((this.width / 2) - 49, j + 72 + 12 + 24, 98 / 2, 20, new StringTextComponent("Done"), (button) -> {
            if (!(x.getValue().isEmpty() || y.getValue().isEmpty() || z.getValue().isEmpty() || x.getValue().isEmpty() || y.getValue().isEmpty() || x.getValue().equals("-") || y.getValue().equals("-") || z.getValue().equals("-") || CoordHandler.isNoCoordPanel())){
                if (!CoordHandler.isNoCoordPanel())
                    CoordHandler.handle(Integer.parseInt(x.getValue()), Integer.parseInt(y.getValue()), Integer.parseInt(z.getValue()));
                this.onClose();
            }
            if (!DimHandler.isNoDimPanel()) {
                DimHandler.handle(dimList.getValueInt());
                this.onClose();
            }
        });

        name = new TextFieldWidget(font, (this.width / 2 ) - 49, j - 24, 98, 20, new StringTextComponent("Waypoint Name"));
        name.setBordered(true);
        this.addWidget(name);

        save = new Button((this.width / 2), j + 72 + 12 + 24, 98 / 2, 20, new StringTextComponent("Save"), (button) -> {
            if (!deleteMode){
                if (!(x.getValue().isEmpty() || y.getValue().isEmpty() || z.getValue().isEmpty() || x.getValue().isEmpty() || y.getValue().isEmpty() || x.getValue().equals("-") || y.getValue().equals("-") || z.getValue().equals("-") || CoordHandler.isNoCoordPanel() || name.getValue().isEmpty())) {
                    DMCM.waypointsList.waypoints.add(new Waypoint(name.getValue(), Integer.parseInt(x.getValue()), Integer.parseInt(y.getValue()), Integer.parseInt(z.getValue()), dimList.getValueInt()));
                    try {
                        DMCM.waypointsList.save();
                        refreshList();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }else {
                DMCM.waypointsList.waypoints.remove(selected);
                try {
                    DMCM.waypointsList.save();
                    refreshList();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                deleteMode = false;
            }
            Waypoint.WaypointButton.index = 0;
        });

        this.addButton(save);
        this.addWidget(x);
        this.addWidget(y);
        this.addWidget(z);
        this.addButton(done);

        dimList = new Slider((this.width / 2) - 49, j + 72 + 12, 98, 20, new StringTextComponent(""), new StringTextComponent(""), 0, 4, 0, true, true, (s) -> {}, (s) -> {
            updateDim();
        });
        dimList.showDecimal = false;
        dimList.minValue = 0;
        dimList.maxValue = 4;

        if (!DimHandler.isNoDimPanel()) {
            dimList.setValue(((DimensionSelectorTileEntity) minecraft.level.getBlockEntity(Utils.getBlockPanelAroundPlayer(minecraft.player, DMBlocks.DIMENSION_SELECTOR_PANEL.get()))).getIndex());
        }

        if (!CoordHandler.isNoCoordPanel()){
            BlockPos pos = ClientTardisFlightCache.getTardisFlightData(minecraft.player.blockPosition()).getPos();
            x.setValue(String.valueOf(pos.getX()));
            y.setValue(String.valueOf(pos.getY()));
            z.setValue(String.valueOf(pos.getZ()));
        }
        updateDim();
        this.addWidget(dimList);


        try {
            Waypoint.WaypointButton.index = 0;
            DMCM.waypointsList.load();
            for (Waypoint waypoint : DMCM.waypointsList.waypoints){
                Waypoint.WaypointButton button = new Waypoint.WaypointButton(waypoint, this, j);
                waypoints.add(button);
                this.addButton(button);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(MatrixStack matrixStack, int xi, int yi, float tick){
        x.render(matrixStack, xi, yi, tick);
        y.render(matrixStack, xi, yi, tick);
        z.render(matrixStack, xi, yi, tick);
        done.render(matrixStack, xi, yi, tick);
        dimList.render(matrixStack, xi, yi, tick);
        for (Waypoint.WaypointButton waypoint : waypoints){
            waypoint.render(matrixStack, xi, yi, tick);
        }
        name.render(matrixStack, xi, yi, tick);
        save.render(matrixStack, xi, yi, tick);
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void tick(){
        boolean coordPanel = !CoordHandler.isNoCoordPanel();
        boolean dimPanel = !DimHandler.isNoDimPanel();

        done.active = coordPanel || dimPanel;
        x.active = coordPanel;
        y.active = coordPanel;
        z.active = coordPanel;

        dimList.active = dimPanel;

        x.setFilter(getValidCoord());
        y.setFilter(getValidCoord());
        z.setFilter(getValidCoord());

        if (x.getValue().isEmpty())
            x.setSuggestion("X Coord");
        else
            x.setSuggestion("");

        if (y.getValue().isEmpty())
            y.setSuggestion("Y Coord");
        else
            y.setSuggestion("");

        if (z.getValue().isEmpty())
            z.setSuggestion("Z Coord");
        else
            z.setSuggestion("");

        if (deleteMode)
            save.setMessage(new StringTextComponent("Delete"));
        else
            save.setMessage(new StringTextComponent("Save"));
    }

    public void updateDim(){
        if (dimList.getValueInt() < TardisLocationRegistry.getLocationRegistryAsList().size() && dimList.getValueInt() >= 0)
            dimList.setMessage(new StringTextComponent("Dimension: " + TardisLocationRegistry.getLocationRegistryAsList().get(dimList.getValueInt()).getDimension().location().getPath().replace("_", " ").toUpperCase().replace("MINECRAFT ", "")));
    }

    public Predicate<String> getValidCoord(){
        return DMCMScreen::isStringNumber;
    }

    public static boolean isStringNumber(String str) {
        if (str.isEmpty() || str.equals("-"))
            return true;

        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void refreshList(){
        waypoints.clear();
        children.removeIf((p) -> p instanceof Waypoint.WaypointButton);

        Waypoint.WaypointButton.index = 0;
        try {
            DMCM.waypointsList.load();
            for (Waypoint waypoint : DMCM.waypointsList.waypoints){
                Waypoint.WaypointButton button = new Waypoint.WaypointButton(waypoint, this, this.height / 4 + 48);
                waypoints.add(button);
                this.addButton(button);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
