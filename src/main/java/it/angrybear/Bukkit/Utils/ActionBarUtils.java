package it.angrybear.Bukkit.Utils;

import it.angrybear.Utils.StringUtils;
import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarUtils {
    public static void sendActionBar(Player player, String text) {
        if (VersionsUtils.is1_9()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
            return;
        }
        text = StringUtils.parseMessage(text);

        Class<?> packetPlayOutChatClass = NMSUtils.getNMSClass("PacketPlayOutChat");
        ReflObject<?> iChatBaseComponent = NMSUtils.getChatSerializerObject(text);
        ReflObject<?> packet = new ReflObject<>(packetPlayOutChatClass.getCanonicalName(), iChatBaseComponent.getObject(), (byte) 2);

        PacketsUtils.sendPacket(player, packet);
    }
}