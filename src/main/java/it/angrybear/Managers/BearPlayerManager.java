package it.angrybear.Managers;

import it.angrybear.Bukkit.BearPlugin;
import it.angrybear.Bungeecord.BungeeBearPlugin;
import it.angrybear.Exceptions.ExpectedPlayerException;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.ABearPlayer;
import it.angrybear.Objects.UtilPlayer;
import it.angrybear.Utils.FileUtils;
import it.angrybear.Utils.ServerUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class BearPlayerManager<P extends ABearPlayer> {
    private final IBearPlugin<?> plugin;
    protected final File playersFolder;
    protected final List<P> players;
    protected final Class<P> customPlayerClass;
    protected boolean save;
    protected Consumer<P> quitAction;

    public BearPlayerManager(IBearPlugin<?> plugin, Class<P> customPlayerClass) {
        this.plugin = plugin;
        this.playersFolder = new File(plugin.getDataFolder(), "Players");
        this.customPlayerClass = customPlayerClass;
        this.players = new ArrayList<>();
        this.save = true;
    }

    public <Player> void reloadPlayers(Collection<Player> players) {
        this.players.clear();
        players.forEach(this::addPlayer);
    }

    public <Player> void addPlayer(Player player) {
        this.players.add(new ReflObject<>(customPlayerClass,
                new Class[]{ServerUtils.isBukkit() ? BearPlugin.class : BungeeBearPlugin.class, File.class, ServerUtils.isBukkit() ?
                                ReflUtil.getClass("org.bukkit.OfflinePlayer") :
                                ReflUtil.getClass("net.md_5.bungee.api.connection.ProxiedPlayer")},
                plugin, save ? playersFolder : null, player).getObject());
    }

    public <Player> boolean hasPlayer(Player player) {
        return getPlayer(player) != null;
    }

    public boolean hasPlayer(String name) {
        return getPlayer(name) != null;
    }

    public boolean hasPlayer(UUID uuid) {
        return getPlayer(uuid) != null;
    }

    public <Player> P getPlayer(Player player) {
        try {
            return getPlayer(player == null ? null : new UtilPlayer(player).getUniqueId());
        } catch (ExpectedPlayerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public P getPlayer(String name) {
        if (name == null) return null;
        return this.players.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public P getPlayer(UUID uuid) {
        if (uuid == null) return null;
        return this.players.stream().filter(p -> p.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public <Player> void removePlayer(Player player) {
        try {
            removePlayer(player == null ? null : new UtilPlayer(player).getUniqueId());
        } catch (ExpectedPlayerException e) {
            e.printStackTrace();
        }
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