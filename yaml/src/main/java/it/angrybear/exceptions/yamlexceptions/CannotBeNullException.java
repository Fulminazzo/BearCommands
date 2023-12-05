package it.angrybear.exceptions.yamlexceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * Exception thrown when an object from
 * a IConfiguration is null and nullability
 * is not allowed.
 */
public class CannotBeNullException extends YamlException {

    public CannotBeNullException(String path, String name, String objectName) {
        super(path, name, null, BearLoggingMessage.GENERAL_CANNOT_BE_NULL, "%object%", objectName);
    }
}
