package it.angrybear.Velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import it.angrybear.Commands.ABearSubCommand;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Utils.SubCommandsUtils;
import it.angrybear.Velocity.Interfaces.VelocitySubCommandable;
import it.angrybear.Velocity.VelocityBearPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class VelocityBearSubCommand<P extends VelocityBearPlugin<?>> extends ABearSubCommand implements VelocitySubCommandable<P> {
    private final P plugin;
    private final VelocitySubCommandable<P> command;
    private final List<VelocityBearSubCommand<P>> subCommands;

    public VelocityBearSubCommand(P plugin, VelocitySubCommandable<P> command, String name, BearPermission permission, String description,
                                String usage, String... aliases) {
        this(plugin, command, name, permission, description, usage, false, aliases);
    }

    public VelocityBearSubCommand(P plugin, VelocitySubCommandable<P> command, String name, BearPermission permission, String description,
                                String usage, boolean playerOnly, String... aliases) {
        super(command.getName(), name, permission, description, usage, playerOnly, aliases);
        this.plugin = plugin;
        this.command = command;
        this.subCommands = new ArrayList<>();
    }

    public <C extends VelocitySubCommandable<P>> C getCommand() {
        return (C) command;
    }

    public P getPlugin() {
        return plugin;
    }

    @Override
    public void addSubCommands(VelocityBearSubCommand<?>... subCommands) {
        removeSubCommands(subCommands);
        Arrays.stream(subCommands).forEach(s -> this.subCommands.add((VelocityBearSubCommand<P>) s));
    }

    @Override
    public void removeSubCommands(VelocityBearSubCommand<?>... subCommands) {
        if (subCommands == null) return;
        removeSubCommands(Arrays.stream(subCommands).filter(Objects::nonNull).map(VelocityBearSubCommand::getName).toArray(String[]::new));
    }

    @Override
    public void removeSubCommands(String... names) {
        Arrays.stream(names).forEach(name -> {
            if (name == null) return;
            this.subCommands.removeIf(s -> s.getName().equalsIgnoreCase(name));
        });
    }

    @Override
    public <SubCommand extends VelocityBearSubCommand<P>> List<SubCommand> getInternalSubCommands() {
        return this.subCommands.stream().map(s -> (SubCommand) s).collect(Collectors.toList());
    }

    @Override
    public <SubCommand extends VelocityBearSubCommand<P>> SubCommand getSubCommand(CommandSource sender, String arg) {
        return SubCommandsUtils.getSubCommand(getInternalSubCommands(), sender, arg);
    }

    @Override
    public <SubCommand extends VelocityBearSubCommand<P>> List<SubCommand> getExecutableSubCommands(CommandSource sender) {
        return SubCommandsUtils.getExecutableSubCommands(getInternalSubCommands(), sender).stream().map(s -> (SubCommand) s).collect(Collectors.toList());
    }

    @Override
    public List<String> getExecutableSubCommandsStrings(CommandSource sender) {
        return SubCommandsUtils.getExecutableSubCommandsString(getInternalSubCommands(), sender);
    }

    public abstract void execute(CommandSource sender, SimpleCommand cmd, String[] args);

    public abstract List<String> suggest(CommandSource sender, SimpleCommand cmd, String[] args);

    public abstract int getMinArguments();
}