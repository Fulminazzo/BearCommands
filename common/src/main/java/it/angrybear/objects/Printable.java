package it.angrybear.objects;

import it.angrybear.utils.StringUtils;

/**
 * Represents an object that on toString() calls
 * prints itself and the fields it contains.
 */
public abstract class Printable {

    @Override
    public String toString() {
        return StringUtils.printObject(this);
    }
}
