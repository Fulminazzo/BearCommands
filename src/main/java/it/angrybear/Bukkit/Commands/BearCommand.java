package it.angrybear.Bukkit.Commands;

import it.angrybear.Bukkit.Interfaces.SubCommandable;
import it.angrybear.Bukkit.Utils.CommandUtils;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Utils.SubCommandsUtils;
import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class BearCommand<Plugin extends JavaPlugin> extends Command implements SubCommandable<Plugin> {
    private final Plugin plugin;
    private final List<BearSubCommand<Plugin>> subCommands;
    private Object previousCommand;

    public BearCommand(Plugin plugin, String name, BearPermission permission, String description, String usageMessage, String... aliases) {
        super(name, description, usageMessage, Arrays.asList(aliases));
        setUsage(getUsageSyntax(usageMessage));
        setPermission(permission == null ? null : permission.getPermission());
        this.plugin = plugin;
        this.subCommands = new ArrayList<>();
    }

    public void loadCommand() {
        if (getPlugin() == null) return;
        unloadCommand();
        ReflObject<?> knownCommands = CommandUtils.getKnownCommands();
        if (VersionsUtils.is1_9()) {
            previousCommand = knownCommands.getMethodObject("get", getName());
            CommandUtils.getCommandMap().callMethod("register", plugin.getName().toLowerCase(), this);
        } else {
            previousCommand = new ArrayList<>((Collection<?>) knownCommands.getObject()).stream()
                    .map(c -> (Command) c)
                    .filter(c -> c.getName().equalsIgnoreCase(getName()))
                    .findFirst().orElse(null);
            CommandUtils.getCommandMap().callMethod("register", plugin.getName().toLowerCase(), this);
        }
        CommandUtils.generateHelpPage(getPlugin(), this);
        CommandUtils.syncCommands();
    }

    public void unloadCommand() {
        if (getPlugin() == null) return;
        Map<String, Command> knownCommands = CommandUtils.getKnownCommands().getObject();
        if (knownCommands == null) return;
        new ArrayList<>(knownCommands.keySet()).stream()
                .filter(s -> knownCommands.get(s) != null)
                .filter(s -> knownCommands.get(s).equals(this))
                .forEach(knownCommands::remove);
        CommandUtils.removeHelpPage(getPlugin(), this);
        if (previousCommand == null) previousCommand = Bukkit.getPluginCommand(getName());
        if (previousCommand != null && !previousCommand.equals(this)) knownCommands.put(getName(), (Command) previousCommand);
        CommandUtils.syncCommands();
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

    public String getFullName() {
        return getPlugin() == null ? null : (getPlugin().getName() + ":" + getName()).toLowerCase();
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
