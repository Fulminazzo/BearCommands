package it.angrybear.Bukkit.Listeners;

import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.PluginException;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.MessagingListener;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.Wrappers.PlayerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BukkitMessagingListener extends MessagingListener implements PluginMessageListener {
    public BukkitMessagingListener(IBearPlugin<?> plugin, MessagingChannel channel, MessagingCommand... commands) {
        super(plugin, channel, commands);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (this.channel.equals(channel)) {
            try {
                executeCommand(new PlayerWrapper(player), message);
            } catch (IOException | PluginException e) {
                IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                        "%task%", "parsing Plugin Message",
                        "%error%", e.getMessage());
            }
        }
    }
}
