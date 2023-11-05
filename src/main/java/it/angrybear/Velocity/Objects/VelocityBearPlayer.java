package it.angrybear.Velocity.Objects;

import com.velocitypowered.api.proxy.Player;
import it.angrybear.Objects.ABearPlayer;
import it.angrybear.Velocity.VelocityBearPlugin;

import java.io.File;

@SuppressWarnings("unchecked")
public abstract class VelocityBearPlayer<P extends VelocityBearPlugin<?>> extends ABearPlayer<P> {

    public VelocityBearPlayer(P plugin) {
        super(plugin);
    }

    public VelocityBearPlayer(P plugin, File playerFile) throws Exception {
        super(plugin, playerFile);
    }

    public VelocityBearPlayer(P plugin, File playersFolder, Player player) throws Exception {
        super(plugin, playersFolder, player);
    }

    @Override
    public Player getPlayer() {
        return uuid == null ? null : getPlugin().getProxyServer().getPlayer(uuid).orElse(null);
    }

    @Override
    public boolean isOnline() {
        return true;
    }
}
