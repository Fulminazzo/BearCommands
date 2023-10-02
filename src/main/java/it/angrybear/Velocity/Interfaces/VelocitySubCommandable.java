package it.angrybear.Velocity.Interfaces;

import com.velocitypowered.api.command.CommandSource;
import it.angrybear.Interfaces.ISubCommandable;
import it.angrybear.Velocity.Commands.VelocityBearSubCommand;
import it.angrybear.Velocity.VelocityBearPlugin;

import java.util.List;

public interface VelocitySubCommandable<P extends VelocityBearPlugin<?>> extends ISubCommandable {
    void addSubCommands(VelocityBearSubCommand<?>... subCommands);

    void removeSubCommands(VelocityBearSubCommand<?>... subCommands);

    void removeSubCommands(String... names);

    <SubCommand extends VelocityBearSubCommand<P>> List<SubCommand> getInternalSubCommands();

    <SubCommand extends VelocityBearSubCommand<P>> SubCommand getSubCommand(CommandSource sender, String arg);

    <SubCommand extends VelocityBearSubCommand<P>> List<SubCommand> getExecutableSubCommands(CommandSource sender);

    List<String> getExecutableSubCommandsStrings(CommandSource sender);

    P getPlugin();

    String getName();

    String getPermission();
}