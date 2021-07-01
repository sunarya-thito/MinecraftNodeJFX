package thito.nodeflow.minecraft.chat;

import javafx.scene.text.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;

public class TextStyle {
    public static final TextStyle EMPTY = new TextStyle();
    public ChatColor color;
    @Deprecated // not yet ready for editor implementation
    public String font;
    public Boolean bold;
    public Boolean italic;
    public Boolean underlined;
    public Boolean strikethrough;
    public Boolean obfuscated;
    public ClickEvent click;
    public BaseComponent[] hover;
    public Boolean translateText;

    public TextStyle() {
    }

    public TextStyle(ChatColor color, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, ClickEvent click, BaseComponent[] hover, Boolean translateText) {
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
        this.click = click;
        this.hover = hover;
        this.translateText = translateText;
    }

    public String toCSS() {
        StringBuilder sb = new StringBuilder();
        sb.append("-fx-font-family: Minecraft; -fx-font-size: 18;");
        if (color != null) {
            sb.append("-fx-fill: rgb("+color.getColor().getRed()+", "+color.getColor().getGreen()+", "+color.getColor().getBlue()+", "+(color.getColor().getAlpha() / 255d)+");");
        } else {
            sb.append("-fx-fill: white;");
        }
        if (bold != null && bold) {
            sb.append("-fx-font-weight: bold;");
        }
        if (italic != null && italic) {
            sb.append("-fx-font-style: italic;");
        }
        if (underlined != null && underlined) {
            sb.append("-fx-underline: true;");
        }
        if (strikethrough != null && strikethrough) {
            sb.append("-fx-strikethrough: true;");
        }
        if (translateText != null) {
            if (click != null) {
                sb.append("-rtfx-background-color: rgb(0, 200, 200, 0.5);");
            } else {
                sb.append("-rtfx-background-color: rgb(200, 200, 200, 0.5);");
            }
        } else {
            if (click != null) {
                sb.append("-rtfx-background-color: rgb(0, 0, 200, 0.5);");
            } else {
                sb.append("-rtfx-background-color: transparent;");
            }
        }
        return sb.toString();
    }

    public TextStyle updateWith(TextStyle mixin) {
        return new TextStyle(mixin.color != null ? mixin.color : color,
                mixin.bold != null ? mixin.bold : bold,
                mixin.italic != null ? mixin.italic : italic,
                mixin.underlined != null ? mixin.underlined : underlined,
                mixin.strikethrough != null ? mixin.strikethrough : strikethrough,
                mixin.obfuscated != null ? mixin.obfuscated : obfuscated,
                mixin.click != null ? mixin.click : click,
                mixin.hover != null ? mixin.hover : hover,
                mixin.translateText != null ? mixin.translateText : translateText);
    }
}
