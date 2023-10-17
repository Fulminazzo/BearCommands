package it.angrybear.Commands;

import it.angrybear.Bukkit.Commands.BukkitCrossBearCommand;
import it.angrybear.Bungeecord.Commands.BungeeCrossBearCommand;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Interfaces.ISubCommandable;
import it.angrybear.Objects.Wrappers.CommandSenderWrapper;
import it.angrybear.Utils.ServerUtils;
import it.angrybear.Velocity.Commands.VelocityBearCommand;
import it.angrybear.Velocity.Commands.VelocityCrossBearCommand;
import it.angrybear.Velocity.Utils.CommandUtils;
import it.angrybear.Velocity.VelocityBearPlugin;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.util.List;

public abstract class CrossBearCommand<Plugin extends IBearPlugin<?>> implements ISubCommandable {
    private final ReflObject<? extends ISubCommandable> realCommand;
    
    public CrossBearCommand(Plugin plugin, String name, String permission, String description, String usageMessage, String... aliases) {
        this(plugin, name, new BearPermission(permission) {
            @Override
            public String getPermission() {
                return permission;
            }
        }, description, usageMessage, aliases);
    }

    public CrossBearCommand(Plugin plugin, String name, BearPermission permission, String description, String usageMessage, String... aliases) {
        Class<? extends ISubCommandable> commandClass;
        if (ServerUtils.isBukkit()) commandClass = BukkitCrossBearCommand.class;
        else if (ServerUtils.isVelocity()) commandClass = VelocityCrossBearCommand.class;
        else commandClass = BungeeCrossBearCommand.class;
        this.realCommand = new ReflObject<>(commandClass, this, plugin, name, permission, description, usageMessage, aliases);
    }

    public abstract void execute(CommandSenderWrapper sender, String[] args);

    public abstract List<String> onTabComplete(CommandSenderWrapper sender, String[] args);

    public void load() {
        if (ServerUtils.isBukkit()) realCommand.callMethod("loadCommand");
        else if (ServerUtils.isVelocity()) CommandUtils.loadCommand((VelocityBearPlugin<?>) getPlugin(), (VelocityBearCommand<?>) realCommand.getObject());
        else ServerUtils.getPluginManager().callMethod("registerCommand", getPlugin(), realCommand.getObject());
    }

    public void unload() {
        if (ServerUtils.isBukkit()) realCommand.callMethod("unloadCommand");
        else if (ServerUtils.isVelocity()) CommandUtils.unloadCommand((VelocityBearPlugin<?>) getPlugin(), (VelocityBearCommand<?>) realCommand.getObject());
        else ServerUtils.getPluginManager().callMethod("unregisterCommand", realCommand.getObject());
    }

    public void addSubCommands(ABearSubCommand... subCommands) {
        realCommand.callMethod("addSubCommands", (Object) subCommands);
    }

    public void removeSubCommands(ABearSubCommand... subCommands) {
        realCommand.callMethod("removeSubCommands", (Object) subCommands);
    }

    public void removeSubCommands(String... names) {
        realCommand.callMethod("removeSubCommands", (Object) names);
    }

    public <SubCommand extends ABearSubCommand> List<SubCommand> getInternalSubCommands() {
        return realCommand.getMethodObject("getInternalSubCommands");
    }

    public <SubCommand extends ABearSubCommand> SubCommand getSubCommand(Object sender, String arg) {
        return getSubCommand(new CommandSenderWrapper(sender), arg);
    }

    public <SubCommand extends ABearSubCommand> SubCommand getSubCommand(CommandSenderWrapper sender, String arg) {
        return realCommand.getMethodObject("getSubCommand", sender.getCommandSender(), arg);
    }

    public <SubCommand extends ABearSubCommand> List<SubCommand> getExecutableSubCommands(Object sender) {
        return getExecutableSubCommands(new CommandSenderWrapper(sender));
    }

    public <SubCommand extends ABearSubCommand> List<SubCommand> getExecutableSubCommands(CommandSenderWrapper sender) {
        return realCommand.getMethodObject("getExecutableSubCommands", sender.getCommandSender());
    }

    public List<String> getExecutableSubCommandsStrings(Object sender) {
        return getExecutableSubCommandsStrings(new CommandSenderWrapper(sender));
    }

    public List<String> getExecutableSubCommandsStrings(CommandSenderWrapper sender) {
        return realCommand.getMethodObject("getExecutableSubCommandsStrings", sender.getCommandSender());
    }

    public Plugin getPlugin() {
        return realCommand.getMethodObject("getPlugin");
    }

    public String getName() {
        return realCommand.getMethodObject("getName");
    }

    public String getPermission() {
        return realCommand.getMethodObject("getPermission");
    }

    public String getDescription() {
        return realCommand.getMethodObject("getDescription");
    }

    public String getUsage() {
        return realCommand.getMethodObject("getUsage");
    }

    public String[] getAliases() {
        Object object = realCommand.getMethodObject("getAliases");
        String[] aliases;
        if (object instanceof List) aliases = ((List<?>) object).stream().map(Object::toString).toArray(String[]::new);
        else aliases = (String[]) object;
        return aliases;
    }
}
