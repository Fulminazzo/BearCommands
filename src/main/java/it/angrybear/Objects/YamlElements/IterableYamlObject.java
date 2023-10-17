package it.angrybear.Objects.YamlElements;

import it.angrybear.Bukkit.Utils.NMSUtils;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.YamlElementException;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Utils.ServerUtils;

public abstract class IterableYamlObject<O, V> extends YamlObject<O> {
    protected Class<?> vClass;

    public IterableYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public IterableYamlObject(O object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public O load(Configuration configurationSection, String path) throws Exception {
        String classPath = configurationSection.getString(path + ".value-class");
        if (classPath == null) return null;
        vClass = Class.forName(classPath);
        parseCraftBukkitClass();
        return null;
    }

    @Override
    public void dump(Configuration configurationSection, String path) throws Exception {
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
        if (!ServerUtils.isBukkit()) return;
        try {vClass = NMSUtils.convertCraftClassToSpigotClass(vClass);}
        catch (Exception ignored) {}
    }

    public Class<?> getvClass() {
        return vClass;
    }
}
