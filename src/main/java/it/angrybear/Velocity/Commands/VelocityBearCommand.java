package it.angrybear.Velocity.Commands;

import com.velocitypowered.api.command.SimpleCommand;
import it.angrybear.Velocity.Interfaces.VelocitySubCommandable;
import it.angrybear.Utils.SubCommandsUtils;
import com.velocitypowered.api.command.CommandSource;
import it.angrybear.Velocity.VelocityBearPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class VelocityBearCommand<P extends VelocityBearPlugin<?>> implements SimpleCommand, VelocitySubCommandable<P> {
    private final P plugin;
    private final String name;
    private final String description;
    private final String[] aliases;
    private final List<VelocityBearSubCommand<P>> subCommands;

    public VelocityBearCommand(P plugin, String name, String description, String[] aliases) {
        this.plugin = plugin;
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.subCommands = new ArrayList<>();
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

    public List<String> getExecutableSubCommandsStrings(CommandSource sender) {
        return SubCommandsUtils.getExecutableSubCommandsString(getInternalSubCommands(), sender);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public P getPlugin() {
        return plugin;
    }
}
