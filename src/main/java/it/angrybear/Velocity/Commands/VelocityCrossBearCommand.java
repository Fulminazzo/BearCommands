package it.angrybear.Velocity.Commands;

import com.velocitypowered.api.command.CommandSource;
import it.angrybear.Commands.CrossBearCommand;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Objects.Wrappers.CommandSenderWrapper;
import it.angrybear.Velocity.VelocityBearPlugin;

import java.util.List;

public class VelocityCrossBearCommand<P extends VelocityBearPlugin<?>> extends VelocityBearCommand<P> {
    private final CrossBearCommand<P> crossCommand;

    public VelocityCrossBearCommand(CrossBearCommand<P> crossCommand, P plugin, String name, BearPermission permission, String description, String usageMessage, String... aliases) {
        super(plugin, name, permission, description, usageMessage, aliases);
        this.crossCommand = crossCommand;
    }

    @Override
    public void execute(CommandSource source, String label, String[] args) {
        crossCommand.execute(new CommandSenderWrapper(source), args);
    }

    @Override
    public List<String> onTabComplete(CommandSource source, String label, String[] args) {
        return crossCommand.onTabComplete(new CommandSenderWrapper(source), args);
    }
}
