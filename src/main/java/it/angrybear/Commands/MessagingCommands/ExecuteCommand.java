package it.angrybear.Commands.MessagingCommands;

import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Objects.UtilPlayer;
import it.angrybear.Objects.YamlElements.ArrayYamlObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.DataInputStream;

public class ExecuteCommand extends MessagingCommand {
    public ExecuteCommand() {
        super("executecommand");
    }

    @Override
    public void execute(UtilPlayer player, DataInputStream inputStream) {
        try {
            ProxiedPlayer proxiedPlayer = player.getPlayer();
            String command = inputStream.readUTF();
            if (command.startsWith("/")) command = command.substring(1);
            ProxyServer.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
