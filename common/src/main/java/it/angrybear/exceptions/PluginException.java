package it.angrybear.exceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * An exception that supports BearLoggingMessages.
 */
public class PluginException extends Exception {
    public PluginException() {
        super();
    }

    public PluginException(BearLoggingMessage loggingMessage, String... strings) {
        super(loggingMessage.getMessage(strings));
    }
}