package it.angrybear.Commands;

import it.angrybear.Enums.BearPermission;
import it.angrybear.Interfaces.SubCommandable;
import it.angrybear.Utils.SubCommandsUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class BearSubCommand<Plugin extends JavaPlugin> implements SubCommandable<Plugin> {
    private final Plugin plugin;
    private final String name;
    private final String permission;
    private final String usage;
    private final String description;
    private final boolean playerOnly;
    private final SubCommandable<Plugin> command;
    private final String[] aliases;
    private final List<BearSubCommand<Plugin>> subCommands;

    public BearSubCommand(Plugin plugin, SubCommandable<Plugin> command, String name, BearPermission permission, String usage,
                          String description, String... aliases) {
        this(plugin, command, name, permission, usage, description, false, aliases);
    }

    public BearSubCommand(Plugin plugin, SubCommandable<Plugin> command, String name, BearPermission permission, String usage,
                          String description, boolean playerOnly, String... aliases) {
        this.plugin = plugin;
        this.command = command;
        this.name = name.toLowerCase();
        this.permission = permission == null ? null : permission.getPermission();
        this.usage = getUsageSyntax(usage);
        this.description = ChatColor.translateAlternateColorCodes('&', description);
        this.playerOnly = playerOnly;
        this.aliases = Arrays.stream(aliases).map(String::toLowerCase).toArray(String[]::new);
        this.subCommands = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isPlayerOnly() {
        return playerOnly;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public <C extends SubCommandable<Plugin>> C getCommand() {
        return (C) command;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void addSubCommands(BearSubCommand<?>... subCommands) {
        removeSubCommands(subCommands);
        Arrays.stream(subCommands).forEach(s -> this.subCommands.add((BearSubCommand<Plugin>) s));
    }

    @Override
    public void removeSubCommands(BearSubCommand<?>... subCommands) {
        if (subCommands == null) return;
        removeSubCommands(Arrays.stream(subCommands).filter(Objects::nonNull).map(BearSubCommand::getName).toArray(String[]::new));
    }

    @Override
    public void removeSubCommands(String... names) {
        Arrays.stream(names).forEach(name -> {
            if (name == null) return;
            this.subCommands.removeIf(s -> s.getName().equalsIgnoreCase(name));
        });
    }

    @Override
    public <SubCommand extends BearSubCommand<Plugin>> List<SubCommand> getInternalSubCommands() {
        return this.subCommands.stream().map(s -> (SubCommand) s).collect(Collectors.toList());
    }

    @Override
    public <SubCommand extends BearSubCommand<Plugin>> SubCommand getSubCommand(CommandSender sender, String arg) {
        return SubCommandsUtils.getSubCommand(getInternalSubCommands(), sender, arg);
    }

    @Override
    public <SubCommand extends BearSubCommand<Plugin>> List<SubCommand> getExecutableSubCommands(CommandSender sender) {
        return SubCommandsUtils.getExecutableSubCommands(getInternalSubCommands(), sender).stream().map(s -> (SubCommand) s).collect(Collectors.toList());
    }

    @Override
    public List<String> getExecutableSubCommandsStrings(CommandSender sender) {
        return SubCommandsUtils.getExecutableSubCommandsString(getInternalSubCommands(), sender);
    }

    public abstract void execute(CommandSender sender, Command cmd, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, Command cmd, String[] args);

    public abstract int getMinArguments();
}