package it.angrybear.Objects.YamlElements;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.YamlElementException;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.configuration.ConfigurationSection;

@SuppressWarnings("unchecked")
public class EnumYamlObject extends YamlObject<Enum<?>> {
    private final Class<Enum<?>> enumClass;

    public EnumYamlObject(Class<Enum<?>> enumClass, YamlPair<?>... yamlPairs) {
        super(yamlPairs);
        this.enumClass = enumClass;
    }

    public EnumYamlObject(Enum<?> object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
        this.enumClass = (Class<Enum<?>>) object.getClass();
    }

    @Override
    public Enum<?> load(ConfigurationSection configurationSection, String path) throws Exception {
        if (enumClass == null) throw new YamlElementException(BearLoggingMessage.GENERAL_CANNOT_BE_NULL,
                "%object%", "EnumClass");
        String enumName = configurationSection.getString(path);
        if (enumName == null) return null;
        this.object = new ReflObject<>(enumClass.getCanonicalName(), false).getFieldObject(enumName);
        return object;
    }

    @Override
    public void dump(ConfigurationSection configurationSection, String path) {
        configurationSection.set(path, object == null ? null : object.name());
    }
}
