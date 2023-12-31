package it.angrybear.Bukkit.Managers;

import it.angrybear.Bukkit.Objects.BearPlayer;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Managers.BearPlayersManager;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.Arrays;

public class OfflineBearPlayersManager<Player extends BearPlayer<?>> extends BearPlayersManager<Player> {

    public OfflineBearPlayersManager(IBearPlugin<?> plugin, Class<Player> customPlayerClass) {
        super(plugin, customPlayerClass);
    }

    public void reloadPlayers() {
        this.players.clear();
        Arrays.stream(Bukkit.getOfflinePlayers()).forEach(this::addPlayer);
    }

    public void addPlayer(OfflinePlayer player) {
        this.players.add(new ReflObject<>(customPlayerClass,
                new Class[]{File.class, OfflinePlayer.class},
                save ? playersFolder : null, player).getObject());
    }

    public boolean hasPlayer(OfflinePlayer player) {
        return getPlayer(player) != null;
    }

    public Player getPlayer(OfflinePlayer player) {
        return getPlayer(player == null ? null : player.getUniqueId());
    }

    public void removePlayer(OfflinePlayer player) {
        removePlayer(player == null ? null : player.getUniqueId());
    }
}