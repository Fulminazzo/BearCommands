package it.angrybear.Objects.Configurations;

import it.angrybear.Bukkit.Utils.BukkitUtils;
import it.angrybear.Utils.ServerUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class Configuration {
    private final ReflObject<?> configuration;

    public Configuration(Object configuration) {
        if (configuration instanceof ReflObject<?>) configuration = ((ReflObject<?>) configuration).getObject();
        this.configuration = new ReflObject<>(configuration);
    }

    public Set<String> getKeys(boolean deep) {
        return configuration.getMethodObject("getKeys", deep);
    }

    public Map<String, Object> getValues(boolean deep) {
        return configuration.getMethodObject("getValues", deep);
    }

    public boolean contains(String path) {
        return configuration.getMethodObject("contains", path);
    }

    public boolean contains(String path, boolean ignoreDefault) {
        return configuration.getMethodObject("contains", path, ignoreDefault);
    }

    public boolean isSet(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isSet", path);
        else return contains(path);
    }

    
    public String getCurrentPath() {
        return configuration.getMethodObject("getCurrentPath");
    }

    public String getName() {
        return configuration.getMethodObject("getName");
    }

    
    public <O> O getRoot() {
        return configuration.getMethodObject("getRoot");
    }

    
    public <O> O getParent() {
        return configuration.getMethodObject("getParent");
    }

    
    public Object get(String path) {
        return configuration.getMethodObject("get", path);
    }

    
    public Object get(String path, Object def) {
        return configuration.getMethodObject("get", path, def);
    }

    public void set(String path, Object value) {
        configuration.callMethod("set", new Class<?>[]{String.class, Object.class}, path, value);
    }

    public <S> Configuration createSection(String path) {
        S section = configuration.getMethodObject("createSection", path);
        return new Configuration(section);
    }

    public <S> Configuration createSection(String path, Map<?, ?> map) {
        S section = configuration.getMethodObject("createSection", path, map);
        return new Configuration(section);
    }

    
    public String getString(String path) {
        return configuration.getMethodObject("getString", path);
    }

    
    public String getString(String path, String def) {
        return configuration.getMethodObject("getString", path, def);
    }

    public boolean isString(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isString", path);
        else return contains(path);
    }

    public int getInt(String path) {
        return configuration.getMethodObject("getInt", path);
    }

    public int getInt(String path, int def) {
        return configuration.getMethodObject("getInt", path, def);
    }

    public boolean isInt(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isInt", path);
        else return contains(path);
    }

    public boolean getBoolean(String path) {
        return configuration.getMethodObject("getBoolean", path);
    }

    public boolean getBoolean(String path, boolean def) {
        return configuration.getMethodObject("getBoolean", path, def);
    }

    public boolean isBoolean(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isBoolean", path);
        else return contains(path);
    }

    public double getDouble(String path) {
        return configuration.getMethodObject("getDouble", path);
    }

    public double getDouble(String path, double def) {
        return configuration.getMethodObject("getDouble", path, def);
    }

    public boolean isDouble(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isDouble", path);
        else return contains(path);
    }

    public long getLong(String path) {
        return configuration.getMethodObject("getLong", path);
    }

    public long getLong(String path, long def) {
        return configuration.getMethodObject("getLong", path, def);
    }

    public boolean isLong(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isLong", path);
        else return contains(path);
    }

    
    public List<?> getList(String path) {
        return configuration.getMethodObject("getList", path);
    }

    
    public List<?> getList(String path, List<?> def) {
        return configuration.getMethodObject("getList", path, def);
    }

    public boolean isList(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isList", path);
        else return contains(path);
    }

    public List<String> getStringList(String path) {
        return configuration.getMethodObject("getStringList", path);
    }

    public List<Integer> getIntegerList(String path) {
        return configuration.getMethodObject(ServerUtils.isBukkit() ? "getIntegerList" : "getIntList", path);
    }

    public List<Boolean> getBooleanList(String path) {
        return configuration.getMethodObject("getBooleanList", path);
    }

    public List<Double> getDoubleList(String path) {
        return configuration.getMethodObject("getDoubleList", path);
    }

    public List<Float> getFloatList(String path) {
        return configuration.getMethodObject("getFloatList", path);
    }

    public List<Long> getLongList(String path) {
        return configuration.getMethodObject("getLongList", path);
    }

    public List<Byte> getByteList(String path) {
        return configuration.getMethodObject("getByteList", path);
    }

    public List<Character> getCharacterList(String path) {
        return configuration.getMethodObject("getCharacterList", path);
    }

    public List<Short> getShortList(String path) {
        return configuration.getMethodObject("getShortList", path);
    }

    public List<Map<?, ?>> getMapList(String path) {
        return configuration.getMethodObject("getMapList", path);
    }

    
    public <T> T getObject(String path, Class<T> clazz) {
        return configuration.getMethodObject("getObject", path, clazz);
    }

    
    public <T> T getObject(String path, Class<T> clazz, T def) {
        return configuration.getMethodObject("getObject", path, clazz, def);
    }

    
    public <T> T getSerializable(String path, Class<T> clazz) {
        return configuration.getMethodObject("getSerializable", path, clazz);
    }

    
    public <T> T getSerializable(String path, Class<T> clazz, T def) {
        return configuration.getMethodObject("getSerializable", path, clazz, def);
    }

    
    public <V> V getVector(String path) {
        return configuration.getMethodObject("getVector", path);
    }

    
    public <V> V getVector(String path, V def) {
        return configuration.getMethodObject("getVector", path, def);
    }

    public boolean isVector(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isVector", path);
        else return contains(path);
    }

    
    public <O> O getOfflinePlayer(String path) {
        return configuration.getMethodObject("getOfflinePlayer", path);
    }

    
    public <O> O getOfflinePlayer(String path, O def) {
        return configuration.getMethodObject("getOfflinePlayer", path, def);
    }

    public boolean isOfflinePlayer(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isOfflinePlayer", path);
        else return contains(path);
    }

    
    public <I> I getItemStack(String path) {
        return configuration.getMethodObject("getItemStack", path);
    }

    
    public <I> I getItemStack(String path, I def) {
        return configuration.getMethodObject("getItemStack", path, def);
    }

    public boolean isItemStack(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isItemStack", path);
        else return contains(path);
    }

    
    public <C> C getColor(String path) {
        return configuration.getMethodObject("getColor", path);
    }

    
    public <C> C getColor(String path, C def) {
        return configuration.getMethodObject("getColor", path, def);
    }

    public boolean isColor(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isColor", path);
        else return contains(path);
    }

    
    public <L> L getLocation(String path) {
        return configuration.getMethodObject("getLocation", path);
    }

    
    public <L> L getLocation(String path, L def) {
        return configuration.getMethodObject("getLocation", path, def);
    }

    public boolean isLocation(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isLocation", path);
        else return contains(path);
    }

    public <C> C getConfigurationSection(String path) {
        return configuration.getMethodObject(ServerUtils.isBukkit() ? "getConfigurationSection" : "getSection", path);
    }

    public boolean isConfigurationSection(String path) {
        if (ServerUtils.isBukkit()) return configuration.getMethodObject("isConfigurationSection", path);
        else return contains(path);
    }
    
    public <C> C getDefaultSection() {
        return configuration.getMethodObject("getDefaultSection");
    }

    public void addDefault(String path, Object value) {
        configuration.callMethod("addDefault", path, value);
    }

    public Configuration getConfigSection(String path) {
        return new Configuration(getConfigurationSection(path));
    }

    @SuppressWarnings("unchecked")
    public <C> C getInnerConfigurationSection() {
        return (C) configuration.getObject();
    }
}
