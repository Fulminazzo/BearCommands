package it.angrybear.Bungeecord.Commands;

import it.angrybear.Bungeecord.Interfaces.BungeeSubCommandable;
import it.angrybear.Commands.ABearSubCommand;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Utils.SubCommandsUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class BungeeBearSubCommand<P extends Plugin> extends ABearSubCommand implements BungeeSubCommandable<P> {
    private final P plugin;
    private final BungeeSubCommandable<P> command;
    private final List<BungeeBearSubCommand<P>> subCommands;

    public BungeeBearSubCommand(P plugin, BungeeSubCommandable<P> command, String name, BearPermission permission, String usage,
                                String description, String... aliases) {
        this(plugin, command, name, permission, usage, description, false, aliases);
    }

    public BungeeBearSubCommand(P plugin, BungeeSubCommandable<P> command, String name, BearPermission permission, String usage,
                                String description, boolean playerOnly, String... aliases) {
        super(command.getName(), name, permission, usage, description, playerOnly, aliases);
        this.plugin = plugin;
        this.command = command;
        this.subCommands = new ArrayList<>();
    }

    public <C extends BungeeSubCommandable<P>> C getCommand() {
        return (C) command;
    }

    public P getPlugin() {
        return plugin;
    }

    @Override
    public void addSubCommands(BungeeBearSubCommand<?>... subCommands) {
        removeSubCommands(subCommands);
        Arrays.stream(subCommands).forEach(s -> this.subCommands.add((BungeeBearSubCommand<P>) s));
    }

    @Override
    public void removeSubCommands(BungeeBearSubCommand<?>... subCommands) {
        if (subCommands == null) return;
        removeSubCommands(Arrays.stream(subCommands).filter(Objects::nonNull).map(BungeeBearSubCommand::getName).toArray(String[]::new));
    }

    @Override
    public void removeSubCommands(String... names) {
        Arrays.stream(names).forEach(name -> {
            if (name == null) return;
            this.subCommands.removeIf(s -> s.getName().equalsIgnoreCase(name));
        });
    }

    @Override
    public <SubCommand extends BungeeBearSubCommand<P>> List<SubCommand> getInternalSubCommands() {
        return this.subCommands.stream().map(s -> (SubCommand) s).collect(Collectors.toList());
    }

    @Override
    public <SubCommand extends BungeeBearSubCommand<P>> SubCommand getSubCommand(CommandSender sender, String arg) {
        return SubCommandsUtils.getSubCommand(getInternalSubCommands(), sender, arg);
    }

    @Override
    public <SubCommand extends BungeeBearSubCommand<P>> List<SubCommand> getExecutableSubCommands(CommandSender sender) {
        return SubCommandsUtils.getExecutableSubCommands(getInternalSubCommands(), sender).stream().map(s -> (SubCommand) s).collect(Collectors.toList());
    }

    public List<String> getExecutableSubCommandsStrings(CommandSender sender) {
        return SubCommandsUtils.getExecutableSubCommandsString(getInternalSubCommands(), sender);
    }

    public abstract void execute(CommandSender sender, Command cmd, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, Command cmd, String[] args);

    public abstract int getMinArguments();
}