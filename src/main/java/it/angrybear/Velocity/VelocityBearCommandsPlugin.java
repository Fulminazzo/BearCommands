package it.angrybear.Velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import it.angrybear.Commands.MessagingCommands.ExecuteCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Enums.BearMessagingChannel;
import it.angrybear.Velocity.Objects.VelocityBearPlayer;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Arrays;

public class VelocityBearCommandsPlugin<OnlinePlayer extends VelocityBearPlayer<?>> extends VelocityBearPlugin<OnlinePlayer> {
    private static VelocityBearCommandsPlugin<?> plugin;

    public VelocityBearCommandsPlugin(ProxyServer proxyServer, Logger logger, Path dataDirectory) {
        super(proxyServer, logger, dataDirectory);
        plugin = this;
    }

    @Override
    public void onEnable() {
        addMessagingListener(BearMessagingChannel.MESSAGING_CHANNEL, new ExecuteCommand(this));
        super.onEnable();
        if (!isEnabled()) return;

        Arrays.stream(BearLoggingMessage.ENABLING.getMessage(
                        "%plugin-name%", getName(), "%plugin-version%", getVersion())
                .split("\n")).forEach(VelocityBearPlugin::sendConsole);
    }

    @Override
    public String getName() {
        return "BearCommands";
    }

    @Override
    public String getVersion() {
        return "7.0";
    }

    public static VelocityBearPlugin<?> getPlugin() {
        return plugin == null ? new ReflObject<>(VelocityBearPlugin.class.getCanonicalName(), false).getFieldObject("instance") : plugin;
    }
}