package it.angrybear.interfaces.configurations;

import it.angrybear.interfaces.IBearConfigPlugin;
import it.angrybear.interfaces.functions.TriFunctionException;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * An interface designed for enums containing options
 * from the main config.yml file.
 */
@SuppressWarnings("unchecked")
public interface IBearConfigOption extends IBearConfig {

    /**
     * Gets uuid.
     *
     * @return the uuid
     */
    default UUID getUUID() {
        return get(UUID.class);
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    default Date getDate() {
        return get(Date.class);
    }

    /**
     * Gets string.
     *
     * @return the string
     */
    default String getString() {
        return get(String.class);
    }

    /**
     * Gets integer.
     *
     * @return the integer
     */
    default Integer getInteger() {
        return get(Integer.class);
    }

    /**
     * Gets double.
     *
     * @return the double
     */
    default Double getDouble() {
        return get(Double.class);
    }

    /**
     * Gets float.
     *
     * @return the float
     */
    default Float getFloat() {
        return get(Float.class);
    }

    /**
     * Gets long.
     *
     * @return the long
     */
    default Long getLong() {
        return get(Long.class);
    }

    /**
     * Gets short.
     *
     * @return the short
     */
    default Short getShort() {
        return get(Short.class);
    }

    /**
     * Gets boolean.
     *
     * @return the boolean
     */
    default Boolean getBoolean() {
        return get(Boolean.class);
    }

    /**
     * Gets character.
     *
     * @return the character
     */
    default Character getCharacter() {
        return get(Character.class);
    }

    /**
     * Gets byte.
     *
     * @return the byte
     */
    default Byte getByte() {
        return get(Byte.class);
    }

    /**
     * Gets object.
     *
     * @return the object
     */
    default Object getObject() {
        return get(Object.class);
    }

    /**
     * Gets uuid list.
     *
     * @return the uuid list
     */
    default List<UUID> getUUIDList() {
        return getList(UUID.class);
    }

    /**
     * Gets date list.
     *
     * @return the date list
     */
    default List<Date> getDateList() {
        return getList(Date.class);
    }

    /**
     * Gets string list.
     *
     * @return the string list
     */
    default List<String> getStringList() {
        return getList(String.class);
    }

    /**
     * Gets integer list.
     *
     * @return the integer list
     */
    default List<Integer> getIntegerList() {
        return getList(Integer.class);
    }

    /**
     * Gets double list.
     *
     * @return the double list
     */
    default List<Double> getDoubleList() {
        return getList(Double.class);
    }

    /**
     * Gets float list.
     *
     * @return the float list
     */
    default List<Float> getFloatList() {
        return getList(Float.class);
    }

    /**
     * Gets long list.
     *
     * @return the long list
     */
    default List<Long> getLongList() {
        return getList(Long.class);
    }

    /**
     * Gets short list.
     *
     * @return the short list
     */
    default List<Short> getShortList() {
        return getList(Short.class);
    }

    /**
     * Gets boolean list.
     *
     * @return the boolean list
     */
    default List<Boolean> getBooleanList() {
        return getList(Boolean.class);
    }

    /**
     * Gets character list.
     *
     * @return the character list
     */
    default List<Character> getCharacterList() {
        return getList(Character.class);
    }

    /**
     * Gets byte list.
     *
     * @return the byte list
     */
    default List<Byte> getByteList() {
        return getList(Byte.class);
    }

    /**
     * Gets object list.
     *
     * @return the object list
     */
    default List<?> getObjectList() {
        return get(List.class);
    }

    /**
     * Gets enum list.
     *
     * @param <E>    the type parameter
     * @param eClass the e class
     * @return the enum list
     */
    default <E extends Enum<E>> List<E> getEnumList(Class<? extends E> eClass) {
        return (List<E>) get((config, p, c) -> config.getList(p, eClass), List.class);
    }

    /**
     * Gets list.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the list
     */
    default <T> List<T> getList(Class<T> clazz) {
        return (List<T>) get((config, p, c) -> config.getList(p, clazz), List.class);
    }

    /**
     * Gets enum.
     *
     * @param <E>    the type parameter
     * @param eClass the e class
     * @return the enum
     */
    default <E extends Enum<E>> E getEnum(Class<E> eClass) {
        return get(IConfiguration::getEnum, eClass);
    }

    /**
     * Get a general object of class clazz.
     *
     * @param <T>   the type parameter
     * @param clazz the class of the object
     * @return the object
     */
    default <T> T get(Class<T> clazz) {
        return get(IConfiguration::get, clazz);
    }

    /**
     * Get a general object of class oClass using a configuration function.
     *
     * @param <O>            the type parameter
     * @param configFunction the config function
     * @param oClass         the class of the object
     * @return the object
     */
    default <O> O get(TriFunctionException<FileConfiguration, String, Class<O>, O> configFunction, Class<O> oClass) {
        if (configFunction == null) return null;
        String path = getPath();
        if (path == null) return null;
        FileConfiguration config = getConfig();
        if (config == null) return null;
        try {
            return configFunction.apply(config, path, oClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    default FileConfiguration getConfig() {
        String path = getPath();
        IBearConfigPlugin plugin = getPlugin();
        if (path == null || plugin == null) return null;
        return plugin.getConfiguration();
    }
}