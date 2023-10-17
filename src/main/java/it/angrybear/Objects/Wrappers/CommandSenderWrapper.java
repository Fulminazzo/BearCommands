package it.angrybear.Objects.Wrappers;

import it.angrybear.Utils.ServerUtils;
import it.angrybear.Velocity.Utils.MessageUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

/*
    Class created to unify command senders between Bukkit, BungeeCord and Velocity.
 */
@SuppressWarnings("unchecked")
public class CommandSenderWrapper {
    protected final ReflObject<?> commandSender;

    public CommandSenderWrapper(Object commandSender) {
        this.commandSender = new ReflObject<>(commandSender);
    }

    public void sendMessage(String message) {
        Object realMessage = ServerUtils.isVelocity() ? MessageUtils.messageToComponent(message) : message;
        commandSender.callMethod("sendMessage", realMessage);
    }

    public boolean isPlayer() {
        return ServerUtils.isPlayer(getCommandSender());
    }

    public boolean hasPermission(String permission) {
        if (permission == null) return true;
        return commandSender.getMethodObject("hasPermission", permission);
    }

    public String getName() {
        return commandSender.getMethodObject("getName");
    }

    public <C> C getCommandSender() {
        return (C) commandSender.getObject();
    }
}
