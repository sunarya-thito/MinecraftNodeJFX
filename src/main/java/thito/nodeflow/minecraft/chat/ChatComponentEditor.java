package thito.nodeflow.minecraft.chat;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
import thito.nodeflow.minecraft.*;

import java.util.*;
import java.util.function.*;

public class ChatComponentEditor extends GenericStyledArea<Object, String, TextStyle> {

    private static WritableImage BACKGROUND_DIRT;

    public static WritableImage getBackgroundDirt() {
        if (BACKGROUND_DIRT == null) {
            WritableImage writableImage = new WritableImage(50, 50);
            Image image = new Image(FXUtil.class.getResource("options_background.png").toExternalForm());
            PixelWriter writer = writableImage.getPixelWriter();
            PixelReader reader = image.getPixelReader();
            for (int x = 0; x < writableImage.getWidth(); x++) {
                for (int y = 0; y < writableImage.getHeight(); y++) {
                    Color color = reader.getColor(
                            (int) (x * (image.getWidth() / writableImage.getWidth())),
                            (int) (y * (image.getHeight() / writableImage.getHeight())));
                    color = color.darker().darker().darker();
                    writer.setColor(x, y, color);
                }
            }
            BACKGROUND_DIRT = writableImage;
        }
        return BACKGROUND_DIRT;
    }

    private final static TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();
    public ChatComponentEditor(ChatComponentArea area) {
        super(null, (par, style) -> par.setStyle(""), new TextStyle(), styledTextOps, seg -> create(seg));
        setOnMouseEntered(Event::consume);
        setBackground(new Background(
                new BackgroundImage(getBackgroundDirt(),
                        BackgroundRepeat.REPEAT,
                        BackgroundRepeat.REPEAT,
                        null,
                        new BackgroundSize(getBackgroundDirt().getWidth(), getBackgroundDirt().getHeight(), false, false, false, false))
        ));
        beingUpdatedProperty().addListener((o, old, beingUpdated) -> {
            if(!beingUpdated) {
                boolean bold, italic, underline, strike, obfuscated, translate;
                Color textColor;

                IndexRange selection = getSelection();
                if(selection.getLength() != 0) {
                    StyleSpans<TextStyle> styles = getStyleSpans(selection);
                    bold = styles.styleStream().anyMatch(s -> s.bold != null && s.bold);
                    italic = styles.styleStream().anyMatch(s -> s.italic != null && s.italic);
                    underline = styles.styleStream().anyMatch(s -> s.underlined != null && s.underlined);
                    strike = styles.styleStream().anyMatch(s -> s.strikethrough != null && s.strikethrough);
                    obfuscated = styles.styleStream().anyMatch(s -> s.obfuscated != null && s.obfuscated);
                    translate = styles.styleStream().anyMatch(s -> s.translateText != null && s.translateText);
                    Color[] colors = styles.styleStream().map(style -> style.color == null ? Color.WHITE : Color.rgb(style.color.getColor().getRed(), style.color.getColor().getGreen(), style.color.getColor().getBlue(), style.color.getColor().getAlpha() / 255d)).distinct().toArray(Color[]::new);
                    textColor = colors.length == 1 ? colors[0] : null;
                } else {
                    int p = getCurrentParagraph();
                    int col = getCaretColumn();
                    TextStyle style = getStyleAtPosition(p, col);
                    bold = style.bold != null && style.bold;
                    italic = style.italic != null && style.italic;
                    underline = style.underlined != null && style.underlined;
                    strike = style.strikethrough != null && style.strikethrough;
                    obfuscated = style.obfuscated != null && style.obfuscated;
                    translate = style.translateText != null && style.translateText;
                    if (style.color == null) {
                        textColor = Color.WHITE;
                    } else {
                        textColor = Color.rgb(style.color.getColor().getRed(), style.color.getColor().getGreen(), style.color.getColor().getBlue(), style.color.getColor().getAlpha() / 255d);
                    }
                }

                area.updatingToolbar.suspendWhile(() -> {
                    if (obfuscated) {
                        if (!area.obfuscateBtn.getStyleClass().contains("pressed")) {
                            area.obfuscateBtn.getStyleClass().add("pressed");
                        }
                    }
                    if (translate) {
                        if (!area.translateBtn.getStyleClass().contains("pressed")) {
                            area.translateBtn.getStyleClass().add("pressed");
                        }
                    }
                    if (bold) {
                        if(!area.boldBtn.getStyleClass().contains("pressed")) {
                            area.boldBtn.getStyleClass().add("pressed");
                        }
                    } else {
                        area.boldBtn.getStyleClass().remove("pressed");
                    }

                    if (italic) {
                        if(!area.italicBtn.getStyleClass().contains("pressed")) {
                            area.italicBtn.getStyleClass().add("pressed");
                        }
                    } else {
                        area.italicBtn.getStyleClass().remove("pressed");
                    }

                    if (underline) {
                        if(!area.underlineBtn.getStyleClass().contains("pressed")) {
                            area.underlineBtn.getStyleClass().add("pressed");
                        }
                    } else {
                        area.underlineBtn.getStyleClass().remove("pressed");
                    }

                    if (strike) {
                        if(!area.strikeBtn.getStyleClass().contains("pressed")) {
                            area.strikeBtn.getStyleClass().add("pressed");
                        }
                    } else {
                        area.strikeBtn.getStyleClass().remove("pressed");
                    }

                    if (textColor != null) {
                        area.textColorPicker.setValue(textColor);
                    }

                });
            }
        });
    }

