package it.angrybear.Objects;

import it.angrybear.Utils.StringUtils;

public abstract class Printable {

    @Override
    public String toString() {
        return StringUtils.printObject(this);
    }
}
