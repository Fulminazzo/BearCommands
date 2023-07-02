package it.angrybear.Utils;

import it.angrybear.Objects.Reflections.NMSReflObject;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleUtils {

    public static void sendTitle(Player player, String title) {
        sendGeneralTitle(player, title, null, 5, 20, 5);
    }

    public static void sendTitle(Player player, String title, int start, int duration, int end) {
        sendGeneralTitle(player, title, null, start, duration, end);
    }

    public static void sendSubTitle(Player player, String subTitle) {
        sendGeneralTitle(player, null, subTitle, 5, 20, 5);
    }

    public static void sendSubTitle(Player player, String subTitle, int start, int duration, int end) {
        sendGeneralTitle(player, null, subTitle, start, duration, end);
    }

    public static void sendGeneralTitle(Player player, String title, String subTitle) {
        sendGeneralTitle(player, title, subTitle, 5, 20, 5);
    }

    public static void sendGeneralTitle(Player player, String title, String subTitle, int start, int duration, int end) {
        if (title == null) title = "";
        title = ChatColor.translateAlternateColorCodes('&', title);
        if (subTitle == null) subTitle = "";
        subTitle = ChatColor.translateAlternateColorCodes('&', subTitle);
        if (VersionsUtils.is1_11())
            new ReflObject<>(player).callMethod("sendTitle", title, subTitle, start, duration, end);
        else {
            sendNMSTitle(player, title, "TITLE", start, duration, end);
            sendNMSTitle(player, subTitle, "SUBTITLE", start, duration, end);
        }
    }

    public static void sendNMSTitle(Player player, String text, String type, int start, int duration, int end) {
        ReflObject<?> chatTitle = NMSUtils.getChatSerializerObject(text);
        ReflObject<?> enumAction = new NMSReflObject<>("PacketPlayOutTitle.EnumTitleAction", false)
                .obtainField(type.toUpperCase());
        ReflObject<?> titlePacket = new NMSReflObject<>("PacketPlayOutTitle",
                enumAction.getObject(), chatTitle.getObject());
        ReflObject<?> lengthPacket = new NMSReflObject<>("PacketPlayOutTitle",
                start, duration, end);
        PacketsUtils.sendPacket(player, titlePacket);
        PacketsUtils.sendPacket(player, lengthPacket);
    }

}
