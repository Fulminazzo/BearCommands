package it.angrybear.Interfaces;

import it.angrybear.Utils.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public interface ISubCommandable {

    default HashMap<String, String> getUsageSyntaxParsers() {
        HashMap<String, String> syntaxParsers = new HashMap<>();
        syntaxParsers.put("player", "&6player");
        syntaxParsers.put("subcommand", "&5subcommand");
        syntaxParsers.put("warp", "&awarp");
        syntaxParsers.put("world", "&2world");
        syntaxParsers.put("number", "&dnumber");
        return syntaxParsers;
    }

    default String getUsageSyntax(String usage) {
        if (usage == null) return "";
        StringBuilder finalUsage = new StringBuilder();
        LinkedList<String> tempUsages = new LinkedList<>();
        for (int i = 0; i < usage.length(); i++) {
            char c = usage.charAt(i);
            if (c == '>') {
                String tmp = tempUsages.removeLast();
                String finalTmpUsage = formatSyntax(getUsageSyntaxParsers().entrySet().stream()
                        .filter(e -> e.getKey().equalsIgnoreCase(tmp))
                        .map(Map.Entry::getValue)
                        .findAny().orElse("&7" + tmp));
                if (tempUsages.isEmpty()) finalUsage.append(finalTmpUsage);
                else tempUsages.addLast(tempUsages.removeLast() + finalTmpUsage);
            } else if (c == '<') tempUsages.addLast("");
            else if (tempUsages.isEmpty()) finalUsage.append(c);
            else tempUsages.addLast(tempUsages.removeLast() + c);
        }
        return StringUtils.parseMessage(finalUsage.toString());
    }

    default String formatSyntax(String s) {
        return String.format("&8<%s&8>", s);
    }

    String getName();
}
