package it.angrybear.Bungeecord.Listeners;

import it.angrybear.Bungeecord.Objects.BungeeBearPlayer;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.BearPlayerListener;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeBearPlayerListener<OnlinePlayer extends BungeeBearPlayer<?>>
        extends BearPlayerListener<OnlinePlayer> implements Listener {

    public BungeeBearPlayerListener(IBearPlugin<OnlinePlayer> plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        onPlayerQuit(event.getPlayer());
    }
}
