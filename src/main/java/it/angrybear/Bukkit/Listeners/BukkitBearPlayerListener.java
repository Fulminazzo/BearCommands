package it.angrybear.Bukkit.Listeners;

import it.angrybear.Bukkit.BearPlugin;
import it.angrybear.Bukkit.Managers.OfflineBearPlayersManager;
import it.angrybear.Bukkit.Objects.BearPlayer;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.BearPlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitBearPlayerListener<OnlinePlayer extends BearPlayer<?>, OfflinePlayer extends BearPlayer<?>>
        extends BearPlayerListener<OnlinePlayer> implements Listener {

    public BukkitBearPlayerListener(IBearPlugin<OnlinePlayer> plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        onPlayerJoin(player);
        OfflineBearPlayersManager<OfflinePlayer> offlinePlayerManager = ((BearPlugin<OnlinePlayer, OfflinePlayer>) plugin).getOfflinePlayersManager();
        if (offlinePlayerManager != null && !offlinePlayerManager.hasPlayer(player))
            offlinePlayerManager.addPlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        onPlayerQuit(event.getPlayer());
    }
}
