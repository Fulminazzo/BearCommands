package it.angrybear.exceptions;

/**
 * Class cannot be null exception.
 */
public class ClassCannotBeNullException extends GeneralCannotBeNullException {

    public ClassCannotBeNullException(String variableName) {
        super("Class for " + variableName);
    }
}
