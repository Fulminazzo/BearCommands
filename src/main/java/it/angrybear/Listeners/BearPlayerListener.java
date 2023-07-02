package it.angrybear.Listeners;

import it.angrybear.BearPlugin;
import it.angrybear.Managers.BearPlayerManager;
import it.angrybear.Managers.OfflineBearPlayerManager;
import it.angrybear.Objects.BearPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BearPlayerListener<OnlinePlayer extends BearPlayer, OfflinePlayer extends BearPlayer> implements Listener {
    private final BearPlugin<OnlinePlayer, OfflinePlayer> plugin;

    public BearPlayerListener(BearPlugin<OnlinePlayer, OfflinePlayer> plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BearPlayerManager<OnlinePlayer> playerManager = plugin.getPlayersManager();
        if (playerManager != null && !playerManager.hasPlayer(player))
            playerManager.addPlayer(player);
        OfflineBearPlayerManager<OfflinePlayer> offlinePlayerManager = plugin.getOfflinePlayersManager();
        if (offlinePlayerManager != null && !offlinePlayerManager.hasPlayer(player))
            offlinePlayerManager.addPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BearPlayerManager<OnlinePlayer> playerManager = plugin.getPlayersManager();
        if (playerManager != null && !playerManager.hasPlayer(player))
            playerManager.removePlayer(player);
    }
}
