package it.angrybear.Managers;

import it.angrybear.BearPlugin;
import it.angrybear.Objects.BearPlayer;
import it.angrybear.Utils.FileUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BearPlayerManager<P extends BearPlayer> {
    private final BearPlugin<?, ?> plugin;
    protected final File playersFolder;
    protected final List<P> players;
    protected final Class<P> customPlayerClass;
    protected boolean save;
    protected Consumer<P> quitAction;

    public BearPlayerManager(BearPlugin<?, ?> plugin, Class<P> customPlayerClass) {
        this.plugin = plugin;
        this.playersFolder = new File(plugin.getDataFolder(), "Players");
        this.customPlayerClass = customPlayerClass;
        this.players = new ArrayList<>();
        this.save = true;
    }

    public void reloadPlayers() {
        this.players.clear();
        Bukkit.getOnlinePlayers().forEach(this::addPlayer);
    }

    public void addPlayer(Player player) {
        this.players.add(new ReflObject<>(customPlayerClass,
                new Class[]{BearPlugin.class, File.class, OfflinePlayer.class},
                plugin, save ? playersFolder : null, player).getObject());
    }

    public boolean hasPlayer(Player player) {
        return getPlayer(player) != null;
    }

    public boolean hasPlayer(String name) {
        return getPlayer(name) != null;
    }

    public boolean hasPlayer(UUID uuid) {
        return getPlayer(uuid) != null;
    }

    public P getPlayer(Player player) {
        return getPlayer(player == null ? null : player.getUniqueId());
    }

    public P getPlayer(String name) {
        if (name == null) return null;
        return this.players.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public P getPlayer(UUID uuid) {
        if (uuid == null) return null;
        return this.players.stream().filter(p -> p.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public void removePlayer(Player player) {
        removePlayer(player == null ? null : player.getUniqueId());
    }

    public void removePlayer(String name) {
        this.players.remove(name == null ? null : getPlayer(name));
    }

    public void removePlayer(UUID uuid) {
        this.players.remove(uuid == null ? null : getPlayer(uuid));
    }

    public void removePlayer(P player) {
        if (player != null) {
            this.players.remove(player);
            if (quitAction != null) quitAction.accept(player);
        }
    }

    public List<P> getPlayers() {
        return players;
    }

    public void enableSave() {
        this.save = true;
    }

    public void disableSave() {
        this.save = false;
    }

    public boolean isSaving() {
        return this.save;
    }

    public void onQuit(Consumer<P> action) {
        this.quitAction = action;
    }

    public Consumer<P> getQuitAction() {
        return quitAction;
    }

    public void saveAll() throws IOException {
        if (save) {
            if (!playersFolder.isDirectory()) FileUtils.createFolder(playersFolder);
            for (P p : players) p.save();
        }
    }
}