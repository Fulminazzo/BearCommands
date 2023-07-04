package it.angrybear.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;

import java.util.UUID;

public class UUIDYamlObject extends YamlObject<UUID> {

    public UUIDYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public UUIDYamlObject(UUID object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public UUID load(Configuration configurationSection, String path) {
        if (configurationSection == null) return null;
        Object result = configurationSection.get(path);
        object = result == null ? null : UUID.fromString(result.toString());
        return object;
    }

    @Override
    public void dump(Configuration fileConfiguration, String path) {
        if (fileConfiguration == null) return;
        fileConfiguration.set(path, object == null ? null : object.toString());
    }
}
