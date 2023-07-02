package it.angrybear.Objects.YamlElements;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.YamlElementException;
import it.angrybear.Utils.NMSUtils;
import org.bukkit.configuration.ConfigurationSection;

@SuppressWarnings("unchecked")
public abstract class IterableYamlObject<O, V> extends YamlObject<O> {
    protected Class<V> vClass;

    public IterableYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public IterableYamlObject(O object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public O load(ConfigurationSection configurationSection, String path) throws Exception {
        String classPath = configurationSection.getString(path + ".value-class");
        if (classPath == null) return null;
        vClass = (Class<V>) Class.forName(classPath);
        parseCraftBukkitClass();
        return null;
    }

    @Override
    public void dump(ConfigurationSection configurationSection, String path) throws Exception {
        if (vClass == null) throw new YamlElementException(BearLoggingMessage.GENERAL_CANNOT_BE_NULL,
                "%object%", "VClass");
        parseCraftBukkitClass();
        String classPath = path + ".value-class";
        try {
            configurationSection.set(classPath, vClass.getCanonicalName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseCraftBukkitClass() {
        try {vClass = (Class<V>) NMSUtils.convertCraftClassToSpigotClass(vClass);}
        catch (Exception ignored) {}
    }

    public Class<V> getvClass() {
        return vClass;
    }
}
