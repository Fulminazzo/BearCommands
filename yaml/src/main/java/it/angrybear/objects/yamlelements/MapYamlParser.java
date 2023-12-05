package it.angrybear.objects.yamlelements;

import it.angrybear.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.TriConsumer;
import it.angrybear.objects.configurations.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Map YAML parser.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 */
@SuppressWarnings("unchecked")
public final class MapYamlParser<K, V> extends YamlParser<Map<K, V>> {
    private final Function<String, K> keyLoader;
    private final Function<K, String> keyParser;

    public MapYamlParser() {
        this(s -> (K) s, Object::toString);
    }

    public MapYamlParser(Function<String, K> keyLoader, Function<K, String> keyParser) {
        super((Class<Map<K, V>>) ((Class<?>) Map.class));
        this.keyLoader = keyLoader;
        this.keyParser = keyParser;
    }

    /**
     * Gets loader.
     *
     * @return the loader
     */
    @Override
    protected BiFunction<IConfiguration, String, Map<K, V>> getLoader() {
        return (c, s) -> {
            ConfigurationSection section = c.getConfigurationSection(s);
            HashMap<K, V> map = new HashMap<>();
            section.getKeys().forEach(k -> map.put(keyLoader.apply(k), section.get(k)));
            return map;
        };
    }

    /**
     * Gets dumper.
     *
     * @return the dumper
     */
    @Override
    protected TriConsumer<IConfiguration, String, Map<K, V>> getDumper() {
        return (c, s, m) -> {
            ConfigurationSection section = c.createSection(s);
            m.forEach((k, v) -> section.set(keyParser.apply(k), v));
        };
    }
}
