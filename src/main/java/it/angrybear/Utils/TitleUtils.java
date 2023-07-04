package it.angrybear.Utils;

import it.angrybear.Bukkit.Objects.Reflections.NMSReflObject;
import it.angrybear.Bukkit.Utils.NMSUtils;
import it.angrybear.Bukkit.Utils.PacketsUtils;
import it.angrybear.Exceptions.ExpectedPlayerException;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class TitleUtils {

    public static void sendTitle(Object player, String title) {
        sendGeneralTitle(player, title, null, 5, 20, 5);
    }

    public static void sendTitle(Object player, String title, int start, int duration, int end) {
        sendGeneralTitle(player, title, null, start, duration, end);
    }

    public static void sendSubTitle(Object player, String subTitle) {
        sendGeneralTitle(player, null, subTitle, 5, 20, 5);
    }

    public static void sendSubTitle(Object player, String subTitle, int start, int duration, int end) {
        sendGeneralTitle(player, null, subTitle, start, duration, end);
    }

    public static void sendGeneralTitle(Object player, String title, String subTitle) {
        sendGeneralTitle(player, title, subTitle, 5, 20, 5);
    }

    public static void sendGeneralTitle(Object player, String title, String subTitle, int start, int duration, int end) {
        if (player == null) return;
        if (title == null) title = "";
        title = ChatColor.translateAlternateColorCodes('&', title);
        if (subTitle == null) subTitle = "";
        subTitle = ChatColor.translateAlternateColorCodes('&', subTitle);
        try {
            if (ServerUtils.isBukkit()) {
                if (ServerUtils.isPlayer(player))
                    if (VersionsUtils.is1_11())
                        new ReflObject<>(player).callMethod("sendTitle", title, subTitle, start, duration, end);
                    else {
                        sendNMSTitle(player, title, "TITLE", start, duration, end);
                        sendNMSTitle(player, subTitle, "SUBTITLE", start, duration, end);
                    }
                else throw new ExpectedPlayerException(player);
            } else
                if (ServerUtils.isPlayer(player))
                    createBungeeCordTitle(title, subTitle, start, duration, end).callMethod("send", player);
                else throw new ExpectedPlayerException(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ReflObject<?> createBungeeCordTitle(String titleMessage, String subTitleMessage, int start, int duration, int end) throws Exception {
        if (ServerUtils.isBukkit()) throw new Exception("Not on BungeeCord!");
        //Title title = ProxyServer.getInstance().createTitle();
        ReflObject<?> proxyServer = ServerUtils.getProxyServerInstance();
        ReflObject<?> title = proxyServer.callMethod("createTitle");
        //title.title(new TextComponent(title));
        title.callMethod("title", (Object) TextComponent.fromLegacyText(titleMessage));
        //title.subTitle(new TextComponent(subTitle));
        title.callMethod("subTitle", (Object) TextComponent.fromLegacyText(subTitleMessage));
        //title.fadeIn(start);
        title.callMethod("fadeIn", start);
        //title.stay(duration);
        title.callMethod("stay", duration);
        //title.fadeOut(end);
        title.callMethod("fadeOut", end);
        return title;
    }

    private static void sendNMSTitle(Object player, String text, String type, int start, int duration, int end) {
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
