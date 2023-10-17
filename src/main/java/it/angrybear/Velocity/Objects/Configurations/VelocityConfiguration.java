package it.angrybear.Velocity.Objects.Configurations;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.PluginException;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings("unchecked")
public class VelocityConfiguration implements Serializable {
    private final Map<String, Object> configuration;

    public VelocityConfiguration(Map<String, Object> configuration) throws PluginException {
        this.configuration = configuration;
        if (configuration == null) throw new PluginException(BearLoggingMessage.GENERAL_CANNOT_BE_NULL, "%object%", "Configuration");
    }

    @SuppressWarnings("unused")
    public Set<String> getKeys(boolean deep) {
        return getKeys();
    }

    public Set<String> getKeys() {
        return configuration.keySet();
    }

    public Map<String, Object> getValues() {
        return configuration;
    }

    public boolean contains(String path) {
        String[] tmp = path.split("\\.");
        Map<String, Object> configuration = this.configuration;
        for (int i = 0; i < tmp.length - 1; i++)
            try {configuration = (Map<String, Object>) configuration.get(tmp[i]);}
            catch (Exception e) {return false;}
        return configuration.containsKey(tmp[tmp.length - 1]);
    }

    public boolean contains(String path, boolean ignoreDefault) {
        return contains(path);
    }

    public Object get(String path) {
        if (path.contains(".")) {
            String[] tmp = path.split("\\.");
            VelocityConfiguration configuration = getSection(tmp[0]);
            return configuration == null ? null :
                    configuration.get(String.join(".", Arrays.copyOfRange(tmp, 1, tmp.length)));
        } else return configuration.get(path);
    }

    public Object get(String path, Object def) {
        Object object = get(path);
        if (object == null) object = def;
        return object;
    }

    public void set(String path, Object value) {
        if (path.contains(".")) {
            String[] tmp = path.split("\\.");
            VelocityConfiguration configuration = getSection(tmp[0]);
            if (configuration == null) configuration = createSection(tmp[0]);
            configuration.set(String.join(".", Arrays.copyOfRange(tmp, 1, tmp.length)), value);
        } else configuration.put(path, value);
    }

    public VelocityConfiguration createSection(String path) {
        return createSection(path, new HashMap<>());
    }

    public VelocityConfiguration createSection(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            String[] tmp = path.split("\\.");
            VelocityConfiguration configuration = getSection(tmp[0]);
            if (configuration == null) configuration = createSection(tmp[0]);
            return configuration.createSection(String.join(".", Arrays.copyOfRange(tmp, 1, tmp.length)), map);
        } else {
            configuration.put(path, map);
            return getSection(path);
        }
    }


    public String getString(String path) {
        return getString(path, null);
    }


    public String getString(String path, String def) {
        return getObject(path, def);
    }

    public int getInt(String path) {
        return getInt(path, 0);
    }

    public int getInt(String path, int def) {
        return getObject(path, def);
    }

    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    public boolean getBoolean(String path, boolean def) {
        return getObject(path, def);
    }

    public double getDouble(String path) {
        return getDouble(path, 0.0);
    }

    public double getDouble(String path, double def) {
        return getObject(path, def);
    }

    public long getLong(String path) {
        return getLong(path, 0L);
    }

    public long getLong(String path, long def) {
        return getObject(path, def);
    }


    public List<?> getList(String path) {
        return getList(path, null);
    }


    public List<?> getList(String path, List<?> def) {
        return getObject(path, def);
    }

    public List<String> getStringList(String path) {
        return (List<String>) getList(path);
    }

    public List<Integer> getIntegerList(String path) {
        return (List<Integer>) getList(path);
    }

    public List<Boolean> getBooleanList(String path) {
        return (List<Boolean>) getList(path);
    }

    public List<Double> getDoubleList(String path) {
        return (List<Double>) getList(path);
    }

    public List<Float> getFloatList(String path) {
        return (List<Float>) getList(path);
    }

    public List<Long> getLongList(String path) {
        return (List<Long>) getList(path);
    }

    public List<Byte> getByteList(String path) {
        return (List<Byte>) getList(path);
    }

    public List<Character> getCharacterList(String path) {
        return (List<Character>) getList(path);
    }

    public List<Short> getShortList(String path) {
        return (List<Short>) getList(path);
    }

    public List<Map<?, ?>> getMapList(String path) {
        return (List<Map<?, ?>>) getList(path);
    }


    public <T> T getObject(String path) {
        return getObject(path, null);
    }


    public <T> T getObject(String path, T def) {
        if (path.contains(".")) {
            String[] tmp = path.split("\\.");
            VelocityConfiguration configuration = getSection(tmp[0]);
            if (configuration == null) return def;
            return configuration.getObject(String.join(".", Arrays.copyOfRange(tmp, 1, tmp.length)), def);
        } else {
            T object = (T) configuration.get(path);
            if (object == null) object = def;
            return object;
        }
    }

    public VelocityConfiguration getConfigurationSection(String path) {
        return getSection(path);
    }

    public VelocityConfiguration getSection(String path) {
        if (path.contains(".")) {
            String[] tmp = path.split("\\.");
            VelocityConfiguration configuration = getSection(tmp[0]);
            if (configuration == null) return null;
            return configuration.getSection(String.join(".", Arrays.copyOfRange(tmp, 1, tmp.length)));
        } else {
            try {
                Map<String, Object> section = (Map<String, Object>) configuration.get(path);
                if (section == null) return null;
                return new VelocityConfiguration(section);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return this.configuration.toString();
    }
}