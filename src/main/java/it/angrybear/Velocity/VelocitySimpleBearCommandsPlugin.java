package it.angrybear.Velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import it.angrybear.Velocity.Objects.VelocityBearPlayer;
import org.slf4j.Logger;

import java.nio.file.Path;

public class VelocitySimpleBearCommandsPlugin extends VelocityBearCommandsPlugin<VelocityBearPlayer> {
    protected VelocitySimpleBearCommandsPlugin(ProxyServer proxyServer, Logger logger, Path dataDirectory) {
        super(proxyServer, logger, dataDirectory);
    }
}
