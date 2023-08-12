package it.angrybear.Velocity.Utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtils {

    public static TextComponent messageToComponent(String message) {
        return message == null ? null : LegacyComponentSerializer.legacySection()
                .deserialize(message.replace("&", "§"));
    }
}