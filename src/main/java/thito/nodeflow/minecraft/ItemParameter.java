package thito.nodeflow.minecraft;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import thito.nodejfx.*;

public class ItemParameter extends NodeParameter {
    private BorderPane pane = new BorderPane();
    private Pane amountPane = new Pane();
    private Pane durabilityContainer = new Pane();
    private Label amountLabel = new Label();
    private Label shadowAmountLabel = new Label();
    private MinecraftHover.ItemHover itemHover = new MinecraftHover.ItemHover();
    private DoubleProperty durabilityPercentage = new SimpleDoubleProperty(31);
    private IntegerProperty amount = new SimpleIntegerProperty(2);
    private Pane durabilityBar = new Pane();
    private VBox durabilityShadow = new VBox(durabilityBar);
    public ItemParameter() {
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(139, 139, 139), null, null)));
        StackPane stack = new StackPane(pane, durabilityContainer, amountPane);
        HBox box = new HBox(stack);
        box.setAlignment(Pos.CENTER);
        stack.setPrefHeight(48);
        stack.setPrefWidth(48);
        stack.setMaxHeight(48);
        stack.setMaxWidth(48);
        getContainer().getChildren().add(box);

        durabilityContainer.setOpacity(0);
        durabilityShadow.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        durabilityBar.setMinHeight(3);
        durabilityBar.setPrefHeight(3);
        durabilityBar.setMaxHeight(3);
        durabilityShadow.setPrefHeight(6);
        durabilityShadow.setMinHeight(6);
        durabilityShadow.setMinWidth(44);
        durabilityShadow.setAlignment(Pos.TOP_LEFT);
        durabilityShadow.layoutXProperty().bind(durabilityContainer.widthProperty().subtract(durabilityShadow.widthProperty()).divide(2));
        durabilityShadow.layoutYProperty().bind(durabilityContainer.heightProperty().subtract(8));
        durabilityContainer.getChildren().add(durabilityShadow);

        amountPane.getChildren().addAll(shadowAmountLabel, amountLabel);
        amountPane.opacityProperty().bind(Bindings.createDoubleBinding(() -> amount.get() <= 1 ? 0d : 1d, amount));
        amountLabel.layoutXProperty().bind(Bindings.createDoubleBinding(() -> 52 - amountLabel.getWidth(), amountLabel.widthProperty()));
        amountLabel.setLayoutY(30);
        amountLabel.textProperty().bind(Bindings.createStringBinding(() -> amount.get() == 0 ? "" : String.valueOf(amount.get()), amount));
        amountLabel.setTextFill(Color.WHITE);
        amountLabel.setFont(Font.font("Minecraftia", 18));
        shadowAmountLabel.setFont(amountLabel.getFont());
        shadowAmountLabel.setTextFill(Color.BLACK);
        shadowAmountLabel.setOpacity(0.5);
        shadowAmountLabel.textProperty().bind(amountLabel.textProperty());
        shadowAmountLabel.layoutXProperty().bind(amountLabel.layoutXProperty().add(3));
        shadowAmountLabel.layoutYProperty().bind(amountLabel.layoutYProperty().add(3));

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(pane.widthProperty());
        rectangle.heightProperty().bind(pane.heightProperty());
        rectangle.arcHeightProperty().set(7);
        rectangle.arcWidthProperty().set(7);
        pane.setClip(rectangle);

        MinecraftHover hover = new MinecraftHover();
        hover.getPane().setCenter(itemHover);

        box.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            hover.showHover(getScene().getWindow());
        });
        box.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            hover.hideHover();
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
        durabilityPercentage.set(0.5);
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public DoubleProperty durabilityPercentageProperty() {
        return durabilityPercentage;
    }

    public MinecraftHover.ItemHover getItemHover() {
        return itemHover;
    }

    public BorderPane getPane() {
        return pane;
    }
}
