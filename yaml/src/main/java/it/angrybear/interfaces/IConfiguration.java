package it.angrybear.interfaces;

import it.angrybear.exceptions.yamlexceptions.CannotBeNullException;
import it.angrybear.exceptions.yamlexceptions.UnexpectedClassException;
import it.angrybear.objects.configurations.ConfigurationSection;
import it.angrybear.objects.configurations.FileConfiguration;
import it.angrybear.objects.yamlelements.YamlParser;
import it.angrybear.utils.EnumUtils;
import it.fulminazzo.reflectionutils.utils.ReflUtil;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "unused"})
public interface IConfiguration {

    /**
     * Gets root.
     *
     * @return the root configuration
     */
    default IConfiguration getRoot() {
        IConfiguration section = this;
        while (section.getParent() != null) section = section.getParent();
        return section;
    }

    /**
     * Gets keys.
     *
     * @return the keys
     */
    default Set<String> getKeys() {
        return getKeys(false);
    }

    /**
     * Gets keys.
     *
     * @param deep if true, gets keys from subsections.
     * @return the keys
     */
    default Set<String> getKeys(boolean deep) {
        Map<String, Object> map = toMap();
        List<String> keys = new ArrayList<>(map.keySet());
        if (deep)
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof IConfiguration)
                    keys.addAll(((IConfiguration) value).getKeys(true).stream()
                            .map(c -> entry.getKey() + "." + c).collect(Collectors.toList()));
            }
        return new HashSet<>(keys);
    }

    /**
     * Gets values.
     *
     * @return the values
     */
    default Map<String, Object> getValues() {
        return getValues(false);
    }

    /**
     * Gets values.
     *
     * @param deep if true, gets values from subsections.
     * @return the values
     */
    default Map<String, Object> getValues(boolean deep) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : toMap().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof IConfiguration)
                if (deep) value = ((IConfiguration) value).getValues(true);
                else continue;
            result.put(entry.getKey(), value);
        }
        return result;
    }

    /**
     * Checks if an object is present at the given path.
     *
     * @param path the path
     * @return true if an object is found.
     */
    default boolean contains(String path) {
        if (path == null) return false;
        List<String> sectionPath = parseSectionPath(path);
        if (sectionPath.isEmpty()) return toMap().containsKey(path);
        else {
            String p = sectionPath.get(0);
            IConfiguration section = getConfigurationSection(p);
            return section != null && section.contains(removeFirstPath(path));
        }
    }

    /**
     * Creates a configuration section.
     *
     * @param path the path
     * @return the configuration section
     */
    default ConfigurationSection createSection(String path) {
        return createSection(path, null);
    }

    /**
     * Creates a configuration section and fills it with the given map.
     *
     * @param path the path
     * @param map  the map
     * @return the configuration section
     */
    default ConfigurationSection createSection(String path, Map<Object, Object> map) {
        List<String> sectionPath = parseSectionPath(path);
        if (sectionPath.isEmpty()) {
            ConfigurationSection section = new ConfigurationSection(this, path);
            toMap().put(path, section);
            if (map != null) map.forEach((k, v) -> section.set(k.toString(), v));
            return section;
        } else {
            String p = sectionPath.get(0);
            IConfiguration section = getConfigurationSection(p);
            if (section == null) section = createSection(p);
            return section.createSection(removeFirstPath(path), map);
        }
    }

    /**
     * Gets configuration section.
     *
     * @param <C>  the type of the section
     * @param path the path
     * @return the configuration section
     */
    default <C extends IConfiguration> C getConfigurationSection(String path) {
        return (C) get(path, IConfiguration.class);
    }

    /**
     * Check if is configuration section.
     *
     * @param path the path
     * @return the configuration section
     */
    default boolean isConfigurationSection(String path) {
        return is(path, IConfiguration.class);
    }

    /**
     * Sets an object to the given path.
     *
     * @param <O>  the type of the object
     * @param path the path
     * @param o    the object
     */
    default <O> void set(String path, O o) {
        List<String> sectionPath = parseSectionPath(path);
        IConfiguration section = this;
        if (!sectionPath.isEmpty()) {
            String p = String.join(".", sectionPath);
            section = getConfigurationSection(p);
            if (section == null) section = createSection(p);
            path = getNameFromPath(path);
        }
        if (o == null) section.toMap().remove(path);
        else if (o instanceof Enum<?>) section.toMap().put(path, ((Enum<?>) o).name());
        else {
            YamlParser<O> parser = (YamlParser<O>) FileConfiguration.getParser(o.getClass());
            if (!isPrimitiveOrWrapper(o) && parser != null)
                try {parser.dump(this, path, o);}
                catch (NullPointerException ignored) {}
            else section.toMap().put(path, o);
        }
    }

    /**
     * Converts the given object using its associated YAML parser (if found).
     *
     * @param <T>    the type of the object
     * @param path   the path
     * @param object the object
     * @return the final object
     */
    default <T> T convertObjectToYAMLObject(String path, Object object) {
        return convertObjectToYAMLObject(path, object, FileConfiguration.getParsers());
    }

    /**
     * Converts the given object using its associated YAML parser (if found).
     *
     * @param <T>    the type of the object
     * @param path   the path
     * @param object the object
     * @param clazz  the class of the object
     * @return the final object
     */
    default <T> T convertObjectToYAMLObject(String path, Object object, Class<T> clazz) {
        if (Float.class.isAssignableFrom(clazz) && object instanceof Double)
            object = Float.valueOf(String.valueOf(object));
        return convertObjectToYAMLObject(path, object, Collections.singletonList(FileConfiguration.getParser(clazz)));
    }

    /**
     * Converts the given object using its associated YAML parser (if found).
     *
     * @param <T>     the type of the object
     * @param path    the path
     * @param object  the object
     * @param parsers the parsers
     * @return the final object
     */
    default <T> T convertObjectToYAMLObject(String path, Object object, List<YamlParser<?>> parsers) {
        if (isPrimitiveOrWrapper(object)) return (T) object;
        for (YamlParser<?> parser : parsers)
            if (parser != null)
                try {
                    return (T) parser.load(this, path);
                } catch (NullPointerException | IllegalArgumentException | UnexpectedClassException |
                         CannotBeNullException ignored) {}
        return (T) object;
    }

    /**
     * Gets enum.
     *
     * @param <E>    the type of the enum
     * @param path   the path
     * @param eClass the e class
     * @return the enum
     */
    default <E extends Enum<E>> E getEnum(String path, Class<E> eClass) {
        return getEnum(path, null, eClass);
    }

    /**
     * Gets enum.
     *
     * @param <E>    the type of the enum
     * @param path   the path
     * @param def    the def
     * @param eClass the e class
     * @return the enum
     */
    default <E extends Enum<E>> E getEnum(String path, E def, Class<E> eClass) {
        String name = getString(path);
        if (name == null) return null;
        E e = EnumUtils.valueOf(eClass, name);
        if (e == null) e = def;
        if (e == null && checkNonNull()) throw new CannotBeNullException(getCurrentPath(), name, name);
        return e;
    }

    /**
     * Gets string.
     *
     * @param path the path
     * @return the string
     */
    default String getString(String path) {
        return getString(path, null);
    }

    /**
     * Gets string.
     *
     * @param path the path
     * @param def  the def
     * @return the string
     */
    default String getString(String path, String def) {
        return get(path, def, String.class);
    }

    /**
     * Check if is string.
     *
     * @param path the path
     * @return the string
     */
    default boolean isString(String path) {
        return is(path, String.class);
    }

    /**
     * Gets integer.
     *
     * @param path the path
     * @return the integer
     */
    default Integer getInteger(String path) {
        return getInteger(path, null);
    }

    /**
     * Gets integer.
     *
     * @param path the path
     * @param def  the def
     * @return the integer
     */
    default Integer getInteger(String path, Integer def) {
        return get(path, def, Integer.class);
    }

    /**
     * Check if is integer.
     *
     * @param path the path
     * @return the integer
     */
    default boolean isInteger(String path) {
        return is(path, Integer.class);
    }

    /**
     * Gets double.
     *
     * @param path the path
     * @return the double
     */
    default Double getDouble(String path) {
        return getDouble(path, null);
    }

    /**
     * Gets double.
     *
     * @param path the path
     * @param def  the def
     * @return the double
     */
    default Double getDouble(String path, Double def) {
        return get(path, def, Double.class);
    }

    /**
     * Check if is double.
     *
     * @param path the path
     * @return the double
     */
    default boolean isDouble(String path) {
        return is(path, Double.class);
    }

    /**
     * Gets float.
     *
     * @param path the path
     * @return the float
     */
    default Float getFloat(String path) {
        return getFloat(path, null);
    }

    /**
     * Gets float.
     *
     * @param path the path
     * @param def  the def
     * @return the float
     */
    default Float getFloat(String path, Float def) {
        return get(path, def, Float.class);
    }

    /**
     * Check if is float.
     *
     * @param path the path
     * @return the float
     */
    default boolean isFloat(String path) {
        return is(path, Float.class);
    }

    /**
     * Gets long.
     *
     * @param path the path
     * @return the long
     */
    default Long getLong(String path) {
        return getLong(path, null);
    }

    /**
     * Gets long.
     *
     * @param path the path
     * @param def  the def
     * @return the long
     */
    default Long getLong(String path, Long def) {
        return get(path, def, Long.class);
    }

    /**
     * Check if is long.
     *
     * @param path the path
     * @return the long
     */
    default boolean isLong(String path) {
        return is(path, Long.class);
    }

    /**
     * Gets short.
     *
     * @param path the path
     * @return the short
     */
    default Short getShort(String path) {
        return getShort(path, null);
    }

    /**
     * Gets short.
     *
     * @param path the path
     * @param def  the def
     * @return the short
     */
    default Short getShort(String path, Short def) {
        return get(path, def, Short.class);
    }

    /**
     * Check if is short.
     *
     * @param path the path
     * @return the short
     */
    default boolean isShort(String path) {
        return is(path, Short.class);
    }

    /**
     * Gets boolean.
     *
     * @param path the path
     * @return the boolean
     */
    default Boolean getBoolean(String path) {
        return getBoolean(path, null);
    }

    /**
     * Gets boolean.
     *
     * @param path the path
     * @param def  the def
     * @return the boolean
     */
    default Boolean getBoolean(String path, Boolean def) {
        return get(path, def, Boolean.class);
    }

    /**
     * Check if is boolean.
     *
     * @param path the path
     * @return the boolean
     */
    default boolean isBoolean(String path) {
        return is(path, Boolean.class);
    }

    /**
     * Gets character.
     *
     * @param path the path
     * @return the character
     */
    default Character getCharacter(String path) {
        return getCharacter(path, null);
    }

    /**
     * Gets character.
     *
     * @param path the path
     * @param def  the def
     * @return the character
     */
    default Character getCharacter(String path, Character def) {
        return get(path, def, Character.class);
    }

    /**
     * Check if is character.
     *
     * @param path the path
     * @return the character
     */
    default boolean isCharacter(String path) {
        return is(path, Character.class);
    }

    /**
     * Gets byte.
     *
     * @param path the path
     * @return the byte
     */
    default Byte getByte(String path) {
        return getByte(path, null);
    }

    /**
     * Gets byte.
     *
     * @param path the path
     * @param def  the def
     * @return the byte
     */
    default Byte getByte(String path, Byte def) {
        return get(path, def, Byte.class);
    }

    /**
     * Check if is byte.
     *
     * @param path the path
     * @return the byte
     */
    default boolean isByte(String path) {
        return is(path, Byte.class);
    }

    /**
     * Gets object.
     *
     * @param path the path
     * @return the object
     */
    default Object getObject(String path) {
        return getObject(path, null);
    }

    /**
     * Gets object.
     *
     * @param path the path
     * @param def  the def
     * @return the object
     */
    default Object getObject(String path, Object def) {
        return get(path, def, Object.class);
    }

    /**
     * Gets enum list.
     *
     * @param <E>    the type of the enum
     * @param path   the path
     * @param eClass the e class
     * @return the enum list
     */
    default <E extends Enum<E>> List<E> getEnumList(String path, Class<? extends E> eClass) {
        return getEnumList(path, null, eClass);
    }

    /**
     * Gets enum list.
     *
     * @param <E>    the type of the enum
     * @param path   the path
     * @param def    the def
     * @param eClass the e class
     * @return the enum list
     */
    default <E extends Enum<E>> List<E> getEnumList(String path, E def, Class<? extends E> eClass) {
        List<String> list = getStringList(path);
        return list.stream()
                .filter(e -> check(path, e, eClass))
                .map(e -> EnumUtils.valueOf(eClass, e))
                .collect(Collectors.toList());
    }

    /**
     * Gets string list.
     *
     * @param path the path
     * @return the string list
     */
    default List<String> getStringList(String path) {
        return getList(path, String.class);
    }

    /**
     * Gets integer list.
     *
     * @param path the path
     * @return the integer list
     */
    default List<Integer> getIntegerList(String path) {
        return getList(path, Integer.class);
    }

    /**
     * Gets double list.
     *
     * @param path the path
     * @return the double list
     */
    default List<Double> getDoubleList(String path) {
        return getList(path, Double.class);
    }

    /**
     * Gets float list.
     *
     * @param path the path
     * @return the float list
     */
    default List<Float> getFloatList(String path) {
        return getList(path, Float.class);
    }

    /**
     * Gets long list.
     *
     * @param path the path
     * @return the long list
     */
    default List<Long> getLongList(String path) {
        return getList(path, Long.class);
    }

    /**
     * Gets short list.
     *
     * @param path the path
     * @return the short list
     */
    default List<Short> getShortList(String path) {
        return getList(path, Short.class);
    }

    /**
     * Gets boolean list.
     *
     * @param path the path
     * @return the boolean list
     */
    default List<Boolean> getBooleanList(String path) {
        return getList(path, Boolean.class);
    }

    /**
     * Gets character list.
     *
     * @param path the path
     * @return the character list
     */
    default List<Character> getCharacterList(String path) {
        return getList(path, Character.class);
    }

    /**
     * Gets byte list.
     *
     * @param path the path
     * @return the byte list
     */
    default List<Byte> getByteList(String path) {
        return getList(path, Byte.class);
    }

    /**
     * Gets object list.
     *
     * @param path the path
     * @return the object list
     */
    default List<?> getObjectList(String path) {
        return getObjectList(path, null);
    }

    /**
     * Gets object list.
     *
     * @param path the path
     * @param def  the def
     * @return the object list
     */
    default List<?> getObjectList(String path, List<?> def) {
        return get(path, def, List.class);
    }

    /**
     * Check if is list.
     *
     * @param path the path
     * @return the list
     */
    default boolean isList(String path) {
        return is(path, List.class);
    }

    /**
     * Gets list.
     *
     * @param <T>   the type of the elements in the list
     * @param path  the path
     * @param clazz the clazz
     * @return the list
     */
    default <T> List<T> getList(String path, Class<T> clazz) {
        List<?> list = getObjectList(path);
        if (list == null) return null;
        return list.stream()
                .map(o -> convertObjectToYAMLObject(path, o, clazz))
                .filter(o -> check(path, o, clazz))
                .collect(Collectors.toList());
    }

    /**
     * Gets an object of the specified type
     * and checks if it is valid.
     *
     * @param <T>  the type of the object
     * @param path the path
     * @return the result
     */
    default <T> T get(String path) {
        return get(path, (T) null);
    }

    /**
     * Gets an object of the specified type
     * and checks if it is valid. If is null,
     * return the def argument.
     *
     * @param <T>  the type of the object
     * @param path the path
     * @param def  the default object
     * @return the result
     */
    default <T> T get(String path, T def) {
        if (path == null) throw new CannotBeNullException(getCurrentPath(), "null", "Path");
        List<String> parsedPath = parseSectionPath(path);
        if (parsedPath.isEmpty()) {
            Object object = toMap().get(path);
            if (object == null) object = def;
            object = convertObjectToYAMLObject(path, object);
            return (T) object;
        } else {
            IConfiguration section = getConfigurationSection(parsedPath.get(0));
            if (section == null) return def;
            else return section.get(removeFirstPath(path), def);
        }
    }

    /**
     * Gets an object of the specified type
     * and checks if it is valid.
     *
     * @param <T>   the type of the object
     * @param path  the path
     * @param clazz the class
     * @return the result
     */
    default <T> T get(String path, Class<T> clazz) {
        return get(path, null, clazz);
    }

    /**
     * Gets an object of the specified type
     * and checks if it is valid. If is null,
     * return the def argument.
     *
     * @param <T>   the type of the object
     * @param path  the path
     * @param def   the default object
     * @param clazz the class
     * @return the result
     */
    default <T> T get(String path, T def, Class<T> clazz) {
        if (path == null) throw new CannotBeNullException(getCurrentPath(), "null", "Path");
        List<String> parsedPath = parseSectionPath(path);
        if (parsedPath.isEmpty()) {
            Object object = toMap().get(path);
            if (object == null) object = def;
            object = convertObjectToYAMLObject(path, object, clazz);
            check(path, object, clazz);
            return (T) object;
        } else {
            IConfiguration section = getConfigurationSection(parsedPath.get(0));
            if (section == null) return def;
            else return section.get(removeFirstPath(path), def, clazz);
        }
    }

    /**
     * Checks if the object at the given
     * path is of the given class.
     *
     * @param <T>   the type of the class
     * @param path  the path
     * @param clazz the class
     * @return true if is an instance of class
     */
    default <T> boolean is(String path, Class<T> clazz) {
        try {
            T t = get(path, clazz);
            return t != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the given Object is of the
     * expected class and if is not null (or
     * nullability is set to false).
     *
     * @param <T>    the type of the class
     * @param name   the name
     * @param object the object
     * @param clazz  the class
     * @return true if no exceptions are thrown
     */
    default <T> boolean check(String name, Object object, Class<T> clazz) {
        if (clazz == null) throw new CannotBeNullException(getCurrentPath(), "null", "Class");
        if (object == null && checkNonNull()) throw new CannotBeNullException(getCurrentPath(), name, name);
        if (object != null && !clazz.isAssignableFrom(object.getClass()))
            throw new UnexpectedClassException(getCurrentPath(), name, object, clazz.getSimpleName());
        return true;
    }

    /**
     * Removes the first element from the path.
     *
     * @param path the path
     * @return the string
     */
    default String removeFirstPath(String path) {
        String[] tmp = path.split("\\.");
        return String.join(".", Arrays.copyOfRange(tmp, 1, tmp.length));
    }

    /**
     * Gets name from the path.
     *
     * @param path the path
     * @return the name
     */
    default String getNameFromPath(String path) {
        String[] tmp = path.split("\\.");
        return tmp[tmp.length - 1];
    }

    /**
     * Converts a path into a list of elements
     * and removes the last element.
     *
     * @param path the path
     * @return the list
     */
    default List<String> parseSectionPath(String path) {
        List<String> parsedPath = parsePath(path);
        if (!parsedPath.isEmpty()) parsedPath.remove(parsedPath.size() - 1);
        return parsedPath;
    }

    /**
     * Converts a path into a list of elements.
     *
     * @param path the path
     * @return the list
     */
    default List<String> parsePath(String path) {
        if (path == null) throw new CannotBeNullException(getCurrentPath(), "null", "Path");
        List<String> list = new ArrayList<>();
        for (String p : path.split("\\."))
            if (p.trim().isEmpty()) return list;
            else list.add(p); 
        return list;
    }

    /**
     * Gets the current path.
     *
     * @return the current path
     */
    default String getCurrentPath() {
        String path = getName();
        IConfiguration parent = getParent();
        while (parent != null) {
            String parentName = parent.getName();
            if (!parentName.isEmpty()) path = parentName + "." + path;
            parent = parent.getParent();
        }
        return path;
    }

    /**
     * Sets the nullability of the configuration.
     * If set to true, the plugin will not accept null objects
     * when calling get methods.
     *
     * @param nonNull the non-null boolean
     */
    default void setNonNull(boolean nonNull) {
        toMap().values().stream()
                .filter(v -> v instanceof IConfiguration)
                .map(v -> ((IConfiguration) v))
                .forEach(v -> v.setNonNull(nonNull));
    }

    /**
     * Checks if an object is primitive or wrapper.
     *
     * @param object the object
     * @return true if is null, primitive, wrapper or a string
     */
    static boolean isPrimitiveOrWrapper(Object object) {
        if (object == null) return true;
        if (object instanceof Collection) {
            List<?> list = new ArrayList<>((Collection<?>) object);
            if (list.isEmpty()) return true;
            else object = list.get(0);
        }
        return object == null || isPrimitiveOrWrapper(object.getClass());
    }

    /**
     * Checks if a class is primitive or wrapper.
     *
     * @param clazz the class
     * @return true if is null, primitive, wrapper or a string
     */
    static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        if (clazz == null) return true;
        if (clazz.isArray()) clazz = clazz.getComponentType();
        return ReflUtil.isPrimitiveOrWrapper(clazz) || clazz.equals(String.class);
    }

    /**
     * Print the current configuration keys and values.
     */
    default void print() {
        System.out.println(this);
        System.out.println(toString(""));
    }

    /**
     * To string method.
     *
     * @param start the head start for the print
     * @return the result string
     */
    default String toString(String start) {
        String result = "";
        Map<String, Object> map = toMap();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result += String.format("%s%s: ", start, key);
            if (value instanceof IConfiguration) {
                result += "\n";
                result += ((IConfiguration) value).toString(start + "  ");
            } else result += String.format("%s\n", value);
        }
        return result;
    }

    /**
     * Converts a general map to a configuration map.
     *
     * @param parent the parent configuration.
     * @param map    the general map
     * @return the configuration map
     */
    static Map<String, Object> generalToConfigMap(IConfiguration parent, Map<Object, Object> map) {
        LinkedHashMap<String, Object> treeMap = new LinkedHashMap<>();
        if (map == null) return treeMap;
        map.forEach((k, v) -> {
            if (v instanceof Map) v = new ConfigurationSection(parent, k.toString(), (Map<Object, Object>) v);
            treeMap.put(k.toString(), v);
        });
        return treeMap;
    }

    /**
     * Converts a configuration map to a general map.
     *
     * @param config the configuration
     * @return the general map
     */
    static Map<String, Object> configToGeneralMap(IConfiguration config) {
        if (config == null) return null;
        LinkedHashMap<String, Object> treeMap = new LinkedHashMap<>();
        Map<String, Object> map = config.toMap();
        map.forEach((k, v) -> {
            if (v instanceof IConfiguration) v = configToGeneralMap((IConfiguration) v);
            treeMap.put(k, v);
        });
        return treeMap;
    }

    /**
     * Check if nullability is allowed.
     *
     * @return the boolean
     */
    boolean checkNonNull();

    /**
     * Gets the current configuration name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the parent configuration.
     *
     * @return the parent
     */
    IConfiguration getParent();

    /**
     * Converts the current configuration to a map.
     *
     * @return the map
     */
    Map<String, Object> toMap();
}
