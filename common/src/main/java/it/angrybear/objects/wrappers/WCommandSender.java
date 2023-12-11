package it.angrybear.objects.wrappers;

import it.angrybear.exceptions.NameCannotBeNullException;
import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.utils.ServerUtils;
import it.angrybear.utils.StringUtils;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import lombok.Getter;

//TODO: Full doc
public class WCommandSender {
    protected final IBearPlugin plugin;
    @Getter
    protected final String name;

    public WCommandSender(IBearPlugin plugin, String name) {
        if (name == null) throw new NameCannotBeNullException(getClass().getSimpleName());
        this.plugin = plugin;
        this.name = name;
    }

    public WCommandSender(IBearPlugin plugin, ReflObject<?> commandSender) {
        this(plugin, (String) commandSender.getMethodObject("getName"));
    }

    public <C> WCommandSender(IBearPlugin plugin, C commandSender) {
        this(plugin, new ReflObject<>(commandSender));
    }

    public boolean isPlayer() {
        return ServerUtils.isPlayer(getCommandSender());
    }

    public boolean hasPermission(String permission) {
        if (permission == null) return true;
        ReflObject<?> commandSender = getCommandSender();
        if (commandSender == null) return false;
        //TODO: Velocity?
        return commandSender.getMethodObject("hasPermission", permission);
    }

    public void sendMessage(String message) {
        if (message == null) return;
        ReflObject<?> commandSender = getCommandSender();
        if (commandSender == null) return;
        commandSender.callMethod("sendMessage", StringUtils.getUniversalMessage(message));
    }

    public <C> ReflObject<C> getCommandSender() {
        if (name == null) return null;
        return new ReflObject<>(name.equalsIgnoreCase("console") ?
                plugin.getRawConsole() : plugin.getRawPlayer(name));
    }

    public boolean equals(WCommandSender sender) {
        return sender != null && sender.name.equalsIgnoreCase(name);
    }
}
