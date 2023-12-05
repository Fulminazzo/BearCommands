package it.angrybear.objects.yamlelements;

import it.angrybear.interfaces.IConfiguration;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Set YAML parser.
 *
 * @param <T> the type of the set elements
 */
@SuppressWarnings("unchecked")
public class SetYamlParser<T> extends CollectionYamlParser<T, Set<T>> {

    public SetYamlParser() {
        super((Class<Set<T>>) (Class<?>) Set.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunction<IConfiguration, String, Set<T>> getLoader() {
        return (c, s) -> new HashSet<>(mapYamlParser.load(c, s).values());
    }
}
