package it.angrybear.Bukkit.Commands;

import it.angrybear.Bukkit.Interfaces.SubCommandable;
import it.angrybear.Commands.ABearSubCommand;
import it.angrybear.Enums.BearPermission;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HelpSubCommand<Plugin extends JavaPlugin> extends BearSubCommand<Plugin> {
    private final String noPermission;
    private final String subcommandNotFound;
    private final String helpMessage;
    
    public HelpSubCommand(Plugin plugin, SubCommandable<Plugin> command,
                          BearPermission permission, String usage, String description,
                          String noPermission, String subcommandNotFound, String helpMessage, String... aliases) {
        super(plugin, command, "help", permission, usage, description, Stream.concat(Stream.of("?"), Arrays.stream(aliases)).toArray(String[]::new));
        this.noPermission = noPermission;
        this.subcommandNotFound = subcommandNotFound;
        this.helpMessage = helpMessage;
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        List<BearSubCommand<Plugin>> subCommands = getCommand().getExecutableSubCommands(sender);
        subCommands.removeIf(s -> Arrays.stream(args).anyMatch(a ->
                s.getName().equalsIgnoreCase(a) || Arrays.stream(s.getAliases()).anyMatch(a2 -> a2.equalsIgnoreCase(a))));
        if (subCommands.isEmpty()) {
            if (args.length == 0) sender.sendMessage(noPermission);
            else sender.sendMessage(subcommandNotFound.replace("%subcommand%", args[0]));
        } else subCommands.stream().sorted(Comparator.comparing(ABearSubCommand::getName)).forEach(s -> {
            SubCommandable<Plugin> command = s.getCommand();
            String commandName = command instanceof BearCommandExecutor ? "" : new ReflObject<>(s.getCommand()).getMethodObject("getName");
            sender.sendMessage(helpMessage
                    .replace("%name%", s.getName())
                    .replace("%alias%", Arrays.toString(s.getAliases()))
                    .replace("%permission%", s.getPermission())
                    .replace("%min-arguments%", String.valueOf(s.getMinArguments()))
                    .replace("%help%", s.getDescription())
                    .replace("%usage%", s.getUsage())
                    .replace("%command%", commandName)
                    .replace("%subcommands%", String.valueOf(s.getInternalSubCommands().size()))
            );
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length > 0) {
            List<BearSubCommand<Plugin>> givenSubCommands = new ArrayList<>();
            for (int i = 0; i < args.length - 1; i++) {
                BearSubCommand<Plugin> subCommand = getSubCommand(sender, args[0]);
                if (subCommand != null) givenSubCommands.add(subCommand);
            }
            list.addAll(getExecutableSubCommandsStrings(sender).stream()
                    .filter(s -> givenSubCommands.stream().anyMatch(c -> c.getName().equalsIgnoreCase(s) ||
                            Arrays.stream(c.getAliases()).anyMatch(a -> a.equalsIgnoreCase(s))))
                    .collect(Collectors.toList()));
        }
        return list;
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}
