package thito.nodeflow.minecraft.chat;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import org.fxmisc.flowless.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
import org.reactfx.*;
import thito.nodeflow.minecraft.*;

import java.util.function.*;

public class ChatComponentArea extends BorderPane {
    static {
        FXUtil.poke();
    }
    private ChatComponentEditor area;
    SuspendableNo updatingToolbar = new SuspendableNo();
    Button undoBtn, redoBtn, cutBtn, copyBtn, pasteBtn, italicBtn, underlineBtn, strikeBtn, boldBtn, obfuscateBtn, translateBtn, hoverBtn, clickBtn;
    ColorPicker textColorPicker;
    CheckBox wrapToggle;
    FlowPane toolBar;
    BiConsumer<ClickEvent, Consumer<ClickEvent>> clickEventSupplier;
    BiConsumer<BaseComponent[], Consumer<BaseComponent[]>> hoverEventSupplier;
    StringProperty undoText = new SimpleStringProperty(), redoText = new SimpleStringProperty(), cutText = new SimpleStringProperty(),
    copyText = new SimpleStringProperty(), pasteText = new SimpleStringProperty(), boldText = new SimpleStringProperty(),
    italicText = new SimpleStringProperty(), underlineText = new SimpleStringProperty(), strikeThroughText = new SimpleStringProperty(),
    obfuscateText = new SimpleStringProperty(), flagAsTranslateText = new SimpleStringProperty(), hoverText = new SimpleStringProperty(),
    clickText = new SimpleStringProperty(), wrapText = new SimpleStringProperty();
    public ChatComponentArea() {
        area = new ChatComponentEditor(this);
        area.setPadding(new Insets(10));
        wrapToggle = new CheckBox();
        Tooltip t = new Tooltip();
        t.textProperty().bind(wrapText);
        wrapToggle.setTooltip(t);
        wrapToggle.setSelected(true);
        area.wrapTextProperty().bind(wrapToggle.selectedProperty());
        undoBtn = createButton("mc-chat-undo", area::undo, undoText);
        redoBtn = createButton("mc-chat-redo", area::redo, redoText);
        cutBtn = createButton("mc-chat-cut", area::cut, cutText);
        copyBtn = createButton("mc-chat-copy", area::copy, copyText);
        pasteBtn = createButton("mc-chat-paste", area::paste, pasteText);
        boldBtn = createButton("mc-chat-bold", this::toggleBold, boldText);
        italicBtn = createButton("mc-chat-italic", this::toggleItalic, italicText);
        underlineBtn = createButton("mc-chat-underline", this::toggleUnderline, underlineText);
        strikeBtn = createButton("mc-chat-strikethrough", this::toggleStrikethrough, strikeThroughText);
        obfuscateBtn = createButton("mc-chat-obfuscate", this::toggleObfuscate, obfuscateText);
        translateBtn = createButton("mc-chat-translate", this::toggleTranslate, flagAsTranslateText);
        hoverBtn = createButton("mc-chat-hover", () -> {
            if (hoverEventSupplier != null) {
                StyleSpans<TextStyle> style = getSelection();
                if (style != null) {
                    TextStyle current = style.styleStream().filter(x -> x.hover != null).findFirst().orElse(null);
                    hoverEventSupplier.accept(current == null ? null : current.hover, x -> setHover(x));
                }
            }
        }, hoverText);
        clickBtn = createButton("mc-chat-click", () -> {
            if (clickEventSupplier != null) {
                StyleSpans<TextStyle> style = getSelection();
                if (style != null) {
                    TextStyle current = style.styleStream().filter(x -> x.click != null).findFirst().orElse(null);
                    clickEventSupplier.accept(current == null ? null : current.click, x -> setClick(x));
                }
            }
        }, clickText);

        Text bold = new Text("B");
        boldBtn.setGraphic(bold);
        bold.setFont(Font.font("Minecraft", FontWeight.BOLD, 18));

        Text italic = new Text("I");
        italicBtn.setGraphic(italic);
        italic.setFont(Font.font("Minecraft", FontPosture.ITALIC, 18));

        Text under = new Text("U");
        underlineBtn.setGraphic(under);
        under.setFont(Font.font("Minecraft", 18));
        underlineBtn.setUnderline(true);

        Text strike = new Text("S");
        strikeBtn.setGraphic(strike);
        strike.setStrikethrough(true);
        strike.setFont(Font.font("Minecraft", 18));

        ObfuscatedText obfuscated = new ObfuscatedText();
        obfuscated.wrappedProperty().set("O");
        obfuscated.setFont(Font.font("Minecraft", 18));
        obfuscateBtn.setFont(Font.font("Minecraft", 18));
        obfuscateBtn.setGraphic(obfuscated);

        Text tr = new Text("T");
        translateBtn.setGraphic(tr);
        tr.setFont(Font.font("Minecraft", 18));

        textColorPicker = new ColorPicker(Color.BLACK);

        for (ChatColor color : ChatColor.values()) {
            java.awt.Color c = color.getColor();
            if (c == null) continue;
            textColorPicker.getCustomColors().add(Color.rgb(c.getRed(), c.getGreen(), c.getBlue()));
        }

        textColorPicker.setTooltip(new Tooltip("Text color"));

        textColorPicker.valueProperty().addListener((o, old, color) -> updateTextColor(color));

        undoBtn.disableProperty().bind(area.undoAvailableProperty().map(x -> !x));
        redoBtn.disableProperty().bind(area.redoAvailableProperty().map(x -> !x));

        BooleanBinding selectionEmpty = new BooleanBinding() {
            { bind(area.selectionProperty()); }

            @Override
            protected boolean computeValue() {
                return area.getSelection().getLength() == 0;
            }
        };

        cutBtn.disableProperty().bind(selectionEmpty);
        copyBtn.disableProperty().bind(selectionEmpty);

        toolBar = new FlowPane(
                wrapToggle, new Separator(Orientation.VERTICAL),
                undoBtn, redoBtn, new Separator(Orientation.VERTICAL),
                cutBtn, copyBtn, pasteBtn, new Separator(Orientation.VERTICAL),
                boldBtn, italicBtn, underlineBtn, strikeBtn, obfuscateBtn, translateBtn, clickBtn, hoverBtn, new Separator(Orientation.VERTICAL), textColorPicker);

        toolBar.setPadding(new Insets(5));
        toolBar.setVgap(5);
        toolBar.setHgap(5);

        VirtualizedScrollPane<GenericStyledArea<Object, String, TextStyle>> vsPane = new VirtualizedScrollPane<>(area);

        setTop(toolBar);
        setCenter(vsPane);
    }

