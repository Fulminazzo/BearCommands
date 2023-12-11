package it.angrybear.exceptions;

/**
 * Name cannot be null exception.
 */
public class NameCannotBeNullException extends GeneralCannotBeNullException {

    public NameCannotBeNullException(String variableName) {
        super("Name for " + variableName);
    }
}
