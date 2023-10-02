package it.angrybear.Bungeecord.Listeners;

import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.ExpectedPlayerException;
import it.angrybear.Exceptions.PluginException;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.MessagingListener;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.Wrappers.PlayerWrapper;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;

public class BungeeMessagingListener extends MessagingListener implements Listener {

    public BungeeMessagingListener(IBearPlugin<?> plugin, MessagingChannel channel, MessagingCommand... commands) {
        super(plugin, channel, commands);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (this.channel.equals(event.getTag())) {
            try {
                executeCommand(new PlayerWrapper(event.getReceiver()), event.getData());
            } catch (IOException | ExpectedPlayerException | PluginException e) {
                IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                        "%task%", "parsing Plugin Message",
                        "%error%", e.getMessage());
            }
        }
    }
}