package it.angrybear.utils;

import it.fulminazzo.reflectionutils.objects.ReflObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HexUtils {
    public static final String hexRegex = "(#[A-Fa-f0-9]{6})";

    /**
     * Converts all the hex codes in the given string to colors. (Only 1.16 and above)
     *
     * @param message the string
     * @return the colored string
     */
    public static String parseString(String message) {
        return parseString(message, true, false);
    }

    /**
     * Converts all the hex codes in the given string to colors. (Only 1.16 and above)
     *
     * @param message    the string
     * @param forceUnhex if this is set to true, hex codes will be removed without coloring
     * @return the colored string
     */
    public static String parseString(String message, boolean forceUnhex) {
        return parseString(message, true, forceUnhex);
    }

    /**
     * Converts all the hex codes in the given string to colors. (Only 1.16 and above)
     *
     * @param message             the string
     * @param removeIfNotParsable if not in 1.16, if this is set to true, hex codes will be hidden.
     * @param forceUnhex          if this is set to true, hex codes will be removed without coloring
     * @return the colored string
     */
    public static String parseString(String message, boolean removeIfNotParsable, boolean forceUnhex) {
        List<String> matches = extractHexCodes(message);
        ReflObject<?> ChatColor = StringUtils.getChatColor();
        for (String match : matches) {
            String chatColor = VersionsUtils.is1_16() && !forceUnhex ?
                    ChatColor.callMethod("of", match).toString() :  null;
            if (chatColor == null)
                if (removeIfNotParsable) chatColor = "";
                else continue;
            message = message.replace(match, chatColor);
        }
        return ChatColor.getMethodObject("translateAlternateColorCodes", '&', message).toString();
    }

    /**
     * Returns a list of hex codes extracted from the given string.
     *
     * @param message the string
     * @return the list of codes (empty if none found)
     */
    public static List<String> extractHexCodes(String message) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = Pattern.compile(hexRegex).matcher(message);
        while (matcher.find()) matches.add(matcher.group());
        return matches.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Reverse coloring of a string, converts all hex colors into their respective hex colors.
     *
     * @param message the string
     * @return the uncolored string
     */
    public static String unParseHexColor(String message) {
        String regex = "(§x(§[A-Fa-f0-9]){6})";
        String[] tmp = message.split(regex);
        String newMessage = "";
        for (String t : tmp) {
            newMessage += t;
            message = message.substring(t.length());
            if (message.isEmpty()) break;
            String color = "#" + message.substring(0, 14).replace("§", "").substring(1);
            message = message.substring(14);
            newMessage += color;
        }
        return newMessage;
    }
}