package thito.nodeflow.minecraft;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import net.md_5.bungee.api.chat.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.type.*;

public class ChestNodeParameter extends NodeParameter {
    static {
        FXUtil.poke();
    }
    public static final char COLOR_CHAR = '&';
    private static Font font = Font.font("Minecraft", 18);
    private Pane panel = new Pane();
    private static Image image = new Image(ChestNodeParameter.class.getResource("generic_54.png").toExternalForm());
    private ImageView view = new ImageView();
    private IntegerProperty rows = new SimpleIntegerProperty();
    private int cropTop;
    private int cropBottom = 125;
    private ComplexColorizer title = new ComplexColorizer();
    private Label inventory = new Label("Inventory");
    private ChestSlot[] slots = new ChestSlot[9 * 6];
    private NodeDragListener listener;

    public ChestNodeParameter() {
        getContainer().getChildren().add(panel);
        getAllowInput().set(false);
        getOutputType().set(JavaParameterType.getType(String.class));
        setOutputShape(NodeLinkShape.CIRCLE_SHAPE);
        view.setSmooth(false);
        inventory.setFont(font);
        rows.addListener(obs -> update());
//        dummy.getContainer().setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        panel.getChildren().addAll(view, title, inventory);
        for (int i = 0; i < slots.length; i++) {
            ChestSlot slot = new ChestSlot(this);
            slot.setLayoutX(16 + 36 * (i % 9));
            slot.setLayoutY(36 + 36 * (i / 9));
            slots[i] = slot;
        }
        rows.set(6);
        title.setLayoutX(18);
        title.setLayoutY(11);
        inventory.setLayoutX(18);

    }

    @Override
    public NodeParameter findByPosition(double x, double y) {
        for (ChestSlot slot : slots) {
            if (slot.getLayoutBounds().contains(slot.sceneToLocal(x, y))) {
                return slot.getDummy();
            }
        }
        return super.findByPosition(x, y);
    }

    @Override
    public void updateLinks() {
        for (ChestSlot slot : slots) {
            slot.getDummy().updateLinks();
        }
        super.updateLinks();
    }

    @Override
    public void setCanvas(NodeCanvas canvas) {
        super.setCanvas(canvas);
        for (ChestSlot slot : slots) {
            slot.getDummy().setCanvas(canvas);
        }
    }

    @Override
    public void initialize(Node node) {
        super.initialize(node);
        for (ChestSlot slot : slots) {
            slot.getDummy().initialize(node);
        }
    }

    @Override
    protected void initialize(NodeLink x) {
        super.initialize(x);
        x.getStyle().getComponent().setEffect(null);
    }

    public IntegerProperty rowsProperty() {
        return rows;
    }

    public ObjectProperty<BaseComponent[]> inventoryTitleProperty() {
        return title.componentsProperty();
    }

    public Label getInventoryLabel() {
        return inventory;
    }

    public ChestSlot[] getSlots() {
        return slots;
    }

    public void update() {
        for (int i = 0; i < slots.length; i++) {
            int slotY = i / 9;
            if (slotY < rows.get()) {
                if (!panel.getChildren().contains(slots[i])) {
                    if (getNode() != null) {
                        slots[i].getDummy().initialize(getNode());
                    }
                    panel.getChildren().add(slots[i]);
                }
            } else {
                if (getNode() != null) {
                    slots[i].getDummy().destroy(getNode());
                }
                panel.getChildren().remove(slots[i]);
            }
        }

        PixelReader reader = image.getPixelReader();
        cropTop = 17 + 18 * rows.get();
        WritableImage result = new WritableImage(176 * 2, 222 * 2 - (cropBottom - cropTop + 1) * 2);
        PixelWriter writer = result.getPixelWriter();
        inventory.setLayoutY((cropTop + 1) * 2);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (j >= cropTop && j < cropBottom) continue;
                int argb = reader.getArgb(i, j);
                int offsetY = j;
                if (j > cropBottom) offsetY = offsetY - cropBottom + cropTop - 1;
                int x = i * 2;
                int y = offsetY * 2;
                if (x >= 0 && y >= 0 && x < result.getWidth() && y < result.getHeight()) {
                    writer.setArgb(x, y, argb);
                    if (x + 1 < result.getWidth()) {
                        writer.setArgb(x + 1, y, argb);
                    }
                    if (y + 1 < result.getHeight()) {
                        writer.setArgb(x, y + 1, argb);
                    }
                    if (x + 1 < result.getWidth() && y + 1 < result.getHeight()) {
                        writer.setArgb(x + 1, y + 1, argb);
                    }
                }
            }
        }
        view.imageProperty().set(result);
    }

}
