package it.angrybear.Velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import it.angrybear.Velocity.Objects.VelocityBearPlayer;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "bearcommands",
        name = "BearCommands",
        version = "8.0",
        description = "A Minecraft library to help work with subcommands, plugins and NMS.",
        authors = {"Fulminazzo"}
)
public class VelocitySimpleBearCommandsPlugin extends VelocityBearCommandsPlugin<VelocityBearPlayer<?>> {

    @Inject
    public VelocitySimpleBearCommandsPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        super(proxyServer, logger, dataDirectory);
    }
}
