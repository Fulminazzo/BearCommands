package it.angrybear.exceptions;

import it.angrybear.enums.BearLoggingMessage;

public class NotSupportedException extends PluginRuntimeException {

    public NotSupportedException() {
        super(BearLoggingMessage.NOT_SUPPORTED);
    }
}