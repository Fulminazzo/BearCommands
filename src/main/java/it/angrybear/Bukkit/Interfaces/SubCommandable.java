package it.angrybear.Bukkit.Interfaces;

import it.angrybear.Bukkit.Commands.BearSubCommand;
import it.angrybear.Interfaces.ISubCommandable;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public interface SubCommandable<Plugin extends JavaPlugin> extends ISubCommandable {
    void addSubCommands(BearSubCommand<?>... subCommands);

    void removeSubCommands(BearSubCommand<?>... subCommands);

    void removeSubCommands(String... names);

    <SubCommand extends BearSubCommand<Plugin>> List<SubCommand> getInternalSubCommands();

    <SubCommand extends BearSubCommand<Plugin>> SubCommand getSubCommand(CommandSender sender, String arg);

    <SubCommand extends BearSubCommand<Plugin>> List<SubCommand> getExecutableSubCommands(CommandSender sender);

    List<String> getExecutableSubCommandsStrings(CommandSender sender);
}