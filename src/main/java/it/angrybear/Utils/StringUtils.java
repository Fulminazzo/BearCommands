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

    public static String parseMessage(String message) {
        if (message == null) message = BearLoggingMessage.MESSAGE_ERROR.getMessage();
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (message.replace(" ", "").equalsIgnoreCase("")) return "";
        return HexUtils.parseString(message);
    }

    public static String replaceChatColors(String string, String from, String to) {
        return replaceChatColors(string, from, to, VersionsUtils.is1_16());
    }

    /*
        A method to replace in a string remembering the last colors modifications.
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

    public static List<String> getCleanChatCodesInString(String string, boolean hex) {
        List<String> result = new ArrayList<>();
        for (String s : getChatCodesInString(string, hex)) {
            if (s.equals(getCharCode("RESET"))) result.clear();
            else if (Arrays.stream(formatCodes).noneMatch(t -> t.equalsIgnoreCase(s.substring(1)))) result.clear();
            result.add(s);
        }
        return result;
    }

    public static List<String> getChatCodesInString(String string, boolean hex) {
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

    public static String getColorCode() {
        return new ReflObject<>("net.md_5.bungee.api.ChatColor", false).getFieldObject("COLOR_CHAR").toString();
    }

    /*
        Returns the char code from the ChatColor name.
     */
    public static String getCharCode(String name) {
        return new ReflObject<>("net.md_5.bungee.api.ChatColor", false)
                .obtainField(name.toUpperCase()).toString().substring(1);
    }

    public static String formatStringToYaml(String string) {
        StringBuilder result = new StringBuilder();
        for (String s : string.split("")) {
            if (s.equals(s.toUpperCase()) && !result.toString().equals("")) result.append("-");
            result.append(s.toLowerCase());
        }
        return result.toString();
    }

    public static String repeatChar(String c, int times) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < times; i++) s.append(c);
        return s.toString();
    }

    public static String printObject(Object object) {
        if (object == null) return null;
        else {
            ReflObject<?> reflObject = new ReflObject<>(object);
            return String.format("%s {\n", object.getClass()) + reflObject.getFields().stream()
                    .map(f -> String.format("%s: %s", f.getName(), reflObject.getFieldObject(f.getName())))
                    .collect(Collectors.joining("\n")) + "\n}\n";
        }
    }

    public static String capitalize(String string) {
        if (string == null) return null;
        return Arrays.stream(string.replace("_", " ").split(" "))
                .map(s -> {
                    if (s.length() > 0)
                        return s.substring(0, 1).toUpperCase() + (
                                s.length() > 1 ? s.substring(1).toLowerCase() : "");
                    else return s;
                }).collect(Collectors.joining(" "));
    }
}