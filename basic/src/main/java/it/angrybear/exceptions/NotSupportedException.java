package it.angrybear.exceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * Not supported exception.
 */
public class NotSupportedException extends BearRuntimeException {

    public NotSupportedException() {
        super(BearLoggingMessage.NOT_SUPPORTED);
    }
}