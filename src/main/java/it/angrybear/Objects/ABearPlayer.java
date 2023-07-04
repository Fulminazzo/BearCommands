package it.angrybear.Objects;

import it.angrybear.Annotations.PreventSaving;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Interfaces.IBearPlugin;

import java.io.File;
import java.util.UUID;

public abstract class ABearPlayer extends Savable {
    @PreventSaving
    protected final File playersFolder;
    protected final UUID uuid;
    protected String name;

    private ABearPlayer(IBearPlugin<?> plugin) {
        super(plugin, null);
        this.playersFolder = null;
        this.uuid = null;
        this.name = null;
    }

    public ABearPlayer(IBearPlugin<?> plugin, File playerFile) throws Exception {
        super(plugin, playerFile);
        if (playerFile == null) throw new Exception(BearLoggingMessage.GENERAL_CANNOT_BE_NULL.getMessage("%object%", "PlayerFile"));
        this.playersFolder = playerFile.getParentFile();
        String fileName = playerFile.getName().split("\\.")[0];
        UUID uuid = UUID.fromString(fileName);
        createNew(null);
        reload();
        this.uuid = uuid;
    }

    public <P> ABearPlayer(IBearPlugin<?> plugin, File playersFolder, P player) throws Exception {
        super(plugin, (playersFolder == null || player == null) ? null : new File(playersFolder, new UtilPlayer(player).getUniqueId() + ".yml"));
        if (player == null) throw new Exception(BearLoggingMessage.GENERAL_CANNOT_BE_NULL.getMessage("%object%", "Player"));
        UtilPlayer utilPlayer = new UtilPlayer(player);
        this.playersFolder = playersFolder;
        UUID uuid = utilPlayer.getUniqueId();
        createNew(utilPlayer);
        reload();
        this.uuid = uuid;
        this.name = utilPlayer.getName();
        save("uuid", "name");
    }

    protected abstract void createNew(UtilPlayer player);

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public abstract <P> P getPlayer();

    public abstract boolean isOnline();

    public boolean isOffline() {
        return !isOnline();
    }

    public File getPlayerFile() {
        return file;
    }
}
