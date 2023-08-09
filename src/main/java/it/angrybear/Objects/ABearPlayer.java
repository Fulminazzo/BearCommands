package it.angrybear.Objects;

import it.angrybear.Annotations.PreventSaving;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Interfaces.IBearPlugin;

import java.io.File;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ABearPlayer extends Savable {
    @PreventSaving
    protected final File playersFolder;
    protected final UUID uuid;
    protected String name;
    @PreventSaving
    private PlayerQuestion playerQuestion;
    private Consumer<UtilPlayer> cancelAction;

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
        this.uuid = uuid;
        this.name = utilPlayer.getName();
        save("uuid", "name");
    }

    protected abstract void createNew(UtilPlayer player);

    public void askQuestion(BiConsumer<UtilPlayer, String> action, Consumer<UtilPlayer> cancelAction) {
        this.playerQuestion = new PlayerQuestion(action);
        this.cancelAction = cancelAction;
    }

    public void askQuestion(BiConsumer<UtilPlayer, String> action, Consumer<UtilPlayer> cancelAction, int seconds) {
        this.playerQuestion = new PlayerQuestion(action, seconds);
        this.cancelAction = cancelAction;
    }

    public boolean answerQuestion(String message) {
        if (playerQuestion != null) {
            try {
                if (message.equalsIgnoreCase("cancel") && cancelAction != null)
                    cancelAction.accept(new UtilPlayer(getPlayer()));
                else playerQuestion.accept(getPlayer(), message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            playerQuestion = null;
            cancelAction = null;
            return true;
        }
        return false;
    }

    public boolean isQuestionExpired() {
        return playerQuestion == null || playerQuestion.isExpired();
    }

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
