package it.angrybear.Bungeecord.Commands;

import it.angrybear.Bungeecord.BungeeBearPlugin;
import it.angrybear.Commands.CrossBearCommand;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Objects.Wrappers.CommandSenderWrapper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeCrossBearCommand<P extends BungeeBearPlugin<?>> extends BungeeBearCommand<P> implements TabExecutor {
    private final CrossBearCommand<P> crossCommand;

    public BungeeCrossBearCommand(CrossBearCommand<P> crossCommand, P plugin, String name, BearPermission permission, String description, String usageMessage, String... aliases) {
        super(plugin, name, permission, description, usageMessage, aliases);
        this.crossCommand = crossCommand;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        crossCommand.execute(new CommandSenderWrapper(sender), args);
    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return crossCommand.onTabComplete(new CommandSenderWrapper(sender), args);
    }
}
