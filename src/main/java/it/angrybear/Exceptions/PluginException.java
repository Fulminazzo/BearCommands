package it.angrybear.Exceptions;

import it.angrybear.Enums.BearLoggingMessage;

public class PluginException extends Exception {
    public PluginException() {
        super();
    }

    public PluginException(BearLoggingMessage loggingMessage, String... strings) {
        super(loggingMessage.getMessage(strings));
    }
}