package it.angrybear.objects.yamlelements;

import it.angrybear.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.TriConsumer;

import java.util.Date;
import java.util.function.BiFunction;

/**
 * Date YAML parser.
 */
public class DateYamlParser extends YamlParser<Date> {

    public DateYamlParser() {
        super(Date.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunction<IConfiguration, String, Date> getLoader() {
        return (c, s) -> new Date(c.getLong(s));
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, Date> getDumper() {
        return (c, s, d) -> c.set(s, d.getTime());
    }
}
