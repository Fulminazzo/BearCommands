package it.angrybear.Utils;

import it.angrybear.Enums.BearLoggingMessage;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
    private final static String[] formatCodes = new String[]{
            getCharCode("MAGIC"), getCharCode("BOLD"), getCharCode("STRIKETHROUGH"),
            getCharCode("UNDERLINE"), getCharCode("ITALIC"), getCharCode("RESET")
    };

    /**
     * Uses HexUtils.parseString() and ChatColor.translateAlternateColorCodes() to color a message.
     * @param message: the message.
     * @return the colored message.
     */
    public static String parseMessage(String message) {
        //TODO: Velocity?
        if (message == null) message = BearLoggingMessage.MESSAGE_ERROR.getMessage();
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (message.replace(" ", "").equalsIgnoreCase("")) return "";
        return HexUtils.parseString(message);
    }

    public static String stripMessage(String message) {
        //TODO: Velocity?
        return ChatColor.stripColor(parseMessage(message));
    }

    /**
     * Replaces the content of string from one string into another, while keeping
     * in count the previous color. This way, the replacement will not alter
     * the coloring of the string. For example:
     * replaceChatColors("&dHello %replace-this%, how are you?",
     *                          "%replace-this%", "&aWorld");
     * will return:
     * "&dHello &aWorld&d, how are you?"
     * @param string: the string to replace the content from;
     * @param from: the content to be replaced;
     * @param to: the replacement;
     * @return the replacement string.
     */
    public static String replaceChatColors(String string, String from, String to) {
        return replaceChatColors(string, from, to, VersionsUtils.is1_16());
    }

    /**
     * Replaces the content of string from one string into another, while keeping
     * in count the previous color. This way, the replacement will not alter
     * the coloring of the string. For example:
     * replaceChatColors("&dHello %replace-this%, how are you?",
     *                          "%replace-this%", "&aWorld", true);
     * will return:
     * "&dHello &aWorld&d, how are you?"
     * @param string: the string to replace the content from;
     * @param from: the content to be replaced;
     * @param to: the replacement;
     * @param checkHex: if false, ignores HEX codes.
     * @return the replacement string.
     */
    public static String replaceChatColors(String string, String from, String to, boolean checkHex) {
        if (string == null) return null;
        String[] strings = HexUtils.unParseHexColor(string).split(from
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("{", "\\{")
                .replace("}", "\\}"));
        StringBuilder result = new StringBuilder();
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            result.append(s);
            codes.addAll(getCleanChatCodesInString(s, checkHex));
            if (codes.isEmpty()) codes.add(String.valueOf(ChatColor.WHITE));
            if (i != strings.length - 1) result.append(to).append(String.join("", codes));
        }
        if (string.endsWith(from)) result.append(to);
        return result.toString();
    }

    /**
     * Returns a list of colors codes contained in a string "cleaned",
     * meaning that if the codes are ["&d", "&r", "&e"], everything before
     * "&r" will be removed.
     * @param string: the string;
     * @param hex: if true, also detect HEX codes.
     * @return a list containing all the cleaned codes (ordered).
     */
    public static List<String> getCleanChatCodesInString(String string, boolean hex) {
        List<String> result = new ArrayList<>();
        for (String s : getChatCodesInString(string, hex)) {
            if (s.equals(getCharCode("RESET"))) result.clear();
            else if (Arrays.stream(formatCodes).noneMatch(t -> t.equalsIgnoreCase(s.substring(1)))) result.clear();
            result.add(s);
        }
        return result;
    }

    /**
     * Returns all the color codes contained in a string.
     * @param string: the string;
     * @param hex: if true, also detect HEX codes.
     * @return a list containing all the codes (ordered).
     */
    private static List<String> getChatCodesInString(String string, boolean hex) {
        List<String> matches = new ArrayList<>();
        String regex = "([&|" + getColorCode() + "][A-Fa-f0-9" +
                Arrays.stream(formatCodes).map(Object::toString).collect(Collectors.joining()) + "])";
        if (hex) regex += "|" + HexUtils.hexRegex;
        Matcher matcher = Pattern.compile(regex).matcher(string);
        while (matcher.find()) {
            matches.removeIf(s -> s.equalsIgnoreCase(matcher.group()));
            matches.add(matcher.group());
        }
        return matches.stream().distinct().collect(Collectors.toList());
    }

    /**
     * @return the current Character used as color identifier.
     */
    public static String getColorCode() {
        return new ReflObject<>("net.md_5.bungee.api.ChatColor", false).getFieldObject("COLOR_CHAR").toString();
    }

    /**
     * Converts the name into its corresponding char color code.
     * @param name: the name to be converted.
     * @return a ReflObject containing the corresponding color code.
     */
    public static String getCharCode(String name) {
        return new ReflObject<>("net.md_5.bungee.api.ChatColor", false)
                .obtainField(name.toUpperCase()).toString().substring(1);
    }

    /**
     * Converts a string into a "YAML" format.
     * For example, if the string is "CamelCase", it will be
     * converted into "camel-case".
     * @param string: the string to convert.
     * @return the converted string.
     */
    public static String formatStringToYaml(String string) {
        StringBuilder result = new StringBuilder();
        for (String s : string.split("")) {
            if (s.equals(s.toUpperCase()) && !result.toString().isEmpty()) result.append("-");
            result.append(s.toLowerCase());
        }
        return result.toString();
    }

    /**
     * Repeats a string for a specified number of times.
     * @param c: the string to repeat;
     * @param times: the number of times.
     * @return the resulting string.
     */
    public static String repeatChar(String c, int times) {
        String s = "";
        for (int i = 0; i < times; i++) s += c;
        return s;
    }

    /**
     * Prints the object class and fields in a nice format.
     * @param object: the object.
     * @return the string containing the information.
     */
    public static String printObject(Object object) {
        if (object == null) return null;
        else {
            ReflObject<?> reflObject = new ReflObject<>(object);
            return String.format("%s {\n", object.getClass()) + reflObject.getFields().stream()
                    .map(f -> String.format("%s: %s", f.getName(), reflObject.getFieldObject(f.getName())))
                    .collect(Collectors.joining("\n")) + "\n}\n";
        }
    }

    /**
     * Replacement for WordUtils.capitalizeFully(string.replace("_", " ")):
     * converts a string by replacing its "_" characters with spaces and capitalizing
     * every first char of any word. For example: "this_string" will be formatted
     * as "This String".
     * @param string: the string to convert.
     * @return the converted string.
     */
    public static String capitalize(String string) {
        if (string == null) return null;
        return Arrays.stream(string.replace("_", " ").split(" "))
                .map(s -> {
                    if (!s.isEmpty())
                        return s.substring(0, 1).toUpperCase() + (
                                s.length() > 1 ? s.substring(1).toLowerCase() : "");
                    else return s;
                }).collect(Collectors.joining(" "));
    }
}