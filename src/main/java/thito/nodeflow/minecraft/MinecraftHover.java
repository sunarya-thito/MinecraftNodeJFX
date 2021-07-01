package thito.nodeflow.minecraft;

import javafx.animation.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;

public class MinecraftHover {

    private BorderPane pane = new BorderPane();
    private Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> update()));
    private ObjectProperty<Color> borderColor = new SimpleObjectProperty<>(Color.rgb(37, 2, 92, 0.9));
    private ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>(Color.rgb(19, 2, 19, 0.999));
    private Scene scene = new Scene(pane, Color.TRANSPARENT);
    public MinecraftHover() {
        timeline.setCycleCount(-1);
        pane.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        pane.setMinWidth(5);
        pane.setMinHeight(5);
        pane.setPadding(new Insets(1, 5, 3, 5));
        pane.setMouseTransparent(true);
        borderColor.addListener((obs) -> updateBackground());
        backgroundColor.addListener((obs) -> updateBackground());
        updateBackground();
    }

    private void updateBackground() {
        pane.setBackground(new Background(new BackgroundFill(backgroundColor.get(), new CornerRadii(3), null)));
        pane.setBorder(new Border(new BorderStroke(borderColor.get(), BorderStrokeStyle.SOLID, null, new BorderWidths(2), new Insets(1))));
    }

    private Stage stage;
    private void update() {
        double mouseX = FXUtil.getMouseX();
        double mouseY = FXUtil.getMouseY();
        if (stage != null) {
            stage.setX(mouseX + 15);
            stage.setY(mouseY + 5);
        }
    }

    private boolean shown;
    public void showHover(Window owner) {
        if (shown) return;
        timeline.play();
        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.initOwner(owner);
        double mouseX = FXUtil.getMouseX();
        double mouseY = FXUtil.getMouseY();
        stage.setX(mouseX + 15);
        stage.setY(mouseY + 5);
        stage.show();
        if (owner != null) owner.requestFocus();
        shown = true;
    }

    public BorderPane getPane() {
        return pane;
    }

    public void hideHover() {
        if (!shown) return;
        timeline.stop();
        stage.hide();
        stage = null;
        shown = false;
    }

    public static class ItemHover extends VBox {
        private Colorizer displayName = new Colorizer(Font.font("Minecraft", 18), ChestNodeParameter.COLOR_CHAR);
        private Colorizer lore = new Colorizer(Font.font("Minecraft", 18), ChestNodeParameter.COLOR_CHAR);
        private StringProperty display = new SimpleStringProperty();
        private ObservableList<String> loreList = FXCollections.observableArrayList();
        public ItemHover() {
            getChildren().addAll(displayName, lore);
            lore.stringProperty().bind(Bindings.createStringBinding(() -> "&5&o"+String.join("\n&5&o", loreList), loreList));
            displayName.stringProperty().bind(Bindings.createStringBinding(() -> "&f&o"+display.get(), display));
            setSpacing(2);
        }

        public StringProperty displayNameProperty() {
            return display;
        }

        public ObservableList<String> getLore() {
            return loreList;
        }
    }
}
