package it.angrybear.exceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * An exception that supports BearLoggingMessages.
 */
public class BearException extends Exception {

    public BearException() {
        super();
    }

    public BearException(BearLoggingMessage loggingMessage, String... strings) {
        super(loggingMessage.getMessage(strings));
    }
}