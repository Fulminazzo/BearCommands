package it.angrybear.Exceptions;

import it.angrybear.Enums.BearLoggingMessage;

public class ExpectedClassException extends Exception {

    public ExpectedClassException(Object object, String expected) {
        super(BearLoggingMessage.UNEXPECTED_CLASS.getMessage(
                "%expected%", expected,
                "%received%", object == null ? "null" : object.getClass().getSimpleName()
        ));
    }
}