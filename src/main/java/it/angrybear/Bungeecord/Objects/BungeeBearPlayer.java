package it.angrybear.Bungeecord.Objects;

import it.angrybear.Bungeecord.BungeeBearPlugin;
import it.angrybear.Objects.ABearPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;

@SuppressWarnings("unchecked")
public abstract class BungeeBearPlayer<P extends BungeeBearPlugin<?>> extends ABearPlayer<P> {

    public BungeeBearPlayer(P plugin, File playersFolder, ProxiedPlayer player) throws Exception {
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
