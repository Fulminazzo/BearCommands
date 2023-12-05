package it.angrybear.objects.yamlelements;

import it.angrybear.interfaces.IConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * List YAML parser.
 *
 * @param <T> the type parameter
 */
@SuppressWarnings("unchecked")
public class ListYamlParser<T> extends CollectionYamlParser<T, List<T>> {

    public ListYamlParser() {
        super((Class<List<T>>) (Class<?>) List.class);
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunction<IConfiguration, String, List<T>> getLoader() {
        return (c, s) -> new ArrayList<>(mapYamlParser.load(c, s).values());
    }
}
