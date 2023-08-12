package it.angrybear.Velocity.Objects;

import com.velocitypowered.api.proxy.Player;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.ABearPlayer;
import it.angrybear.Velocity.VelocityBearPlugin;

import java.io.File;

@SuppressWarnings("unchecked")
public abstract class VelocityBearPlayer extends ABearPlayer {

    public VelocityBearPlayer(IBearPlugin<?> plugin, File playersFolder, Player player) throws Exception {
        super(plugin, playersFolder, player);
    }

    @Override
    public Player getPlayer() {
        return uuid == null ? null : ((VelocityBearPlugin<?>) getPlugin()).getProxyServer().getPlayer(uuid).orElse(null);
    }

    @Override
    public boolean isOnline() {
        return true;
    }
}
