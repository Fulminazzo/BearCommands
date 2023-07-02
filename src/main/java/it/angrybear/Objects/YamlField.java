package it.angrybear.Objects;

import it.angrybear.BearPlugin;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.YamlElementException;
import it.angrybear.Objects.YamlElements.YamlObject;
import it.angrybear.Utils.StringUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.util.Arrays;

public class YamlField {
    private final BearPlugin<?, ?> plugin;
    private final String fieldName;
    private final String path;
    private final Object object;

    public YamlField(BearPlugin<?, ?> plugin, Field field, Object containingObject) throws IllegalAccessException, YamlElementException {
        if (field == null) throw new YamlElementException(BearLoggingMessage.GENERAL_CANNOT_BE_NULL,
                "%object%", "Field");
        this.plugin = plugin;
        this.fieldName = field.getName();
        this.path = StringUtils.formatStringToYaml(field.getName());
        field.setAccessible(true);
        this.object = field.get(containingObject);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getPath() {
        return path;
    }

    public void setObject(ConfigurationSection configurationSection, Object containingObject) throws YamlElementException {
        if (configurationSection == null)
            throw new YamlElementException(BearLoggingMessage.GENERAL_CANNOT_BE_NULL, "%object%", "FileConfiguration");
        ReflObject<?> reflObject = new ReflObject<>(containingObject);
        Field field = reflObject.getField(fieldName);
        if (field == null) throw new YamlElementException(BearLoggingMessage.FIELD_NOT_FOUND,
                "%field%", fieldName, "%object%", containingObject.getClass().toString());
        try {
            field.setAccessible(true);
            Class<?> fieldClass = field.getType();
            YamlObject<?> yamlObject = YamlObject.newObject(fieldClass, plugin.getAdditionalYamlPairs());
            Object result = yamlObject.load(configurationSection, path);
            field.set(containingObject, result);
        } catch (Exception e) {
            throw new YamlElementException(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                    "%task%", String.format("getting field %s for %s", fieldName, containingObject.getClass().getName()),
                    "%error%", e.getMessage());
        }
    }

    public void save(ConfigurationSection configurationSection) throws YamlElementException {
        if (configurationSection == null) throw new YamlElementException(BearLoggingMessage.GENERAL_CANNOT_BE_NULL,
                "%object%", "ConfigurationSection");
        try {
            if (object == null) return;
            YamlObject<?> yamlObject = YamlObject.newObject(object, plugin.getAdditionalYamlPairs());
            yamlObject.dump(configurationSection, path);
        } catch (Exception e) {
            throw new YamlElementException(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                    "%task%", String.format("saving field %s in %s", fieldName, configurationSection),
                    "%error%", e.getMessage());
        }
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", path, object);
    }
}
