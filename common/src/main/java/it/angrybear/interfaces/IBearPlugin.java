package it.angrybear.interfaces;

import it.angrybear.enums.BearLoggingMessage;
import it.angrybear.objects.wrappers.WCommandSender;
import it.angrybear.objects.wrappers.WPlayer;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//TODO: FULL DOC
public interface IBearPlugin {

    /*
        LOADERS SECTION
     */
    default void loadAll() throws Exception {
        //TODO: Configurations
        //TODO: Dependencies
        //TODO: MessagingChannels
        //TODO: Listeners (from package?)
        //TODO: Players for every platform
        loadManagers();
    }

    //TODO: This is not good. Rework it
    default void loadManagers() throws Exception {

    }

    /*
        PLAYERS SECTION
     */

    default WPlayer getPlayer(UUID uuid) {
        return new WPlayer(this, uuid);
    }

    default WPlayer getPlayer(String name) {
        return new WPlayer(this, name);
    }

    default <P> WPlayer getPlayer(P player) {
        return new WPlayer(this, player);
    }

    default List<WPlayer> getPlayers() {
        return getRawPlayers().stream().map(this::getPlayer).collect(Collectors.toList());
    }

    <P> P getRawPlayer(UUID uuid);

    <P> P getRawPlayer(String name);

    <P> List<P> getRawPlayers();

    /*
        CONSOLE SECTION
     */
    default WCommandSender getConsole() {
        return new WCommandSender(this, (Object) getRawConsole());
    }

    <C> C getRawConsole();

    /*
        LOGGING SECTION
     */

    default void logInfo(BearLoggingMessage bearLoggingMessage, String... strings) {
        logInfo(bearLoggingMessage.getMessage(strings));
    }

    void logInfo(String message);

    default void logWarning(BearLoggingMessage bearLoggingMessage, String... strings) {
        logWarning(bearLoggingMessage.getMessage(strings));
    }

    void logWarning(String message);

    default void logError(BearLoggingMessage bearLoggingMessage, String... strings) {
        logError(bearLoggingMessage.getMessage(strings));
    }

    void logError(String message);

    /*
        GETTERS
     */

    File getDataFolder();
}
