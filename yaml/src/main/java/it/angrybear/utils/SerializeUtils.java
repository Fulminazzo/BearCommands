package it.angrybear.utils;

import java.io.*;
import java.util.Base64;

public class SerializeUtils {

    /**
     * Serializes an object into Base64.
     *
     * @param object the object
     * @return the encoded object (null if failed)
     */
    public static String serializeToBase64(Object object) {
        if (object == null) return null;
        byte[] serialized = serialize(object);
        if (serialized == null) return null;
        return Base64.getEncoder().encodeToString(serialized);
    }

    /**
     * Converts an object into an array of bytes.
     *
     * @param object the object
     * @return the array of bytes (null if failed)
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
     * Deserializes a Base64 string into an object.
     *
     * @param <O>    the type parameter
     * @param base64 the string
     * @return the object (null if failed)
     */
    public static <O> O deserializeFromBase64(String base64) {
        if (base64 == null) return null;
        byte[] serialized = Base64.getDecoder().decode(base64);
        return deserialize(serialized);
    }

    /**
     * Converts an array of bytes into an object.
     *
     * @param <O>   the type parameter
     * @param bytes the array of bytes
     * @return the object (null if failed)
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