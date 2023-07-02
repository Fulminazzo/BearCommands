package it.angrybear.Objects.YamlElements;

import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class UUIDYamlObject extends YamlObject<UUID> {

    public UUIDYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public UUIDYamlObject(UUID object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public UUID load(ConfigurationSection configurationSection, String path) {
        if (configurationSection == null) return null;
        Object result = configurationSection.get(path);
        object = result == null ? null : UUID.fromString(result.toString());
        return object;
    }

    @Override
    public void dump(ConfigurationSection fileConfiguration, String path) {
        if (fileConfiguration == null) return;
        fileConfiguration.set(path, object == null ? null : object.toString());
    }
}
