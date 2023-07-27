package it.angrybear.Utils;

import it.fulminazzo.reflectionutils.Objects.ReflObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentUtils {

    public static HoverEvent getTextHoverEvent(String text) {
        if (VersionsUtils.is1_16()) {
            // Text textComponent = new Text(text);
            ReflObject<?> textComponent = new ReflObject<>("net.md_5.bungee.api.chat.hover.content.Text", StringUtils.parseMessage(text));
            // return new HoverEvent(HoverEvent.Action.SHOW_TEXT, textComponent);
            return new ReflObject<>(HoverEvent.class,
                    new Class<?>[]{HoverEvent.Action.class, new ReflObject<>("net.md_5.bungee.api.chat.hover.content.Content",
                            false).getArray().getClass()},
                    HoverEvent.Action.SHOW_TEXT, textComponent.getArray(textComponent.getObject())).getObject();
        } else return getTextHoverEventLegacy(text);
    }

    public static HoverEvent getTextHoverEventLegacy(String text) {
        // return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(text)});
        return new ReflObject<>(HoverEvent.class, HoverEvent.Action.SHOW_TEXT,
                new BaseComponent[]{new TextComponent(StringUtils.parseMessage(text))}).getObject();
    }
}
