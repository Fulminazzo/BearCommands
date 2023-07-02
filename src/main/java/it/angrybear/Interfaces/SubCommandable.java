package it.angrybear.Interfaces;

import it.angrybear.Commands.BearSubCommand;
import it.angrybear.Utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface SubCommandable<Plugin extends JavaPlugin> {
    void addSubCommands(BearSubCommand<?>... subCommands);

    void removeSubCommands(BearSubCommand<?>... subCommands);

    void removeSubCommands(String... names);

    <SubCommand extends BearSubCommand<Plugin>> List<SubCommand> getInternalSubCommands();

    <SubCommand extends BearSubCommand<Plugin>> SubCommand getSubCommand(CommandSender sender, String arg);

    <SubCommand extends BearSubCommand<Plugin>> List<SubCommand> getExecutableSubCommands(CommandSender sender);

    List<String> getExecutableSubCommandsStrings(CommandSender sender);

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
}