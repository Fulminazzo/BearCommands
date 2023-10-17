package it.angrybear.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Utils.SerializeUtils;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class MapYamlObject<K, V> extends IterableYamlObject<Map<K, V>, V> {
    private final Function<String, K> getKey;
    private final Function<K, String> convertKey;

    public MapYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
        this.getKey = s -> (K) SerializeUtils.deserializeUUIDOrBase64(s);
        this.convertKey = SerializeUtils::serializeUUIDOrBase64;
    }

    public MapYamlObject(Function<String, K> getKey, Function<K, String> convertKey, YamlPair<?>... yamlPairs) {
        super(yamlPairs);
        this.getKey = getKey;
        this.convertKey = convertKey;
    }

    public MapYamlObject(Map<K, V> object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
        this.getKey = s -> (K) SerializeUtils.deserializeUUIDOrBase64(s);
        this.convertKey = SerializeUtils::serializeUUIDOrBase64;
    }

    public MapYamlObject(Map<K, V> object, Function<String, K> getKey, Function<K, String> convertKey, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
        this.getKey = getKey;
        this.convertKey = convertKey;
    }

    @Override
    public Map<K, V> load(Configuration configurationSection, String path) throws Exception {
        Configuration mapSection = configurationSection.getConfiguration(path);
        if (mapSection == null) return null;
        super.load(configurationSection, path);
        this.object = new HashMap<>();
        for (String key : mapSection.getKeys(false)) {
            if (key.equals("value-class")) continue;
            YamlObject<V> yamlObject = newObject(vClass, yamlPairs);
            V object = yamlObject.load(mapSection, key);
            this.object.put(getKey.apply(key), object);
        }
        return this.object;
    }

    @Override
    public void dump(Configuration fileConfiguration, String path) throws Exception {
        fileConfiguration.set(path, null);
        if (object == null) return;
        Configuration mapSection = fileConfiguration.createSection(path);
        if (object.isEmpty()) return;
        vClass = getGeneralClass();
        for (K key : object.keySet()) {
            YamlObject<V> yamlObject = newObject(object.get(key), yamlPairs);
            yamlObject.dump(mapSection, convertKey.apply(key));
        }
        super.dump(fileConfiguration, path);
    }

    private V getNonNullObject() {
        if (object == null) return null;
        return object.values().stream().filter(Objects::nonNull).findAny().orElse(null);
    }

    private Class<?> getGeneralClass() {
        if (this.object.isEmpty()) return null;
        V object = getNonNullObject();
        if (object == null) return null;
        List<Class<?>> classes = new ArrayList<>(Arrays.asList(ReflUtil.getClassAndSuperClasses(object.getClass())));
        this.object.values().stream()
                .filter(Objects::nonNull)
                .filter(o -> !o.equals(object))
                .map(o -> ReflUtil.getClassAndSuperClasses(o.getClass()))
                .forEach(c1 -> classes.removeIf(c2 -> Arrays.stream(c1).noneMatch(c3 -> c3.equals(c2))));
        return classes.isEmpty() ? object.getClass() : classes.get(0);
    }
}
