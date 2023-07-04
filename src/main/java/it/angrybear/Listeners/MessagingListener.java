package it.angrybear.Listeners;

import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.PluginException;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.UtilPlayer;
import it.angrybear.Utils.MessagingUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MessagingListener {
    protected final IBearPlugin<?> plugin;
    protected final MessagingChannel channel;
    protected final List<MessagingCommand> commands;

    public MessagingListener(IBearPlugin<?> plugin, MessagingChannel channel, MessagingCommand... commands) {
        this.plugin = plugin;
        this.channel = channel;
        this.commands = Arrays.asList(commands);
    }

    public void executeCommand(UtilPlayer sender, byte[] message) throws IOException, PluginException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(message);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        String commandName;
        try {
            commandName = dataInputStream.readUTF();
        } catch (IOException e) {
            MessagingUtils.sendPluginMessage(plugin, sender, channel,
                    BearLoggingMessage.NO_COMMAND_PROVIDED.getMessage());
            throw new PluginException(BearLoggingMessage.NO_COMMAND_PROVIDED);
        }
        MessagingCommand command = commands.stream()
                .filter(c -> c.getName().equalsIgnoreCase(commandName))
                .findAny().orElse(null);
        try {
            if (command == null) {
                MessagingUtils.sendPluginMessage(plugin, sender, channel,
                        BearLoggingMessage.COMMAND_NOT_FOUND.getMessage("%command%", commandName));
                throw new PluginException(BearLoggingMessage.COMMAND_NOT_FOUND, "%command%", commandName);
            }
            else command.execute(sender, dataInputStream);
        } finally {
            dataInputStream.close();
        }
    }

    public MessagingChannel getChannel() {
        return channel;
    }
}