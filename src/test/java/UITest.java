import javafx.application.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.stage.*;
import thito.nodeflow.minecraft.*;
import thito.nodejfx.*;
import thito.nodejfx.Node;
import thito.nodejfx.parameter.*;
import thito.nodejfx.parameter.type.*;

public class UITest extends Application {
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        NodeEditor editor = new NodeEditor();
        NodeViewport viewport = editor.getViewport();

        NodeCanvas canvas = viewport.getCanvas();
        canvas.nodeLinkStyleProperty().set(NodeLinkStyle.BEZIER_STYLE);
        editor.getCanvas().getSelectionContainer().getMode().set(ToolMode.SELECT);

        NodeContext.resizeFont(Font.font(null, FontWeight.BLACK, FontPosture.ITALIC, 125), 10);

        for (int i = 0; i < 2; i++) {
            canvas.getNodes().add(createNode(i % 5 + 1));
        }

        ChestNodeParameter chest = new ChestNodeParameter();
        chest.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() > 0) {
                chest.rowsProperty().set(chest.rowsProperty().get() + 1);
            } else {
                chest.rowsProperty().set(chest.rowsProperty().get() - 1);
            }
            event.consume();
        });
        Image image = new Image(ChestNodeParameter.class.getResource("DIAMOND.png").toExternalForm());
        for (ChestSlot slot : chest.getSlots()) {
            slot.addEventHandler(ScrollEvent.SCROLL, event -> {
                if (event.isControlDown()) {
                    if (event.getDeltaY() > 0) {
                        slot.durabilityPercentageProperty().set(slot.durabilityPercentageProperty().get() + 0.1);
                    } else {
                        slot.durabilityPercentageProperty().set(slot.durabilityPercentageProperty().get() - 0.1);
                    }
                } else {
                    if (event.getDeltaY() > 0) {
                        slot.amountProperty().set(slot.amountProperty().get() + 1);
                    } else {
                        slot.amountProperty().set(slot.amountProperty().get() - 1);
                    }
                }
                event.consume();
            });
            ImageView view = new ImageView(image);
            view.fitWidthProperty().set(32);
            view.fitHeightProperty().set(32);
            slot.amountProperty().addListener((obs, old, val) -> {
                if (val.intValue() > 0) {
                    slot.getItemContainer().setCenter(view);
                } else {
                    slot.getItemContainer().setCenter(null);
                }
            });
        }

//        canvas.getSelectionContainer().getMode().set(ToolMode.GROUPING);

        NodeGroup group = new NodeGroup();
        canvas.getGroups().add(group);

        Node node = new Node();
        node.highlightProperty().set(true);
        node.getParameters().add(chest);
        ItemParameter parameter = new ItemParameter();
        parameter.getPane().setCenter(new ImageView(image));
        node.getParameters().add(parameter);
        NodeContainer container = new NodeContainer(canvas);
        canvas.getChildren().add(1, container);
        container.getNodes().add(node);

        Scene scene = new Scene(editor, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    Node createNode(int count) {
        Node node = new Node();
        node.getParameters().addAll(
                new LabelParameter("Test"),
                castableType(new StringParameter("String")),
                castableType(new NumberParameter("Number", Double.class)),
                castableType(new EnumParameter<>("Enum", Test.class)),
                castableType(new BooleanParameter("Boolean")),
                new CharacterParameter("Character")
        );
        return node;
    }

    enum Test {

    }
    <T extends NodeParameter> T castableType(T parameter) {
        parameter.getInputType().set(JavaParameterType.getCastableType(((JavaParameterType) parameter.getInputType().get()).getType()));
        parameter.getOutputType().set(JavaParameterType.getCastableType(((JavaParameterType) parameter.getOutputType().get()).getType()));
        return parameter;
    }
}
