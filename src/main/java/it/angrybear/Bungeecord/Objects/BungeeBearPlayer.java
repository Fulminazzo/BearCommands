package it.angrybear.Bungeecord.Objects;

import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.ABearPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;

@SuppressWarnings("unchecked")
public abstract class BungeeBearPlayer extends ABearPlayer {

    public BungeeBearPlayer(IBearPlugin<?> plugin, File playersFolder, ProxiedPlayer player) throws Exception {
        super(plugin, playersFolder, player);
    }

    @Override
    public ProxiedPlayer getPlayer() {
        return uuid == null ? null : ProxyServer.getInstance().getPlayer(uuid);
    }

    @Override
    public boolean isOnline() {
        return true;
    }
}
