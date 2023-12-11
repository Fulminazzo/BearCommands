package it.angrybear.exceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * A runtime exception that supports BearLoggingMessages.
 */
public class BearRuntimeException extends RuntimeException {

    public BearRuntimeException() {
        super();
    }

    public BearRuntimeException(BearLoggingMessage loggingMessage, String... strings) {
        super(loggingMessage.getMessage(strings));
    }
}