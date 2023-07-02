package it.angrybear.Objects.YamlElements;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class LocationYamlObject extends YamlObject<Location> {

    public LocationYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public LocationYamlObject(Location object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public Location load(ConfigurationSection configurationSection, String path) throws Exception {
        ConfigurationSection locationSection = configurationSection.getConfigurationSection(path);
        if (locationSection == null) return null;

        WorldYamlObject worldYamlObject = YamlObject.newObject(World.class, yamlPairs);
        object = new Location(worldYamlObject.load(locationSection, "world"),
                locationSection.getDouble("x"), locationSection.getDouble("y"), locationSection.getDouble("z"),
                (float) locationSection.getDouble("yaw"), (float) locationSection.getDouble("pitch"));
        return object;
    }

    @Override
    public void dump(ConfigurationSection fileConfiguration, String path) throws Exception {
        fileConfiguration.set(path, null);
        if (object == null || object.getWorld() == null) return;
        ConfigurationSection locationSection = fileConfiguration.createSection(path);
        ((WorldYamlObject) YamlObject.newObject(object.getWorld(), yamlPairs)).dump(locationSection, "world");
        locationSection.set("x", object.getX());
        locationSection.set("y", object.getY());
        locationSection.set("z", object.getZ());
        locationSection.set("yaw", (double) object.getYaw());
        locationSection.set("pitch", (double) object.getPitch());
    }
}
