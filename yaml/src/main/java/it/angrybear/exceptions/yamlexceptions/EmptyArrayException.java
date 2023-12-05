package it.angrybear.exceptions.yamlexceptions;

import it.angrybear.enums.BearLoggingMessage;

/**
 * Exception used in ArrayYAMLParser.
 * If the array is empty, because of how Java
 * works, the plugin will not be able to determine
 * the array type. Therefore, this exception will be
 * thrown.
 */
public class EmptyArrayException extends YamlException {
    public EmptyArrayException(String path, String name, Object object) {
        super(path, name, object, BearLoggingMessage.CANNOT_DECIPHER_EMPTY_ARRAY);
    }
}
