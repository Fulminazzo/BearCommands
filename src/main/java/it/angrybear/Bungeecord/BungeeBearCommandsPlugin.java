package it.angrybear.Bungeecord;

import it.angrybear.Bungeecord.Objects.BungeeBearPlayer;
import it.angrybear.Commands.MessagingCommands.ExecuteCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Enums.BearMessagingChannel;

import java.io.InputStream;
import java.util.Arrays;

public class BungeeBearCommandsPlugin<OnlinePlayer extends BungeeBearPlayer> extends BungeeBearPlugin<OnlinePlayer> {

    @Override
    public void onEnable() {
        addMessagingListener(BearMessagingChannel.MESSAGING_CHANNEL, new ExecuteCommand());
        super.onEnable();

        Arrays.stream(BearLoggingMessage.ENABLING.getMessage(
                        "%plugin-name%", getName(), "%plugin-version%", getDescription().getVersion())
                .split("\n")).forEach(BungeeBearPlugin::sendConsole);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Arrays.stream(BearLoggingMessage.DISABLING.getMessage(
                        "%plugin-name%", getName(), "%plugin-version%", getDescription().getVersion())
                .split("\n")).forEach(BungeeBearPlugin::sendConsole);
    }

    @Override
    public InputStream getResource(String path) {
        return this.getClass().getResourceAsStream(path);
    }
}