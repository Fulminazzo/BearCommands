package it.angrybear.Objects.YamlElements;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class WorldYamlObject extends YamlObject<World> {

    public WorldYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public WorldYamlObject(World object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public World load(ConfigurationSection configurationSection, String path) {
        String worldName = configurationSection.getString(path);
        if (worldName == null) return null;
        return Bukkit.getWorlds().stream().filter(w -> w.getName().equalsIgnoreCase(worldName)).findAny().orElse(null);
    }

    @Override
    public void dump(ConfigurationSection fileConfiguration, String path) {
        fileConfiguration.set(path, object == null ? null : object.getName());
    }
}
