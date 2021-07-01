package thito.nodeflow.minecraft;

import com.sun.glass.ui.*;
import javafx.scene.text.*;

public class FXUtil {

    public static final Font MINECRAFT;
    static {
        MINECRAFT = Font.loadFont(ChestNodeParameter.class.getResourceAsStream("1_Minecraft-Regular.otf"), 14);
        Font.loadFont(ChestNodeParameter.class.getResourceAsStream("2_Minecraft-Italic.otf"), 14);
        Font.loadFont(ChestNodeParameter.class.getResourceAsStream("3_Minecraft-Bold.otf"), 14);
        Font.loadFont(ChestNodeParameter.class.getResourceAsStream("4_Minecraft-BoldItalic.otf"), 14);
    }

    public static void poke() {}

    public static final Robot ROBOT = com.sun.glass.ui.Application.GetApplication().createRobot();
    public static double getMouseX() {
        return ROBOT.getMouseX();
    }

    public static double getMouseY() {
        return ROBOT.getMouseY();
    }
}
