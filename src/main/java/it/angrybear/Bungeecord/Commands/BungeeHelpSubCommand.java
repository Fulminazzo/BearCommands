package it.angrybear.Bungeecord.Commands;

import it.angrybear.Bungeecord.Interfaces.BungeeSubCommandable;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Interfaces.IHelpSubCommand;
import it.angrybear.Objects.Wrappers.CommandSenderWrapper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BungeeHelpSubCommand<P extends Plugin> extends BungeeBearSubCommand<P> implements IHelpSubCommand {
    private final String noPermission;
    private final String subcommandNotFound;
    private final String helpMessage;
    private final String[] topMessages;
    private final String[] subMessages;

    public BungeeHelpSubCommand(P plugin, BungeeSubCommandable<P> command,
                                BearPermission permission, String description, String usage,
                                String noPermission, String subcommandNotFound, String helpMessage, String... aliases) {
        this(plugin, command, permission, description, usage, null, null, noPermission, subcommandNotFound, helpMessage, aliases);
    }

    public BungeeHelpSubCommand(P plugin, BungeeSubCommandable<P> command,
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
