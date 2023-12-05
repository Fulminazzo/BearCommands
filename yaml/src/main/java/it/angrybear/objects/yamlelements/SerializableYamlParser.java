package it.angrybear.objects.yamlelements;

import it.angrybear.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.TriConsumer;
import it.angrybear.utils.SerializeUtils;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * Serializable YAML parser.
 */
public class SerializableYamlParser extends YamlParser<Serializable> {

    public SerializableYamlParser() {
        super(Serializable.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunction<IConfiguration, String, Serializable> getLoader() {
        return (c, s) -> SerializeUtils.deserializeFromBase64(c.getString(s));
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, Serializable> getDumper() {
        return (c, s, ser) -> c.set(s, SerializeUtils.serializeToBase64(ser));
    }
}
