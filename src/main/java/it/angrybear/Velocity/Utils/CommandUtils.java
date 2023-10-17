package it.angrybear.Velocity.Utils;

import it.angrybear.Velocity.Commands.VelocityBearCommand;
import it.angrybear.Velocity.VelocityBearPlugin;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

public class CommandUtils {

    public static void loadCommand(VelocityBearPlugin<?> plugin, VelocityBearCommand<?> command) {
        ReflObject<?> commandManager = getCommandManager(new ReflObject<>(plugin.getProxyServer()));
        ReflObject<?> metaBuilder = commandManager.callMethod("metaBuilder", command.getName())
                .callMethod("plugin", plugin)
                .callMethod("aliases", (Object) command.getAliases())
                .callMethod("build");
        commandManager.callMethod("register", metaBuilder.getObject(), command);
    }
    
    public static void unloadCommand(VelocityBearPlugin<?> plugin, VelocityBearCommand<?> command) {
        unloadCommand(plugin, command.getName());
    }
    
    public static void unloadCommand(VelocityBearPlugin<?> plugin, String commandName) {
        ReflObject<?> commandManager = getCommandManager(new ReflObject<>(plugin.getProxyServer()));
        commandManager.callMethod("unregister", commandName);
    }

    public static ReflObject<?> getCommandManager(ReflObject<?> proxyServer) {
        return proxyServer.callMethod("getCommandManager");
    }
}