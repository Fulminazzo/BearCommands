package it.angrybear.exceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * A runtime exception that supports BearLoggingMessages.
 */
public class PluginRuntimeException extends RuntimeException {
    public PluginRuntimeException() {
        super();
    }

    public PluginRuntimeException(BearLoggingMessage loggingMessage, String... strings) {
        super(loggingMessage.getMessage(strings));
    }
}