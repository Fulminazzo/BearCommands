package it.angrybear.Exceptions;

import it.angrybear.Utils.ServerUtils;

public class ExpectedPlayerException extends ExpectedClassException {
    public ExpectedPlayerException(Object object) {
        super(object, ServerUtils.getPlayerClass().getSimpleName());
    }
}
