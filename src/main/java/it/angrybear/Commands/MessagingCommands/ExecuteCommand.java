package it.angrybear.Commands.MessagingCommands;

import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.Wrappers.PlayerWrapper;
import it.angrybear.Utils.ServerUtils;
import it.angrybear.Velocity.VelocityBearPlugin;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.io.DataInputStream;

public class ExecuteCommand extends MessagingCommand {
    private final IBearPlugin<?> plugin;

    public ExecuteCommand(IBearPlugin<?> plugin) {
        super("executecommand");
        this.plugin = plugin;
    }

    @Override
    public void execute(PlayerWrapper player, DataInputStream inputStream) {
        try {
            String command = inputStream.readUTF();
            if (command.startsWith("/")) command = command.substring(1);
            if (ServerUtils.isVelocity()) {
                ((VelocityBearPlugin<?>) plugin).getProxyServer().getCommandManager().executeAsync(player.getPlayer(), command);
            } else ServerUtils.getPluginManager().callMethod("dispatchCommand",
                    new Class<?>[]{ReflUtil.getClass("net.md_5.bungee.api.CommandSender"), String.class},
                    player.getPlayer(), command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
