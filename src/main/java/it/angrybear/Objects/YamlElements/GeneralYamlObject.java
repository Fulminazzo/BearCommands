package it.angrybear.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Utils.SerializeUtils;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.io.Serializable;

@SuppressWarnings("unchecked")
public class GeneralYamlObject extends YamlObject<Object> {

    public GeneralYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public GeneralYamlObject(Object object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    public <O> O getObject(Configuration fileConfiguration, String path) {
        return (O) load(fileConfiguration, path);
    }

    @Override
    public Object load(Configuration configurationSection, String path) {
        Object object = configurationSection.get(path);
        this.object = object;
        if (object instanceof String)
            try {
              object = SerializeUtils.deserializeFromBase64(object.toString());
            } catch (IllegalArgumentException ignored) {}
        if (object != null) this.object = object;
        return this.object;
    }

    @Override
    public void dump(Configuration fileConfiguration, String path) {
        fileConfiguration.set(path, null);
        if (object == null) return;
        Class<?> clazz = object.getClass();
        if (clazz.isArray()) clazz = clazz.getComponentType();
        if (ReflUtil.isPrimitiveOrWrapper(clazz)) fileConfiguration.set(path, object);
        else {
            String serialized = SerializeUtils.serializeToBase64(object);
            if (serialized == null) serialized = object.toString();
            if (!(object instanceof Serializable)) return;
            fileConfiguration.set(path, serialized);
        }
    }
}
