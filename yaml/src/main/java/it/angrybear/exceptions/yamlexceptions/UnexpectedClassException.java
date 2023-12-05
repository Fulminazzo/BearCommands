package it.angrybear.exceptions.yamlexceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * This exception occurs when loading an object
 * from an IConfiguration, but the result does not
 * correspond to the expected type.
 */
public class UnexpectedClassException extends YamlException {

    public UnexpectedClassException(String path, String name, Object object, String expected) {
        super(path, name, object, BearLoggingMessage.UNEXPECTED_CLASS.getMessage(
                "%expected%", expected,
                "%received%", object == null ? "null" : object.getClass().getSimpleName()
        ));
    }
}
