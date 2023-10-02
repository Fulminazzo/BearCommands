package it.angrybear.Bukkit.Commands;

import it.angrybear.Bukkit.Interfaces.SubCommandable;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Interfaces.IHelpSubCommand;
import it.angrybear.Objects.Wrappers.CommandSenderWrapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class HelpSubCommand<Plugin extends JavaPlugin> extends BearSubCommand<Plugin> implements IHelpSubCommand {
    private final String noPermission;
    private final String subcommandNotFound;
    private final String helpMessage;
    private final String[] topMessages;
    private final String[] subMessages;

    public HelpSubCommand(Plugin plugin, SubCommandable<Plugin> command,
                          BearPermission permission, String description, String usage,
                          String noPermission, String subcommandNotFound, String helpMessage, String... aliases) {
        this(plugin, command, permission, description, usage, null, null, noPermission, subcommandNotFound, helpMessage, aliases);
    }

    public HelpSubCommand(Plugin plugin, SubCommandable<Plugin> command,
                          BearPermission permission, String description, String usage,
                          String[] topMessages, String[] subMessages,
                          String noPermission, String subcommandNotFound, String helpMessage, String... aliases) {
        super(plugin, command, "help", permission, description, usage, Stream.concat(Stream.of("?"), Arrays.stream(aliases)).toArray(String[]::new));
        this.noPermission = noPermission;
        this.subcommandNotFound = subcommandNotFound;
        this.helpMessage = helpMessage;
        this.topMessages = topMessages;
        this.subMessages = subMessages;
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args) {
        execute(new CommandSenderWrapper(sender), getCommand(), args, topMessages, subMessages, noPermission, subcommandNotFound, helpMessage);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String[] args) {
        return onTabComplete(new CommandSenderWrapper(sender), getCommand(), args);
    }

    @Override
    public int getMinArguments() {
        return 0;
    }
}