    public void setClickEventSupplier(BiConsumer<ClickEvent, Consumer<ClickEvent>> clickEventSupplier) {
        this.clickEventSupplier = clickEventSupplier;
    }

    public void setHoverEventSupplier(BiConsumer<BaseComponent[], Consumer<BaseComponent[]>> hoverEventSupplier) {
        this.hoverEventSupplier = hoverEventSupplier;
    }

    public StringProperty wrapTextProperty() {
        return wrapText;
    }

    public Button getHoverBtn() {
        return hoverBtn;
    }

    public Button getClickBtn() {
        return clickBtn;
    }

    public ChatComponentEditor getEditor() {
        return area;
    }

    public String getUndoText() {
        return undoText.get();
    }

    public StringProperty undoTextProperty() {
        return undoText;
    }

    public String getRedoText() {
        return redoText.get();
    }

    public StringProperty redoTextProperty() {
        return redoText;
    }

    public String getCutText() {
        return cutText.get();
    }

    public StringProperty cutTextProperty() {
        return cutText;
    }

    public String getCopyText() {
        return copyText.get();
    }

    public StringProperty copyTextProperty() {
        return copyText;
    }

    public String getPasteText() {
        return pasteText.get();
    }

    public StringProperty pasteTextProperty() {
        return pasteText;
    }

    public String getBoldText() {
        return boldText.get();
    }

    public StringProperty boldTextProperty() {
        return boldText;
    }

    public String getItalicText() {
        return italicText.get();
    }

    public StringProperty italicTextProperty() {
        return italicText;
    }

    public String getUnderlineText() {
        return underlineText.get();
    }

    public StringProperty underlineTextProperty() {
        return underlineText;
    }

    public String getStrikeThroughText() {
        return strikeThroughText.get();
    }

    public StringProperty strikeThroughTextProperty() {
        return strikeThroughText;
    }

    public String getObfuscateText() {
        return obfuscateText.get();
    }

    public StringProperty obfuscateTextProperty() {
        return obfuscateText;
    }

    public String getFlagAsTranslateText() {
        return flagAsTranslateText.get();
    }

    public StringProperty flagAsTranslateTextProperty() {
        return flagAsTranslateText;
    }

    public String getHoverText() {
        return hoverText.get();
    }

    public StringProperty hoverTextProperty() {
        return hoverText;
    }

    public String getClickText() {
        return clickText.get();
    }

    public StringProperty clickTextProperty() {
        return clickText;
    }

    public FlowPane getToolBar() {
        return toolBar;
    }

