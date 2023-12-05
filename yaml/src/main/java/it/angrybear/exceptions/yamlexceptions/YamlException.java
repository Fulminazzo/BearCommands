package it.angrybear.exceptions.yamlexceptions;

import it.angrybear.enums.BearLoggingMessage;
import lombok.Getter;

/**
 * A general exception that occurs many times
 * while working with IConfiguration instances.
 */
@Getter
public abstract class YamlException extends RuntimeException {
    private final String path;
    private final String name;
    private final Object object;

    public YamlException(String path, String name, Object object,
                         BearLoggingMessage message, String... strings) {
        this(path, name, object, message.getMessage(strings));
    }

    public YamlException(String path, String name, Object object, String message) {
        super(BearLoggingMessage.YAML_ERROR.getMessage(
                "%path%", path.isEmpty() ? "" : path + ".", "%name%", name,
                "%object%", object == null ? null : object.toString(), "%message%", message
        ));
        this.path = path;
        this.name = name;
        this.object = object;
    }

}
