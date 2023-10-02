package it.angrybear.Utils;

import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class SerializeUtils {

    /**
     * Serializes an object.
     * If it is a primitive object (integer, long, boolean...)
     * then converts it using String.valueOf().
     * If it is an instance of UUID, then converts it using object.toString().
     * Otherwise, tries to use serializeToBase64().
     * If it fails, uses object.toString().
     * @param object: the object to be converted.
     * @return the resulting string.
     */
    public static String serializeUUIDOrBase64(Object object) {
        if (object == null) return null;
        if (object instanceof UUID) return object.toString();
        if (ReflUtil.isPrimitiveOrWrapper(object.getClass())) return String.valueOf(object);
        else try {
            return serializeToBase64(object);
        } catch (Exception e) {
            return object.toString();
        }
    }

    /**
     * Serializes an object into Base64.
     * @param object: the object.
     * @return the encoded object (null if failed).
     */
    public static String serializeToBase64(Object object) {
        if (object == null) return null;
        byte[] serialized = serialize(object);
        if (serialized == null) return null;
        return Base64.getEncoder().encodeToString(serialized);
    }

    /**
     * Converts an object into an array of bytes.
     * @param object: the object.
     * @return the array of bytes (null if failed).
     */
    public static byte[] serialize(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(object);
            outputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Deserializes an object.
     * First tries to use all primitives "valueOf" methods
     * (Integer.valueOf(), Long.valueOf(), Boolean.valueOf).
     * If it fails, tries to use UUID.fromString() to convert it
     * to an UUID.
     * If it fails, tries to use deserializeToBase64().
     * If it fails, returns a cast to (<O>) of the given string.
     * @param string: the string to deserialize.
     * @return the resulting object.
     */
    @SuppressWarnings("unchecked")
    public static <O> O deserializeUUIDOrBase64(String string) {
        if (string == null) return null;
        for (Class<?> clazz : ReflUtil.getWrapperClasses())
            if (clazz != Boolean.class && clazz != Byte.class)
                try {
                    Method valueOf = ReflUtil.getMethod(clazz, "valueOf", null, String.class);
                    if (valueOf != null) {
                        O tmp = (O) valueOf.invoke(clazz, string);
                        if (tmp != null) return tmp;
                    }
                } catch (Exception ignored) {}
        try {
            return (O) UUID.fromString(string);
        } catch (IllegalArgumentException e) {
            try {
                return deserializeFromBase64(string);
            } catch (Exception ex) {
                return (O) string;
            }
        }
    }

    /**
     * Deserializes a Base64 string into an object.
     * @param base64: the string.
     * @return the object (null if failed).
     */
    public static <O> O deserializeFromBase64(String base64) {
        if (base64 == null) return null;
        byte[] serialized = Base64.getDecoder().decode(base64);
        return deserialize(serialized);
    }

    /**
     * Converts an array of bytes into an object.
     * @param bytes: the array of bytes.
     * @return the object (null if failed).
     */
    @SuppressWarnings("unchecked")
    public static <O> O deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
            O object = (O) inputStream.readObject();
            inputStream.close();
            return object;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }
}