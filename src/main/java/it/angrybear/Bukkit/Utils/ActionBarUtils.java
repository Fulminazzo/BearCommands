package it.angrybear.Bukkit.Utils;

import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ActionBarUtils {
    public static void sendActionBar(Player player, String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);

        Class<?> packetPlayOutChatClass = NMSUtils.getNMSClass(VersionsUtils.is1_19() ?
                        "ClientboundSystemChatPacket" : "PacketPlayOutChat", "network.protocol.game");
        Class<?> chatMessageTypeClass = NMSUtils.getNMSClass("ChatMessageType", "network.chat");
        Class<?> iChatBaseComponentClass = NMSUtils.getNMSClass("IChatBaseComponent", "network.chat");

        ReflObject<?> iChatBaseComponent;
        if (VersionsUtils.is1_8()) iChatBaseComponent = NMSUtils.getChatSerializerObject(text);
        else iChatBaseComponent = new ReflObject<>("ChatComponentText", text);

        ReflObject<?> packet;
        if (VersionsUtils.is1_19())
            packet = new ReflObject<>(packetPlayOutChatClass.getCanonicalName(), iChatBaseComponent.getObject(), true);
        else if (VersionsUtils.is1_12()) {
            ReflObject<?> chatMessageType = new ReflObject<>(chatMessageTypeClass.getCanonicalName(), false)
                    .obtainField(VersionsUtils.is1_17() ? "c" : "GAME_INFO");
            if (VersionsUtils.is1_16())
                packet = new ReflObject<>(packetPlayOutChatClass.getCanonicalName(),
                        new Class[]{iChatBaseComponentClass, chatMessageTypeClass, UUID.class},
                        iChatBaseComponent.getObject(), chatMessageType.getObject(), player.getUniqueId());
            else
                packet = new ReflObject<>(packetPlayOutChatClass.getCanonicalName(),
                        new Class[]{iChatBaseComponentClass, chatMessageTypeClass},
                        iChatBaseComponent.getObject(), chatMessageType.getObject());
        } else if (VersionsUtils.is1_8())
            packet = new ReflObject<>(packetPlayOutChatClass.getCanonicalName(), iChatBaseComponent.getObject(), (byte) 2);
        else
            packet = new ReflObject<>(packetPlayOutChatClass.getCanonicalName(), iChatBaseComponent.getObject(), 1);
        PacketsUtils.sendPacket(player, packet);
    }
}