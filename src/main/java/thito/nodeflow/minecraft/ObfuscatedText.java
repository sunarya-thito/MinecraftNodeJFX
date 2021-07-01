package thito.nodeflow.minecraft;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.*;

import java.util.*;

public class ObfuscatedText extends Text {
    public static final char[] OBFUSCATION = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    };

    public static final char[] LOWER_OBFUSCATION = {
            'a', 'c', 'e', 'g', 'm', 'n', 'o', 'r', 's', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    public static final char[] DIGIT_OBFUSCATION = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static char obfuscatedChar(char c) {
        if (Character.isDigit(c)) {
            return DIGIT_OBFUSCATION[new Random().nextInt(DIGIT_OBFUSCATION.length)];
        }
        if (Character.isLowerCase(c)) {
            return LOWER_OBFUSCATION[new Random().nextInt(LOWER_OBFUSCATION.length)];
        }
        return OBFUSCATION[new Random().nextInt(OBFUSCATION.length)];
    }

    private StringProperty wrapped = new SimpleStringProperty();
    private Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> obfuscate()));

    public ObfuscatedText() {
        timeline.setCycleCount(-1);
        ChangeListener<Boolean> show = (obs, old, val) -> {
            if (val) {
                timeline.play();
            } else {
                timeline.stop();
            }
        };
        ChangeListener<Window> win = (obs, old, val) -> {
            if (old != null) {
                old.showingProperty().removeListener(show);
            }
            if (val == null) {
                timeline.stop();
            } else {
                val.showingProperty().addListener(show);
                if (val.isShowing()) {
                    timeline.play();
                }
            }
        };
        sceneProperty().addListener((obs, old, val) -> {
            if (old != null) {
                Window x = old.getWindow();
                if (x != null) x.showingProperty().removeListener(show);
                old.windowProperty().removeListener(win);
            }
            if (val == null) {
                timeline.stop();
            } else {
                val.windowProperty().addListener(win);
                if (val.getWindow() != null && val.getWindow().isShowing()) {
                    timeline.play();
                }
            }
        });
        if (getScene() != null) {
            getScene().windowProperty().addListener(win);
            if (getScene().getWindow() != null && getScene().getWindow().isShowing()) {
                timeline.play();
            }
        }
    }

    private void obfuscate() {
        char[] chars = new char[wrapped.get().length()];
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isLetterOrDigit(wrapped.get().charAt(i))) {
                chars[i] = wrapped.get().charAt(i);
                continue;
            }
            chars[i] = obfuscatedChar(wrapped.get().charAt(i));
        }
        setText(new String(chars));
    }

    public StringProperty wrappedProperty() {
        return wrapped;
    }
}
