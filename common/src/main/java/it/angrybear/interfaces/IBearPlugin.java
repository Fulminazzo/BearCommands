package it.angrybear.interfaces;

import it.angrybear.enums.BearLoggingMessage;

import java.io.File;

//TODO: FULL DOC
public interface IBearPlugin {
    
    File getDataFolder();

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
}
