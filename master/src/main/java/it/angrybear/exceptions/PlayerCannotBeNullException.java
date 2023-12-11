package it.angrybear.exceptions;

import it.angrybear.exceptions.GeneralCannotBeNullException;

public class PlayerCannotBeNullException extends GeneralCannotBeNullException {

    public PlayerCannotBeNullException() {
        super("Player");
    }
}
