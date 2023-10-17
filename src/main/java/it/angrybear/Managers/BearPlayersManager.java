package it.angrybear.Managers;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.ExpectedPlayerException;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.ABearPlayer;
import it.angrybear.Objects.Wrappers.PlayerWrapper;
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

public abstract class BearPlayersManager<Player extends ABearPlayer<?>> {
    private final IBearPlugin<?> plugin;
    protected final File playersFolder;
    protected final List<Player> players;
    protected final Class<Player> customPlayerClass;
    protected boolean save;
    protected Consumer<Player> quitAction;

    public BearPlayersManager(IBearPlugin<?> plugin, Class<Player> customPlayerClass) {
        this.plugin = plugin;
        this.playersFolder = new File(plugin.getDataFolder(), "Players");
        this.customPlayerClass = customPlayerClass;
        this.players = new ArrayList<>();
        this.save = true;
    }

    public <P> void reloadPlayers(Collection<P> players) {
        this.players.clear();
        players.forEach(this::addPlayer);
    }

    public <P> void addPlayer(P player) {
        this.players.add(new ReflObject<>(customPlayerClass,
                new Class[]{IBearPlugin.class, File.class, ReflUtil.getClass(ServerUtils.isBukkit() ? "org.bukkit.OfflinePlayer" :
                                ServerUtils.isVelocity() ? "com.velocitypowered.api.proxy.Player" : "net.md_5.bungee.api.connection.ProxiedPlayer")},
                plugin, save ? playersFolder : null, player).getObject());
    }

    public <P> boolean hasPlayer(P player) {
        return getPlayer(player) != null;
    }

    public boolean hasPlayer(String name) {
        return getPlayer(name) != null;
    }

    public boolean hasPlayer(UUID uuid) {
        return getPlayer(uuid) != null;
    }

    public <P> Player getPlayer(P player) {
        try {
            return getPlayer(player == null ? null : new PlayerWrapper(player).getUniqueId());
        } catch (ExpectedPlayerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Player getPlayer(String name) {
        if (name == null) return null;
        return new ArrayList<>(this.players).stream().filter(p -> p.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public Player getPlayer(UUID uuid) {
        if (uuid == null) return null;
        return new ArrayList<>(this.players).stream().filter(p -> p.getUuid().equals(uuid)).findAny().orElse(null);
    }

    public <P> void removePlayer(P player) {
        try {
            removePlayer(player == null ? null : new PlayerWrapper(player).getUniqueId());
        } catch (ExpectedPlayerException e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(String name) {
        removePlayer(name == null ? null : getPlayer(name));
    }

    public void removePlayer(UUID uuid) {
        removePlayer(uuid == null ? null : getPlayer(uuid));
    }

    public void removePlayer(Player player) {
        if (player != null) {
            this.players.remove(player);
            if (quitAction != null) quitAction.accept(player);
            try {
                savePlayer(player);
            } catch (IOException e) {
                IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                        "%task%", "saving player " + player.getName(),
                        "%error%", e.getMessage());
            }
        }
    }

    private void savePlayer(Player player) throws IOException {
        if (save && playersFolder != null) {
            if (!playersFolder.isDirectory()) FileUtils.createFolder(playersFolder);
            player.save();
        }
    }

    public void saveAll() throws IOException {
        for (Player p : players) savePlayer(p);
    }

    public List<Player> getPlayers() {
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

    public void onQuit(Consumer<Player> action) {
        this.quitAction = action;
    }

    public Consumer<Player> getQuitAction() {
        return quitAction;
    }

    public void removeAll() {
        new ArrayList<>(this.players).forEach(this::removePlayer);
    }
}