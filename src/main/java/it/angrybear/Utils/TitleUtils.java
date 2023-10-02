package it.angrybear.Utils;

import it.angrybear.Bukkit.Objects.Reflections.NMSReflObject;
import it.angrybear.Bukkit.Utils.NMSUtils;
import it.angrybear.Bukkit.Utils.PacketsUtils;
import it.angrybear.Exceptions.ExpectedPlayerException;
import it.angrybear.Velocity.Utils.MessageUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class TitleUtils {

    /**
     * Sends a title to a player.
     * @param player: the player;
     * @param title: the title;
     */
    public static void sendTitle(Object player, String title) {
        sendGeneralTitle(player, title, null, 5, 20, 5);
    }

    /**
     * Sends a title to a player.
     * @param player: the player;
     * @param title: the title;
     * @param start: duration of starting fade;
     * @param duration: duration of the title;
     * @param end: duration of ending fade.
     */
    public static void sendTitle(Object player, String title, int start, int duration, int end) {
        sendGeneralTitle(player, title, null, start, duration, end);
    }

    /**
     * Sends a subtitle to a player.
     * @param player: the player;
     * @param subTitle: the subtitle;
     */
    public static void sendSubTitle(Object player, String subTitle) {
        sendGeneralTitle(player, null, subTitle, 5, 20, 5);
    }

    /**
     * Sends a subtitle to a player.
     * @param player: the player;
     * @param subTitle: the subtitle;
     * @param start: duration of starting fade;
     * @param duration: duration of the title;
     * @param end: duration of ending fade.
     */
    public static void sendSubTitle(Object player, String subTitle, int start, int duration, int end) {
        sendGeneralTitle(player, null, subTitle, start, duration, end);
    }

    /**
     * Sends a title and a subtitle to a player.
     * @param player: the player;
     * @param title: the title;
     * @param subTitle: the subtitle;
     */
    public static void sendGeneralTitle(Object player, String title, String subTitle) {
        sendGeneralTitle(player, title, subTitle, 5, 20, 5);
    }

    /**
     * Sends a title and a subtitle to a player.
     * @param player: the player;
     * @param title: the title;
     * @param subTitle: the subtitle;
     * @param start: duration of starting fade;
     * @param duration: duration of the title;
     * @param end: duration of ending fade.
     */
    public static void sendGeneralTitle(Object player, String title, String subTitle, int start, int duration, int end) {
        if (player == null) return;
        if (title == null) title = "";
        if (!ServerUtils.isVelocity()) title = ChatColor.translateAlternateColorCodes('&', title);
        if (subTitle == null) subTitle = "";
        if (!ServerUtils.isVelocity()) subTitle = ChatColor.translateAlternateColorCodes('&', subTitle);
        try {
            if (ServerUtils.isPlayer(player))
                if (ServerUtils.isBukkit())
                    if (VersionsUtils.is1_11())
                        new ReflObject<>(player).callMethod("sendTitle", title, subTitle, start, duration, end);
                    else {
                        sendNMSTitle(player, title, "TITLE", start, duration, end);
                        sendNMSTitle(player, subTitle, "SUBTITLE", start, duration, end);
                    }
                else if (ServerUtils.isVelocity()) sendVelocityTitle(player, title, subTitle, start, duration, end);
                else createBungeeCordTitle(title, subTitle, start, duration, end).callMethod("send", player);
            else throw new ExpectedPlayerException(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a title and a subtitle using Velocity API to a player.
     * @param player: the player;
     * @param title: the title;
     * @param subtitle: the subtitle;
     * @param start: duration of starting fade;
     * @param duration: duration of the title;
     * @param end: duration of ending fade.
     */
    private static void sendVelocityTitle(Object player, String title, String subtitle, long start, long duration, long end) throws Exception {
        if (!ServerUtils.isVelocity()) throw new Exception("Not on Velocity!");
        ReflObject<?> reflPlayer = new ReflObject<>(player);
        ReflObject<?> titlePart = new ReflObject<>("net.kyori.adventure.title.TitlePart", false);
        ReflObject<?> times = new ReflObject<>("net.kyori.adventure.title.Title.Times", false);
        ReflObject<?> ticks = new ReflObject<>("net.kyori.adventure.util.Ticks", false);
        if (title != null && !title.isEmpty()) {
            // player.sendTitlePart(TitlePart.TITLE, MessageUtils.messageToComponent(title));
            reflPlayer.callMethod("sendTitlePart", new Object[]{titlePart.getFieldObject("TITLE"), MessageUtils.messageToComponent(title)});
        }
        if (subtitle != null && !subtitle.isEmpty()) {
            // player.sendTitlePart(TitlePart.SUBTITLE, MessageUtils.messageToComponent(subtitle));
            reflPlayer.callMethod("sendTitlePart", new Object[]{titlePart.getFieldObject("SUBTITLE"), MessageUtils.messageToComponent(subtitle)});
        }
        // player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Ticks.duration(start), Ticks.duration(duration), Ticks.duration(end)));
        reflPlayer.callMethod("sendTitlePart", new Object[]{titlePart.getFieldObject("TIMES"), times.getMethodObject("times",
                new Object[]{ticks.getMethodObject("duration", start),
                        ticks.getMethodObject("duration", duration),
                        ticks.getMethodObject("duration", end)})});
    }

    /**
     * Creates a title and a subtitle using BungeeCord to a player.
     * @param titleMessage: the title;
     * @param subTitleMessage: the subtitle;
     * @param start: duration of starting fade;
     * @param duration: duration of the title;
     * @param end: duration of ending fade;
     * @return a ReflObject containing the Title and SubTitle.
     */
    private static ReflObject<?> createBungeeCordTitle(String titleMessage, String subTitleMessage, int start, int duration, int end) throws Exception {
        if (ServerUtils.isBukkit() || ServerUtils.isVelocity()) throw new Exception("Not on BungeeCord!");
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

    /**
     * Sends a Bukkit title using NMS to a player.
     * @param player: the player;
     * @param text: the message;
     * @param type: whether it is title or subtitle;
     * @param start: duration of starting fade;
     * @param duration: duration of the title;
     * @param end: duration of ending fade.
     */
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
