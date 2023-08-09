package it.angrybear.Bukkit.Commands;

import it.angrybear.Bukkit.Interfaces.SubCommandable;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Utils.SubCommandsUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class BearCommandExecutor<Plugin extends JavaPlugin> implements TabExecutor, SubCommandable<Plugin> {
    protected final Plugin plugin;
    private final String name;
    private final String permission;
    private final List<BearSubCommand<Plugin>> subCommands;

    public BearCommandExecutor(Plugin plugin, String name, BearPermission permission) {
        this.plugin = plugin;
        this.name = name;
        this.permission = permission == null ? null : permission.getPermission();
        this.subCommands = new ArrayList<>();
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

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPermission() {
        return permission;
    }
}
