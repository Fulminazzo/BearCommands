package it.angrybear.objects.yamlelements;

import it.angrybear.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.TriConsumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Collection YAML parser.
 *
 * @param <T> the type of the collection elements
 * @param <C> the type of the collection
 */
@SuppressWarnings("unchecked")
public class CollectionYamlParser<T, C extends Collection<T>> extends YamlParser<C> {
    protected final MapYamlParser<Integer, T> mapYamlParser;

    public CollectionYamlParser() {
        this((Class<C>) (Class<?>) Collection.class);
    }

    public CollectionYamlParser(Class<C> aClass) {
        super(aClass);
        this.mapYamlParser = new MapYamlParser<>(Integer::valueOf, Object::toString);
    }

    @Override
    protected BiFunction<IConfiguration, String, C> getLoader() {
        return (c, s) -> (C) mapYamlParser.load(c, s).values();
    }

    @Override
    protected TriConsumer<IConfiguration, String, C> getDumper() {
        return (c, s, o) -> {
            List<T> list = new ArrayList<>(o);
            HashMap<Integer, T> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) map.put(i, list.get(i));
            mapYamlParser.dump(c, s, map);
        };
    }
}