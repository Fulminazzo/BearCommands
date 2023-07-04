package it.angrybear.Bukkit.Managers;

import it.angrybear.Bukkit.BearPlugin;
import it.angrybear.Bukkit.Objects.BearPlayer;
import it.angrybear.Managers.BearPlayerManager;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.Arrays;

public class OfflineBearPlayerManager<P extends BearPlayer> extends BearPlayerManager<P> {

    public OfflineBearPlayerManager(BearPlugin<?, ?> plugin, Class<P> customPlayerClass) {
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

    public P getPlayer(OfflinePlayer player) {
        return getPlayer(player == null ? null : player.getUniqueId());
    }

    public void removePlayer(OfflinePlayer player) {
        removePlayer(player == null ? null : player.getUniqueId());
    }
}