    public CheckBox getWrapToggle() {
        return wrapToggle;
    }

    public Button getBoldBtn() {
        return boldBtn;
    }

    public Button getItalicBtn() {
        return italicBtn;
    }

    public Button getObfuscateBtn() {
        return obfuscateBtn;
    }

    public Button getStrikeBtn() {
        return strikeBtn;
    }

    public Button getUnderlineBtn() {
        return underlineBtn;
    }

    public Button getPasteBtn() {
        return pasteBtn;
    }

    public Button getCopyBtn() {
        return copyBtn;
    }

    public Button getCutBtn() {
        return cutBtn;
    }

    public Button getUndoBtn() {
        return undoBtn;
    }

    public Button getRedoBtn() {
        return redoBtn;
    }

    private void setHover(BaseComponent[] hover) {
        updateStyleInSelection(spans -> {
            TextStyle x = new TextStyle();
            x.hover = hover;
            return x;
        });
    }

    private void setClick(ClickEvent event) {
        updateStyleInSelection(spans -> {
            TextStyle x = new TextStyle();
            x.click = event;
            return x;
        });
    }

    private void updateTextColor(Color color) {
        if(!updatingToolbar.get()) {
            TextStyle x = new TextStyle();
            x.color = ChatColor.of(new java.awt.Color((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), (int) (color.getOpacity() * 255)));
            updateStyleInSelection(x);
        }
    }

    private void toggleBold() {
        updateStyleInSelection(spans -> {
            TextStyle x = new TextStyle();
            x.bold = !spans.styleStream().allMatch(style -> style.bold != null && style.bold);
            return x;
        });
    }

    private void toggleTranslate() {
        updateStyleInSelection(spans -> {
            TextStyle x = new TextStyle();
            x.translateText = !spans.styleStream().allMatch(style -> style.translateText != null && style.translateText);
            return x;
        });
    }

    private void toggleItalic() {
        updateStyleInSelection(spans -> {
            TextStyle x = new TextStyle();
            x.italic = !spans.styleStream().allMatch(style -> style.italic != null && style.italic);
            return x;
        });
    }

    private void toggleUnderline() {
        updateStyleInSelection(spans -> {
            TextStyle x = new TextStyle();
            x.underlined = !spans.styleStream().allMatch(style -> style.underlined != null && style.underlined);
            return x;
        });
    }

    private void toggleObfuscate() {
        updateStyleInSelection(spans -> {
            TextStyle x = new TextStyle();
            x.obfuscated = !spans.styleStream().allMatch(style -> style.obfuscated != null && style.obfuscated);
            return x;
        });
    }

    private void toggleStrikethrough() {
        updateStyleInSelection(spans -> {
            TextStyle x = new TextStyle();
            x.strikethrough = !spans.styleStream().allMatch(style -> style.strikethrough != null && style.strikethrough);
            return x;
        });
    }

    private void updateStyleInSelection(Function<StyleSpans<TextStyle>, TextStyle> mixinGetter) {
        IndexRange selection = area.getSelection();
        if(selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
            TextStyle mixin = mixinGetter.apply(styles);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            area.setStyleSpans(selection.getStart(), newStyles);
        }
    }

    private StyleSpans<TextStyle> getSelection() {
        IndexRange selection = area.getSelection();
        if(selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
            return styles;
        }
        return null;
    }

    private void updateStyleInSelection(TextStyle mixin) {
        IndexRange selection = area.getSelection();
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = area.getStyleSpans(selection);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            area.setStyleSpans(selection.getStart(), newStyles);
        }
    }

    private Button createButton(String styleClass, Runnable action, StringProperty toolTip) {
        Button button = new Button();
        button.getStyleClass().add(styleClass);
        button.setOnAction(evt -> {
            action.run();
            area.requestFocus();
        });
        button.setPadding(new Insets(0));
        button.setPrefWidth(25);
        button.setPrefHeight(25);
        if (toolTip != null) {
            Tooltip tooltip = new Tooltip();
            tooltip.textProperty().bind(toolTip);
            button.setTooltip(tooltip);
        }
        return button;
    }

    private ToggleButton createToggleButton(ToggleGroup grp, String styleClass, Runnable action, String toolTip) {
        ToggleButton button = new ToggleButton();
        button.setToggleGroup(grp);
        button.getStyleClass().add(styleClass);
        button.setOnAction(evt -> {
            action.run();
            area.requestFocus();
        });
        button.setPrefWidth(25);
        button.setPrefHeight(25);
        if (toolTip != null) {
            button.setTooltip(new Tooltip(toolTip));
        }
        return button;
    }
}
