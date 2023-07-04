package it.angrybear.Bukkit.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlElements.YamlObject;
import it.angrybear.Objects.YamlPair;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldYamlObject extends YamlObject<World> {

    public WorldYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public WorldYamlObject(World object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public World load(Configuration configurationSection, String path) {
        String worldName = configurationSection.getString(path);
        if (worldName == null) return null;
        return Bukkit.getWorlds().stream().filter(w -> w.getName().equalsIgnoreCase(worldName)).findAny().orElse(null);
    }

    @Override
    public void dump(Configuration fileConfiguration, String path) {
        fileConfiguration.set(path, object == null ? null : object.getName());
    }
}
