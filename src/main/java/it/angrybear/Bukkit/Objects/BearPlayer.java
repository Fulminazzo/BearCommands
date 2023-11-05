package it.angrybear.Bukkit.Objects;

import it.angrybear.Bukkit.BearPlugin;
import it.angrybear.Objects.ABearPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;

@SuppressWarnings("unchecked")
public abstract class BearPlayer<P extends BearPlugin<?, ?>> extends ABearPlayer<P> {

    public BearPlayer(P plugin) {
        super(plugin);
    }

    public BearPlayer(P plugin, File playerFile) throws Exception {
        super(plugin, playerFile);
    }

    public BearPlayer(P plugin, File playersFolder, OfflinePlayer player) throws Exception {
        super(plugin, playersFolder, player);
    }

    @Override
    public Player getPlayer() {
        return uuid == null ? null : Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer getOfflinePlayer() {
        return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public boolean isOnline() {
        Player player = getPlayer();
        return player != null && player.isOnline();
    }
}
