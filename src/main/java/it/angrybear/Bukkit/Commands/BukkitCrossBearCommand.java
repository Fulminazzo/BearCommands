package it.angrybear.Bukkit.Commands;

import it.angrybear.Bukkit.BearPlugin;
import it.angrybear.Commands.CrossBearCommand;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Objects.Wrappers.CommandSenderWrapper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BukkitCrossBearCommand<Plugin extends BearPlugin<?, ?>> extends BearCommand<Plugin> {
    private final CrossBearCommand<Plugin> crossCommand;

    public BukkitCrossBearCommand(CrossBearCommand<Plugin> crossCommand, Plugin plugin, String name, BearPermission permission, String description, String usageMessage, String... aliases) {
        super(plugin, name, permission, description, usageMessage, aliases);
        this.crossCommand = crossCommand;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        crossCommand.execute(new CommandSenderWrapper(sender), args);
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return crossCommand.onTabComplete(new CommandSenderWrapper(sender), args);
    }
}