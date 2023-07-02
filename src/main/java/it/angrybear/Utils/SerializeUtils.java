package it.angrybear.Utils;

import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class SerializeUtils {

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

    public static String serializeToBase64(Object object) {
        if (object == null) return null;
        byte[] serialized = serialize(object);
        if (serialized == null) return null;
        return Base64.getEncoder().encodeToString(serialized);
    }

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

    public static <O> O deserializeFromBase64(String base64) {
        if (base64 == null) return null;
        byte[] serialized = Base64.getDecoder().decode(base64);
        return deserialize(serialized);
    }

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