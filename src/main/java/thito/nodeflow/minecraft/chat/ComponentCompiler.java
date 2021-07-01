package thito.nodeflow.minecraft.chat;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ComponentCompiler {
    public static Reference compile(BaseComponent component) {
        ILocalField local;
        if (component instanceof TextComponent) {
            local = Code.getCode().getLocalFieldMap().createField(Java.Class(TextComponent.class));
            local.set(Java.Class(TextComponent.class).getConstructor(String.class).get().newInstance(((TextComponent) component).getText()));
        } else if (component instanceof TranslatableComponent) {
            local = Code.getCode().getLocalFieldMap().createField(Java.Class(TranslatableComponent.class));
            local.set(Java.Class(TranslatableComponent.class).getConstructor(String.class).get().newInstance(((TranslatableComponent) component).getTranslate()));
        } else throw new UnsupportedOperationException();
        if (component.isBoldRaw() != null) {
            local.get().method("setBold", Boolean.class).invokeVoid(component.isBoldRaw());
        }
        if (component.isItalicRaw() != null) {
            local.get().method("setItalic", Boolean.class).invokeVoid(component.isItalicRaw());
        }
        if (component.isUnderlinedRaw() != null) {
            local.get().method("setUnderlined", Boolean.class).invokeVoid(component.isUnderlinedRaw());
        }
        if (component.isStrikethroughRaw() != null) {
            local.get().method("setStrikethrough", Boolean.class).invoke(component.isStrikethroughRaw());
        }
        if (component.getColorRaw() != null) {
            Color color = component.getColorRaw().getColor();
            local.get().method("setColor", ChatColor.class).invokeVoid(Java.Class(ChatColor.class).staticMethod("of", Color.class)
                    .invoke(Java.Class(Color.class).getConstructor(int.class, int.class, int.class, int.class).get()
                            .newInstance(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())));
        }
        ClickEvent click = component.getClickEvent();
        if (click != null) {
            local.get().method("setClickEvent", ClickEvent.class).invokeVoid(Java.Class(ClickEvent.class).getConstructor(ClickEvent.Action.class, String.class)
                    .get().newInstance(click.getAction(), click.getValue()));
        }
        HoverEvent event = component.getHoverEvent();
        if (event != null) {
            List<Content> contentList = event.getContents();
            ILocalField contents = Code.getCode().getLocalFieldMap().createField(Java.Class(Content[].class));
            contents.set(Java.NewArray(Content.class, contentList.size()));
            for (int j = 0; j < contentList.size(); j++) {
                ILocalField content;
                Content c = contentList.get(j);
                if (c instanceof Text) {
                    content = Code.getCode().getLocalFieldMap().createField(Java.Class(Text.class));
                    Object value = ((Text) c).getValue();
                    if (value instanceof String) {
                        content.set(Java.Class(Text.class).getConstructor(String.class).get().newInstance(value));
                    } else if (value instanceof BaseComponent[]) {
                        Reference compiled = compile((BaseComponent[]) value);
                        content.set(compiled);
                    } else throw new UnsupportedOperationException();
                    contents.get().arraySet(j, content.get());
                } else throw new UnsupportedOperationException();
            }
            local.get().method("setHoverEvent", HoverEvent.class).invokeVoid(Java.Class(HoverEvent.class).getConstructor(HoverEvent.Action.class, Content[].class)
                    .get().newInstance(event.getAction(), contents.get()));
        }
        List<BaseComponent> extra = component.getExtra();
        if (extra != null) {
            for (BaseComponent c : extra) {
                Reference compiled = compile(c);
                local.get().method("addExtra", BaseComponent.class).invokeVoid(compiled);
            }
        }
        return local.get();
    }
    public static Reference compile(BaseComponent[] components) {
        ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(BaseComponent[].class));
        field.set(Java.NewArray(BaseComponent.class, components.length));
        for (int i = 0; i < components.length; i++) {
            Reference reference = compile(components[i]);
            field.get().arraySet(i, reference);
        }
        return field.get();
    }
}
