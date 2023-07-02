package it.angrybear.Objects;

import it.angrybear.Annotations.PreventSaving;
import it.angrybear.BearPlugin;
import it.angrybear.Enums.BearLoggingMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public abstract class BearPlayer extends Savable {
    @PreventSaving
    protected final File playersFolder;
    protected final UUID uuid;
    protected String name;

    private BearPlayer(BearPlugin<?, ?> plugin) {
        super(plugin, null);
        this.playersFolder = null;
        this.uuid = null;
        this.name = null;
    }

    public BearPlayer(BearPlugin<?, ?> plugin, File playerFile) throws Exception {
        super(plugin, playerFile);
        if (playerFile == null) throw new Exception(BearLoggingMessage.GENERAL_CANNOT_BE_NULL.getMessage("%object%", "PlayerFile"));
        this.playersFolder = playerFile.getParentFile();
        String fileName = playerFile.getName().split("\\.")[0];
        UUID uuid = UUID.fromString(fileName);
        createNew(null);
        reload();
        this.uuid = uuid;
    }

    public BearPlayer(BearPlugin<?, ?> plugin, File playersFolder, OfflinePlayer player) throws Exception {
        super(plugin, (playersFolder == null || player == null) ? null : new File(playersFolder, player.getUniqueId() + ".yml"));
        if (player == null) throw new Exception(BearLoggingMessage.GENERAL_CANNOT_BE_NULL.getMessage("%object%", "Player"));
        this.playersFolder = playersFolder;
        UUID uuid = player.getUniqueId();
        createNew(player);
        reload();
        this.uuid = uuid;
        this.name = player.getName();
        save("uuid", "name");
    }

    protected abstract void createNew(OfflinePlayer player);

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public boolean isOnline() {
        Player player = getPlayer();
        return player != null && player.isOnline();
    }

    public boolean isOffline() {
        return !isOnline();
    }

    public File getPlayerFile() {
        return file;
    }
}
