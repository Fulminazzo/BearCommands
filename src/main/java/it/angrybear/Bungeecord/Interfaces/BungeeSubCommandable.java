package it.angrybear.Bungeecord.Interfaces;

import it.angrybear.Bungeecord.Commands.BungeeBearSubCommand;
import it.angrybear.Interfaces.ISubCommandable;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

public interface BungeeSubCommandable<P extends Plugin> extends ISubCommandable {
    void addSubCommands(BungeeBearSubCommand<?>... subCommands);

    void removeSubCommands(BungeeBearSubCommand<?>... subCommands);

    void removeSubCommands(String... names);

    <SubCommand extends BungeeBearSubCommand<P>> List<SubCommand> getInternalSubCommands();

    <SubCommand extends BungeeBearSubCommand<P>> SubCommand getSubCommand(CommandSender sender, String arg);

    <SubCommand extends BungeeBearSubCommand<P>> List<SubCommand> getExecutableSubCommands(CommandSender sender);

    List<String> getExecutableSubCommandsStrings(CommandSender sender);

    P getPlugin();

    String getName();

    String getPermission();
}