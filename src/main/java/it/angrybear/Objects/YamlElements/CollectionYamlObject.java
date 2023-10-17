package it.angrybear.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Utils.NumberUtils;
import it.angrybear.Utils.ServerUtils;
import it.angrybear.Velocity.Objects.Configurations.VelocityConfiguration;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.util.*;

@SuppressWarnings("unchecked")
public class CollectionYamlObject<T> extends IterableYamlObject<Collection<T>, T> {

    public CollectionYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public CollectionYamlObject(Collection<T> object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public Collection<T> load(Configuration configurationSection, String path) throws Exception {
        Object obj = configurationSection.get(path);
        if (obj == null) return null;
        if (obj instanceof Map) obj = new VelocityConfiguration((Map<String, Object>) obj);
        IterableYamlObject<?, T> iterableYamlObject = null;
        Collection<T> result = null;
        if (obj instanceof List) {
            iterableYamlObject = new ListYamlObject<>(yamlPairs);
            result = (Collection<T>) iterableYamlObject.load(configurationSection, path);
        } else if (ServerUtils.isConfigurationSection(obj)) {
            Configuration collectionSection = new Configuration(obj);
            result = new ArrayList<>();
            List<String> keys = new ArrayList<>(collectionSection.getKeys(false));

            if (!keys.isEmpty() && NumberUtils.isNatural(keys.get(0))) {
                iterableYamlObject = getMapYamlObject(null);
                Map<Integer, T> map = (Map<Integer, T>) iterableYamlObject.load(configurationSection, path);

                for (Map.Entry<Integer, T> e : map.entrySet()) {
                    Integer n = e.getKey();
                    T t = e.getValue();
                    for (int i = 0; i <= n; i++) if (result.size() - 1 < i) result.add(null);
                    ((ArrayList<T>) result).set(n, t);
                }
            }
        }
        setVClass(iterableYamlObject);
        this.object = result;
        return this.object;
    }

    @Override
    public void dump(Configuration configurationSection, String path) throws Exception {
        configurationSection.set(path, null);
        if (object == null) return;
        if (object.isEmpty()) {
            configurationSection.set(path, new ArrayList<>());
            return;
        }
        Class<?> tmp = getGeneralClass();
        IterableYamlObject<?, T> iterableYamlObject;
        if (ReflUtil.isPrimitiveOrWrapper(tmp)) iterableYamlObject = new ListYamlObject<>(object, yamlPairs);
        else {
            HashMap<Integer, T> map = new HashMap<>();
            List<T> list = new ArrayList<>(object);
            for (int i = 0; i < list.size(); i++) map.put(i, list.get(i));
            iterableYamlObject = getMapYamlObject(map);
        }
        iterableYamlObject.dump(configurationSection, path);
        setVClass(iterableYamlObject);
    }

    private T getNonNullObject() {
        if (object == null) return null;
        return object.stream().filter(Objects::nonNull).findAny().orElse(null);
    }

    private Class<?> getGeneralClass() {
        if (this.object.isEmpty()) return null;
        Object object = getNonNullObject();
        if (object == null) return null;
        List<Class<?>> classes = new ArrayList<>(Arrays.asList(ReflUtil.getClassAndSuperClasses(object.getClass())));
        this.object.stream()
                .filter(Objects::nonNull)
                .filter(o -> !o.equals(object))
                .map(o -> ReflUtil.getClassAndSuperClasses(o.getClass()))
                .forEach(c1 -> classes.removeIf(c2 -> Arrays.stream(c1).noneMatch(c3 -> c3.equals(c2))));
        return classes.isEmpty() ? object.getClass() : classes.get(0);
    }

    private MapYamlObject<Integer, T> getMapYamlObject(Map<Integer, T> map) {
        return map == null ? new MapYamlObject<>(Integer::valueOf, String::valueOf, yamlPairs) :
                new MapYamlObject<>(map, Integer::valueOf, String::valueOf, yamlPairs);
    }

    private void setVClass(IterableYamlObject<?, T> iterableYamlObject) {
        if (iterableYamlObject != null) this.vClass = iterableYamlObject.getvClass();
    }
}