    public void setComponents(BaseComponent[] components) {
        clear();
        if (components != null) {
            for (BaseComponent c : components) {
                append(c);
            }
        }
    }

    void append(BaseComponent component) {
        if (component == null) return;
        TextStyle style = new TextStyle();
        String segment;
        if (component instanceof TextComponent) {
            segment = ((TextComponent) component).getText();
        } else if (component instanceof TranslatableComponent) {
            segment = ((TranslatableComponent) component).getTranslate();
            style.translateText = true;
        } else throw new UnsupportedOperationException();

        if (component.isBold()) {
            style.bold = true;
        }
        if (component.isItalic()) {
            style.italic = true;
        }
        if (component.isUnderlined()) {
            style.underlined = true;
        }
        if (component.isStrikethrough()) {
            style.strikethrough = true;
        }
        if (component.isObfuscated()) {
            style.obfuscated = true;
        }

        style.color = component.getColor();

        HoverEvent event = component.getHoverEvent();
        if (event != null) {
            if (event.getAction() == HoverEvent.Action.SHOW_TEXT) {
                BaseComponent[] hover = event.getValue();
                if (hover != null) {
                    style.hover = hover;
                }
            }
        }

        ClickEvent click = component.getClickEvent();
        style.click = click;

        append(segment, style);

        List<BaseComponent> extra = component.getExtra();
        if (extra != null) {
            for (BaseComponent c : extra) {
                append(c);
            }
        }
    }

    public BaseComponent[] getComponents() {
        ArrayList<BaseComponent> components = new ArrayList<>();
        boolean first = true;
        for (Paragraph<Object, String, TextStyle> par : getDocument().getParagraphs()) {
            if (first) first = false;
            else components.add(new TextComponent("\n"));
            for (StyledSegment<String, TextStyle> seg : par.getStyledSegments()) {
                BaseComponent component;
                String value = seg.getSegment();
                TextStyle style = seg.getStyle();
                if (style.translateText != null && style.translateText) {
                    component = new TranslatableComponent(value);
                } else {
                    component = new TextComponent(value);
                }
                component.setObfuscated(style.obfuscated);
                component.setUnderlined(style.underlined != null && style.underlined);
                component.setBold(style.bold != null && style.bold);
                component.setItalic(style.italic != null && style.italic);
                component.setStrikethrough(style.strikethrough != null && style.strikethrough);
                component.setColor(style.color == null ? ChatColor.WHITE : style.color);
                if (style.click != null) {
                    component.setClickEvent(style.click);
                }
                if (style.hover != null) {
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(style.hover)));
                }

                components.add(component);
            }
        }
        return components.toArray(new BaseComponent[0]);
    }

    private static Node create(StyledSegment<String, TextStyle> seg) {
        return createStyledTextNode(seg.getSegment(), seg.getStyle());
    }

    public static Node createStyledTextNode(String text, TextStyle style) {
        ObfuscatedTextExt t = new ObfuscatedTextExt(text);
        t.setTextOrigin(VPos.TOP);
        t.getStyleClass().add("text");
        t.setStyle(style.toCSS());
        t.setObfuscate(style.obfuscated != null && style.obfuscated);
        if (style.translateText != null && style.translateText) {
            if (text.isEmpty()) {
                style.translateText = null;
            } else {
                t.setText(MinecraftLanguage.getMap().getOrDefault(text, "{?"+text+"}"));
            }
        }
        if (style.hover != null) {
            MinecraftHover hover = new MinecraftHover();
            TextFlow flow = new TextFlow();
            for (BaseComponent component : style.hover) {
                render(flow, component);
            }
            hover.getPane().setCenter(flow);
            t.setOnMouseEntered(e -> {
                hover.showHover(t.getScene().getWindow());
                e.consume();
            });
            t.setOnMouseExited(e -> {
                hover.hideHover();
                e.consume();
            });
        }
        return t;
    }

    private static void render(TextFlow flow, BaseComponent component) {
        if (component instanceof TextComponent) {
            ObfuscatedTextExt text = new ObfuscatedTextExt(((TextComponent) component).getText());
            TextStyle style = new TextStyle();
            if (component.isBold()) {
                style.bold = true;
            }
            if (component.isItalic()) {
                style.italic = true;
            }
            if (component.isStrikethrough()) {
                style.strikethrough = true;
            }
            if (component.isUnderlined()) {
                style.underlined = true;
            }
            if (component.isObfuscated()) {
                text.setObfuscate(true);
            }
            text.setStyle(style.toCSS());
            flow.getChildren().add(text);
            if (component.getExtra() != null) {
                for (BaseComponent child : component.getExtra()) {
                    render(flow, child);
                }
            }
        } else if (component instanceof TranslatableComponent) {
            ObfuscatedTextExt text = new ObfuscatedTextExt(MinecraftLanguage.getMap().getOrDefault(((TranslatableComponent) component).getTranslate(), "{???}"));
            TextStyle style = new TextStyle();
            if (component.isBold()) {
                style.bold = true;
            }
            if (component.isItalic()) {
                style.italic = true;
            }
            if (component.isStrikethrough()) {
                style.strikethrough = true;
            }
            if (component.isUnderlined()) {
                style.underlined = true;
            }
            if (component.isObfuscated()) {
                text.setObfuscate(true);
            }
            text.setStyle(style.toCSS());
            flow.getChildren().add(text);
            if (component.getExtra() != null) {
                for (BaseComponent child : component.getExtra()) {
                    render(flow, child);
                }
            }
        }
    }

}
