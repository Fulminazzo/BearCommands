package it.angrybear.Velocity.Listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.PluginException;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.MessagingListener;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.Wrappers.PlayerWrapper;

import java.io.IOException;

public class VelocityMessagingListener extends MessagingListener {

    public VelocityMessagingListener(IBearPlugin<?> plugin, MessagingChannel channel, MessagingCommand... commands) {
        super(plugin, channel, commands);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (this.channel.equals(event.getIdentifier().getId())) {
            try {
                executeCommand(new PlayerWrapper(event.getTarget()), event.getData());
            } catch (IOException | PluginException e) {
                IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                        "%task%", "parsing Plugin Message",
                        "%error%", e.getMessage());
            }
        }
    }
}