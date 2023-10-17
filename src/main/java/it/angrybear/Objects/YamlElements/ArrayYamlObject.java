package it.angrybear.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class ArrayYamlObject<T> extends IterableYamlObject<T[], T> {

    public ArrayYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public ArrayYamlObject(Object object, YamlPair<?>... yamlPairs) {
        super((T[]) object, yamlPairs);
    }

    @Override
    public T[] load(Configuration configurationSection, String path) throws Exception {
        CollectionYamlObject<T> collectionYamlObject = new CollectionYamlObject<>(yamlPairs);
        Collection<T> result = collectionYamlObject.load(configurationSection, path);
        setVClass(collectionYamlObject);
        if (result == null) return null;
        List<T> objects = new ArrayList<>(result);
        T[] array = (T[]) Array.newInstance(collectionYamlObject.getvClass(), objects.size());
        for (int i = 0; i < objects.size(); i++) array[i] = objects.get(i);
        return array;
    }

    @Override
    public void dump(Configuration configurationSection, String path) throws Exception {
        List<T> elements = Arrays.asList(object);
        CollectionYamlObject<T> collectionYamlObject = new CollectionYamlObject<>(elements, yamlPairs);
        collectionYamlObject.dump(configurationSection, path);
        setVClass(collectionYamlObject);
    }

    private void setVClass(IterableYamlObject<?, T> iterableYamlObject) {
        if (iterableYamlObject != null) this.vClass = iterableYamlObject.getvClass();
    }
}
