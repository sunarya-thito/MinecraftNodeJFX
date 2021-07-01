import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import net.md_5.bungee.chat.*;
import thito.nodeflow.minecraft.*;
import thito.nodeflow.minecraft.chat.*;

public class TextUITest extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ChatComponentArea area = new ChatComponentArea();
        ComplexColorizer colorizer = new ComplexColorizer();
        area.getEditor().beingUpdatedProperty().addListener((obs, old, val) -> {
            colorizer.setComponents(area.getEditor().getComponents());
            System.out.println(ComponentSerializer.toString(area.getEditor().getComponents()));
        });
        Scene scene = new Scene(new VBox(area, colorizer), 600, 400);
        primaryStage.setScene(scene);
        area.requestFocus();
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }
}
