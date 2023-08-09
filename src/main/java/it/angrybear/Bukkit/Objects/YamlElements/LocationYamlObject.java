package it.angrybear.Bukkit.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlElements.YamlObject;
import it.angrybear.Objects.YamlPair;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationYamlObject extends YamlObject<Location> {

    public LocationYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public LocationYamlObject(Location object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public Location load(Configuration configurationSection, String path) {
        Configuration locationSection = configurationSection.getConfiguration(path);
        if (locationSection == null) return null;

        WorldYamlObject worldYamlObject = newObject(World.class, yamlPairs);
        object = new Location(worldYamlObject.load(locationSection, "world"),
                locationSection.getDouble("x"), locationSection.getDouble("y"), locationSection.getDouble("z"),
                (float) locationSection.getDouble("yaw"), (float) locationSection.getDouble("pitch"));
        return object;
    }

    @Override
    public void dump(Configuration fileConfiguration, String path) {
        fileConfiguration.set(path, null);
        if (object == null || object.getWorld() == null) return;
        Configuration locationSection = fileConfiguration.createSection(path);
        ((WorldYamlObject) YamlObject.newObject(object.getWorld(), yamlPairs)).dump(locationSection, "world");
        locationSection.set("x", object.getX());
        locationSection.set("y", object.getY());
        locationSection.set("z", object.getZ());
        locationSection.set("yaw", (double) object.getYaw());
        locationSection.set("pitch", (double) object.getPitch());
    }
}
