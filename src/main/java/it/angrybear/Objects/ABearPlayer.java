package it.angrybear.Objects;

import it.angrybear.Annotations.PreventSaving;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.Wrappers.PlayerWrapper;

import java.io.File;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ABearPlayer<P extends IBearPlugin<?>> extends Savable<P> {
    @PreventSaving
    protected final File playersFolder;
    protected final UUID uuid;
    protected String name;
    @PreventSaving
    private PlayerQuestion playerQuestion;
    private Consumer<PlayerWrapper> cancelAction;

    protected ABearPlayer(P plugin) {
        super(plugin, null);
        this.playersFolder = null;
        this.uuid = null;
        this.name = null;
    }

    public ABearPlayer(P plugin, File playerFile) throws Exception {
        super(plugin, playerFile);
        if (playerFile == null) throw new Exception(BearLoggingMessage.GENERAL_CANNOT_BE_NULL.getMessage("%object%", "PlayerFile"));
        this.playersFolder = playerFile.getParentFile();
        String fileName = playerFile.getName().split("\\.")[0];
        UUID uuid = UUID.fromString(fileName);
        reload();
        this.uuid = uuid;
    }

    public <Pl> ABearPlayer(P plugin, File playersFolder, Pl player) throws Exception {
        super(plugin, (playersFolder == null || player == null) ? null : new File(playersFolder, new PlayerWrapper(player).getUniqueId() + ".yml"));
        if (player == null) throw new Exception(BearLoggingMessage.GENERAL_CANNOT_BE_NULL.getMessage("%object%", "Player"));
        this.playersFolder = playersFolder;
        PlayerWrapper playerWrapper = new PlayerWrapper(player);
        UUID uuid = playerWrapper.getUniqueId();
        createNew(playerWrapper);
        this.uuid = uuid;
        this.name = playerWrapper.getName();
        reload();
        save("uuid", "name");
    }

    protected abstract void createNew(PlayerWrapper player);

    public void askQuestion(BiConsumer<PlayerWrapper, String> action, Consumer<PlayerWrapper> cancelAction) {
        this.playerQuestion = new PlayerQuestion(action);
        this.cancelAction = cancelAction;
    }

    public void askQuestion(BiConsumer<PlayerWrapper, String> action, Consumer<PlayerWrapper> cancelAction, int seconds) {
        this.playerQuestion = new PlayerQuestion(action, seconds);
        this.cancelAction = cancelAction;
    }

    public boolean answerQuestion(String message) {
        if (playerQuestion != null) {
            try {
                if (message.equalsIgnoreCase("cancel") && cancelAction != null)
                    cancelAction.accept(new PlayerWrapper(getPlayer()));
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

    public abstract <Player> Player getPlayer();

    public abstract boolean isOnline();

    public boolean isOffline() {
        return !isOnline();
    }

    public File getPlayerFile() {
        return file;
    }
}
