package it.angrybear.Objects;

import it.angrybear.Enums.BearLoggingMessage;

public class InvalidType {
    private final String entry;
    private final Class<?> expectedType;
    private final Class<?> receivedType;

    public InvalidType(String entry, Class<?> expectedType, Class<?> receivedType) {
        this.entry = entry;
        this.expectedType = expectedType;
        this.receivedType = receivedType;
    }

    public String getEntry() {
        return entry;
    }

    public Class<?> getExpectedType() {
        return expectedType;
    }

    public Class<?> getReceivedType() {
        return receivedType;
    }

    @Override
    public String toString() {
        return BearLoggingMessage.INVALID_TYPE.getMessage("%entry%", entry,
                "%expected%", expectedType.getSimpleName(), "%received%", receivedType.getSimpleName());
    }
}