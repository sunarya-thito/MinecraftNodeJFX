package thito.nodeflow.minecraft;

import javafx.beans.property.*;
import javafx.event.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import thito.nodeflow.minecraft.chat.*;

import java.util.*;

public class ComplexColorizer extends TextFlow {

    static {
        FXUtil.poke();
    }
    private ObjectProperty<BaseComponent[]> components = new SimpleObjectProperty<>();
    private MinecraftHover hover = new MinecraftHover();
    private IntegerProperty requestHover = new SimpleIntegerProperty();
    public ComplexColorizer() {
        setBackground(new Background(new BackgroundImage(ChatComponentEditor.getBackgroundDirt(), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
        requestHover.addListener((obs, old, val) -> {
            if (val.intValue() <= 0) {
                hover.hideHover();
            } else {
                hover.showHover(getScene().getWindow());
            }
        });
        this.components.addListener((obs, old, val) -> {
            getChildren().clear();
            for (BaseComponent c : val) {
                append(c);
            }
        });
        setOnMouseEntered(Event::consume);
    }

    public ComplexColorizer(BaseComponent[] components) {
        this();
        this.components.set(components);
    }

    public BaseComponent[] getComponents() {
        return components.get();
    }

    public void setComponents(BaseComponent[] components) {
        this.components.set(components);
    }

    public ObjectProperty<BaseComponent[]> componentsProperty() {
        return components;
    }

    void append(BaseComponent component) {
        Text text;
        if (component.isObfuscated()) {
            text = new ObfuscatedText();
            if (component instanceof TextComponent) {
                ((ObfuscatedText) text).wrappedProperty().set(((TextComponent) component).getText());
            } else if (component instanceof TranslatableComponent) {
                ((ObfuscatedText) text).wrappedProperty().set(MinecraftLanguage.getMap().get(((TranslatableComponent) component).getTranslate()));
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            text = new Text();
            if (component instanceof TextComponent) {
                text.setText(((TextComponent) component).getText());
            } else if (component instanceof TranslatableComponent) {
                text.setText(MinecraftLanguage.getMap().get(((TranslatableComponent) component).getTranslate()));
            } else {
                throw new UnsupportedOperationException();
            }
        }

        text.setFont(Font.font("Minecraft", 18));

        StringBuilder builder = new StringBuilder();

        if (component.isBold()) {
            builder.append("-fx-font-weight: bold;");
        }

        if (component.isUnderlined()) {
            text.setUnderline(true);
        }

        if (component.isStrikethrough()) {
            text.setStrikethrough(true);
        }

        if (component.isItalic()) {
            builder.append("-fx-font-style: italic;");
        }

        ChatColor color = component.getColor();
        if (color != null) {
            java.awt.Color c = color.getColor();
            text.setFill(Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 255d));
        }

        HoverEvent hover = component.getHoverEvent();
        if (hover != null) {
            BaseComponent[] value = hover.getValue();
            if (hover.getAction() == HoverEvent.Action.SHOW_TEXT && value != null) {
                text.setOnMouseEntered(event -> {
                    requestHover.set(requestHover.get() + 1);
                    ComplexColorizer colorizer = new ComplexColorizer(value);
                    colorizer.setBackground(null);
                    this.hover.getPane().setCenter(colorizer);
                });
                text.setOnMouseExited(event -> {
                    requestHover.set(requestHover.get() - 1);
                });
            }
        }

        ClickEvent click = component.getClickEvent();
        if (click != null) {
            text.setOnMousePressed(event -> {
                requestHover.set(requestHover.get() + 1);
                ComplexColorizer colorizer = new ComplexColorizer();
                TextComponent comp = new TextComponent("Action: "+click.getAction()+"\nValue: "+click.getValue());
                comp.setColor(ChatColor.GOLD);
                colorizer.setComponents(new BaseComponent[] {comp});
                this.hover.getPane().setCenter(colorizer);
                event.consume();
            });
            text.setOnMouseExited(event -> {
                requestHover.set(requestHover.get() - 1);
                event.consume();
            });
        }

        text.setStyle(builder.toString());

        getChildren().add(text);

        List<BaseComponent> extra = component.getExtra();
        if (extra != null) {
            for (BaseComponent c : extra) {
                append(c);
            }
        }
    }
}
