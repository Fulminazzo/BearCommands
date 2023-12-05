package it.angrybear.utils;


import it.fulminazzo.reflectionutils.objects.ReflObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumUtils {

    /**
     * Returns the value of an enum from the
     * given enum name. If an enum is not found,
     * null is returned.
     *
     * @param <E>    the type of the enum
     * @param eClass the enum class
     * @param name   the name of the enum
     * @return the enum
     */
    public static <E extends Enum<E>> E valueOf(Class<? extends E> eClass, String name) {
        if (name == null || eClass == null) return null;
        for (E e : eClass.getEnumConstants()) if (e.name().equalsIgnoreCase(name)) return e;
        return null;
    }

    /**
     * Returns the names of all the values in
     * an enum.
     *
     * @param <E>    the type of the enum
     * @param eClass the enum class
     * @return the names
     */
    public static <E extends Enum<E>> List<String> returnValuesAsNames(Class<E> eClass) {
        if (eClass == null) return new ArrayList<>();
        ReflObject<E> enumObject = new ReflObject<>(eClass.getCanonicalName(), false);
        E[] values = enumObject.getMethodObject("values");
        return Arrays.stream(values).map(Enum::name).map(String::toLowerCase).collect(Collectors.toList());
    }
}