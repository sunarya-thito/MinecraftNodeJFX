package thito.nodeflow.minecraft;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.type.*;

public class ChestSlot extends StackPane {

    public static final int size = 32;
    private Pane dummyContainer = new Pane();
    private NodeParameter dummy = new NodeParameter() {
        @Override
        protected void initialize(NodeLink x) {
            super.initialize(x);
            x.getStyle().getComponent().setEffect(null);
        }
    };
    private BorderPane itemContainer = new BorderPane();
    private Pane pane = new Pane();
    private Pane durabilityContainer = new Pane();
    private Label amountLabel = new Label();
    private Label shadowAmountLabel = new Label();
    private IntegerProperty amount = new SimpleIntegerProperty(0);
    private DoubleProperty durabilityPercentage = new SimpleDoubleProperty(1);
    private ChestNodeParameter chest;
    private NodeDragListener listener;
    private MinecraftHover hover;
    private Pane durabilityBar = new Pane();
    private VBox durabilityShadow = new VBox(durabilityBar);
    private boolean hovering = false;
    public ChestSlot(ChestNodeParameter chest) {
        this.chest = chest;
        durabilityContainer.setOpacity(0);
        durabilityShadow.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        durabilityBar.setMinHeight(2);
        durabilityBar.setPrefHeight(2);
        durabilityBar.setMaxHeight(2);
        durabilityShadow.setPrefHeight(4);
        durabilityShadow.setMinHeight(4);
        durabilityShadow.setMinWidth(28);
        durabilityShadow.setAlignment(Pos.TOP_LEFT);
        durabilityShadow.layoutXProperty().bind(durabilityContainer.widthProperty().subtract(durabilityShadow.widthProperty()).divide(2));
        durabilityShadow.layoutYProperty().bind(durabilityContainer.heightProperty().subtract(6));
        durabilityContainer.getChildren().add(durabilityShadow);
        pane.getChildren().addAll(shadowAmountLabel, amountLabel);
        amountLabel.layoutXProperty().bind(Bindings.createDoubleBinding(() -> 34 - amountLabel.getWidth(), amountLabel.widthProperty()));
        amountLabel.setLayoutY(14);
        amountLabel.textProperty().bind(Bindings.createStringBinding(() -> amount.get() == 0 ? "" : String.valueOf(amount.get()), amount));
        amountLabel.setTextFill(Color.WHITE);
        amountLabel.setFont(Font.font("Minecraftia", 15));
        shadowAmountLabel.setFont(amountLabel.getFont());
        shadowAmountLabel.setTextFill(Color.BLACK);
        shadowAmountLabel.setOpacity(0.5);
        shadowAmountLabel.textProperty().bind(amountLabel.textProperty());
        shadowAmountLabel.layoutXProperty().bind(amountLabel.layoutXProperty().add(2));
        shadowAmountLabel.layoutYProperty().bind(amountLabel.layoutYProperty().add(2));
        dummy.setOutputShape(NodeLinkShape.CIRCLE_SHAPE);
        dummy.getAllowInput().set(false);
        dummy.layoutXProperty().bind(dummyContainer.widthProperty().divide(2));
        dummy.layoutYProperty().bind(dummyContainer.heightProperty().divide(2));
        dummy.maxHeightProperty().set(0);
        dummy.maxWidthProperty().set(0);
        dummy.minWidthProperty().set(0);
        dummy.minHeightProperty().set(0);
        dummyContainer.getChildren().add(dummy);
        dummy.setOpacity(0);
        dummy.getUnmodifiableOutputLinks().addListener((SetChangeListener<NodeParameter>) change -> {
            updateOpacity();
        });
        dummy.setMouseTransparent(true);
        dummy.getOutputType().set(JavaParameterType.getType(Integer.class));
        pane.opacityProperty().bind(Bindings.createDoubleBinding(() -> amount.get() <= 1 ? 0d : 1d, amount));
        getChildren().addAll(itemContainer, durabilityContainer, pane, dummyContainer);

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(pane.widthProperty().add(2));
        rectangle.heightProperty().bind(pane.heightProperty().add(2));
        pane.setClip(rectangle);

        setMinHeight(32);
        setMinWidth(32);
        setMaxWidth(32);
        setMaxHeight(32);
        setWidth(32);
        setHeight(32);

        ColorAdjust adjust = new ColorAdjust(0, 0, 0.5, 0);
        hover = new MinecraftHover();
        MinecraftHover.ItemHover item = new MinecraftHover.ItemHover();
        hover.getPane().setCenter(item);
        addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            dummyVisible = true;
            updateOpacity();
        });
        addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            dummyVisible = false;
            updateOpacity();
        });
        setOnMouseEntered(event -> {
            setBackground(new Background(new BackgroundFill(Color.color(1, 1, 1, 0.5), null, null)));
            getItemContainer().setEffect(adjust);
            event.consume();
            hovering = true;
            if (amount.get() > 0) {
                hover.showHover(getScene().getWindow());
            }
        });
        setOnMouseExited(event -> {
            hovering = false;
            setBackground(Background.EMPTY);
            getItemContainer().setEffect(null);
            event.consume();
            hover.hideHover();
        });
        amount.addListener((obs, old, val) -> {
            if (val.intValue() > 0) {
                if (hovering) {
                    hover.showHover(getScene().getWindow());
                }
            } else {
                hover.hideHover();
            }
        });
        durabilityPercentage.addListener((obs, old, val) -> {
            if (val.doubleValue() < 1) {
                double p = val.doubleValue();
                durabilityBar.maxWidthProperty().bind(durabilityShadow.widthProperty().multiply(p));
                durabilityContainer.setOpacity(1);
                Color color = Color.hsb(p * 108, 1, 1);
                durabilityBar.setBackground(new Background(new BackgroundFill(color, null, null)));
            } else {
                durabilityContainer.setOpacity(0);
            }
        });
        listener = new NodeDragListener(dummy, false, this);
    }

    private boolean dummyVisible;
    private void updateOpacity() {
        if (dummyVisible || dummy.getUnmodifiableOutputLinks().size() > 0) {
            dummy.setOpacity(1);
        } else {
            dummy.setOpacity(0);
        }
    }

    public DoubleProperty durabilityPercentageProperty() {
        return durabilityPercentage;
    }

    public MinecraftHover getHover() {
        return hover;
    }

    public ChestNodeParameter getChest() {
        return chest;
    }

    public BorderPane getItemContainer() {
        return itemContainer;
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public NodeParameter getDummy() {
        return dummy;
    }
}
