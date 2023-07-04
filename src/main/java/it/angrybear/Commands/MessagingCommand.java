package it.angrybear.Commands;

import it.angrybear.Objects.UtilPlayer;

import java.io.DataInputStream;

public abstract class MessagingCommand {
    private final String name;

    public MessagingCommand(String name) {
        this.name = name;
    }

    public abstract void execute(UtilPlayer player, DataInputStream inputStream);

    public String getName() {
        return name;
    }
}