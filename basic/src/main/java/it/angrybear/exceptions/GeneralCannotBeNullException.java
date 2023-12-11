package it.angrybear.exceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * General cannot be null exception.
 */
public class GeneralCannotBeNullException extends BearRuntimeException {

    public GeneralCannotBeNullException(String objectName) {
        super(BearLoggingMessage.GENERAL_CANNOT_BE_NULL, "%object%", objectName);
    }
}
