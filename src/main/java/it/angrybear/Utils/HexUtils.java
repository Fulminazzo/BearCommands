package it.angrybear.Utils;

import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HexUtils {
    public static final String hexRegex = "(#[A-Fa-f0-9]{6})";

    public static String parseString(String message) {
        return parseString(message, true, false);
    }

    public static String parseString(String message, boolean forceUnhex) {
        return parseString(message, true, forceUnhex);
    }

    public static String parseString(String message, boolean removeIfNotParsable, boolean forceUnhex) {
        List<String> matches = extractHexCodes(message);
        ReflObject<ChatColor> ChatColor = new ReflObject<>("net.md_5.bungee.api.ChatColor", false);
        for (String match : matches) {
            String chatColor = VersionsUtils.is1_16() && !forceUnhex ?
                    ChatColor.callMethod("of", match).toString() : null;
            if (chatColor == null)
                if (removeIfNotParsable) chatColor = "";
                else continue;
            message = message.replace(match, chatColor);
        }
        return ChatColor.getMethodObject("translateAlternateColorCodes", '&', message).toString();
    }

    public static List<String> extractHexCodes(String message) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = Pattern.compile(hexRegex).matcher(message);
        while (matcher.find()) matches.add(matcher.group());
        return matches.stream().distinct().collect(Collectors.toList());
    }

    public static String unParseHexColor(String message) {
        String regex = "(§x(§[A-Fa-f0-9]){6})";
        String[] tmp = message.split(regex);
        String newMessage = "";
        for (String t : tmp) {
            newMessage += t;
            message = message.substring(t.length());
            if (message.length() == 0) break;
            String color = "#" + message.substring(0, 14).replace("§", "").substring(1);
            message = message.substring(14);
            newMessage += color;
        }
        return newMessage;
    }
}