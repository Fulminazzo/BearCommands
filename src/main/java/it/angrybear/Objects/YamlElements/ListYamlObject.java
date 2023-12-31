package it.angrybear.Objects.YamlElements;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;

import java.util.ArrayList;
import java.util.List;

/*
    ONLY USE THIS IF YOU ARE SURE THAT OBJECTS WILL BE PRIMITIVES OR WRAPPERS!
 */
@SuppressWarnings("unchecked")
public class ListYamlObject<T> extends IterableYamlObject<List<T>, T> {

    public ListYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public ListYamlObject(Object object, YamlPair<?>... yamlPairs) {
        super((List<T>) object, yamlPairs);
    }

    @Override
    public List<T> load(Configuration configurationSection, String path) {
        List<?> configurationList = configurationSection.getList(path);
        if (configurationList == null) return null;
        List<T> list = new ArrayList<>();
        for (Object o : configurationList)
            try {
                list.add((T) o);
            } catch (ClassCastException e) {
                IBearPlugin.logWarning(BearLoggingMessage.INVALID_TYPE,
                        "%entry%", path, "%expected%", "unknown", "%received%",
                        o == null ? "null" : o.getClass().getSimpleName());
            }
        this.object = list;
        setVClass();
        return object;
    }

    @Override
    public void dump(Configuration configurationSection, String path) {
        configurationSection.set(path, null);
        if (object == null) return;
        configurationSection.set(path, object);
        setVClass();
    }

    private void setVClass() {
        this.vClass = (object.isEmpty() || object.get(0) == null) ? null : (Class<T>) object.get(0).getClass();
    }
}
