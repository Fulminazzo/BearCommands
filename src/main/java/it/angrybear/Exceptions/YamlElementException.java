package it.angrybear.Exceptions;

import it.angrybear.Enums.BearLoggingMessage;

public class YamlElementException extends Exception{

    public YamlElementException(BearLoggingMessage bearLoggingMessage, String... strings) {
        super(bearLoggingMessage.getMessage(strings));
    }
}
