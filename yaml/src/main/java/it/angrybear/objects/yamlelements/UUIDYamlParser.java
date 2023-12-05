package it.angrybear.objects.yamlelements;

import it.angrybear.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.TriConsumer;

import java.util.UUID;
import java.util.function.BiFunction;

/**
 * UUID YAML parser.
 */
public class UUIDYamlParser extends YamlParser<UUID> {

    public UUIDYamlParser() {
        super(UUID.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunction<IConfiguration, String, UUID> getLoader() {
        return (c, s) -> UUID.fromString(c.getString(s));
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, UUID> getDumper() {
        return (c, s, u) -> c.set(s, u.toString());
    }
}