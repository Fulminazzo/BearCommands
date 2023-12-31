package it.angrybear.Listeners;

import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Managers.BearPlayersManager;
import it.angrybear.Objects.ABearPlayer;

public class BearPlayerListener<OnlinePlayer extends ABearPlayer<?>> {
    protected final IBearPlugin<OnlinePlayer> plugin;

    public BearPlayerListener(IBearPlugin<OnlinePlayer> plugin) {
        this.plugin = plugin;
    }

    public <P> void onPlayerJoin(P player) {
        BearPlayersManager<OnlinePlayer> playerManager = plugin.getPlayersManager();
        if (playerManager != null && !playerManager.hasPlayer(player)) playerManager.addPlayer(player);
    }

    public <P> void onPlayerQuit(P player) {
        BearPlayersManager<OnlinePlayer> playerManager = plugin.getPlayersManager();
        if (playerManager != null && playerManager.hasPlayer(player)) playerManager.removePlayer(player);
    }
}
