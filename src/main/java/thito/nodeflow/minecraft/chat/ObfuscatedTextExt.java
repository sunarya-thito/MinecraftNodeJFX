package thito.nodeflow.minecraft.chat;

import javafx.animation.*;
import javafx.beans.value.*;
import javafx.stage.*;
import javafx.util.*;
import org.fxmisc.richtext.*;
import thito.nodeflow.minecraft.*;

import java.util.*;

public class ObfuscatedTextExt extends TextExt {
    private boolean obfuscate;
    private Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> obfuscate()));
    public ObfuscatedTextExt(String text) {
        super(text);
        timeline.setCycleCount(-1);
        ChangeListener<Boolean> show = (obs, old, val) -> {
            updateTime();
        };
        ChangeListener<Window> win = (obs, old, val) -> {
            if (old != null) {
                old.showingProperty().removeListener(show);
            }
            if (val == null) {
                updateTime();
            } else {
                val.showingProperty().addListener(show);
                if (val.isShowing()) {
                    updateTime();
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
                updateTime();
            } else {
                val.windowProperty().addListener(win);
                if (val.getWindow() != null && val.getWindow().isShowing()) {
                    updateTime();
                }
            }
        });
        if (getScene() != null) {
            getScene().windowProperty().addListener(win);
            if (getScene().getWindow() != null && getScene().getWindow().isShowing()) {
                updateTime();
            }
        }
    }

    public void setObfuscate(boolean obfuscate) {
        this.obfuscate = obfuscate;
        updateTime();
    }

    private void updateTime() {
        if (getScene() != null && getScene().getWindow() != null && getScene().getWindow().isShowing() && obfuscate) {
            timeline.play();
        } else {
            timeline.stop();
        }
    }
    private void obfuscate() {
        char[] chars = new char[getText().length()];
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isLetterOrDigit(getText().charAt(i))) {
                chars[i] = getText().charAt(i);
                continue;
            }
            chars[i] = ObfuscatedText.obfuscatedChar(getText().charAt(i));
        }
        setText(new String(chars));
    }

}
