package it.angrybear.Enums;

import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class ClassEnum {
    public String name() {
        Class<? extends ClassEnum> clazz = this.getClass();
        return Arrays.stream(clazz.getDeclaredFields()).filter(f -> {
            Object field = new ReflObject<>(clazz.getCanonicalName(), false).getFieldObject(f.getName());
            return field != null && field.equals(this);
        }).map(Field::getName).findAny().orElse(null);
    }

    protected static <Enum extends ClassEnum> Enum valueOf(Class<Enum> enumClass, String name) {
        return Arrays.stream(enumClass.getDeclaredFields())
                .filter(f -> f.getType().equals(enumClass))
                .filter(f -> f.getName().equalsIgnoreCase(name))
                .map(f -> new ReflObject<>(enumClass.getCanonicalName(), false).obtainField(f.getName()))
                .map(ReflObject::getObject)
                .map(f -> (Enum) f)
                .findAny().orElse(null);
    }

    protected static <Enum extends ClassEnum> Enum[] values(Class<Enum> enumClass) {
        List<Enum> tmp = Arrays.stream(enumClass.getDeclaredFields())
                .filter(f -> f.getType().equals(enumClass))
                .map(f -> new ReflObject<>(enumClass.getCanonicalName(), false).obtainField(f.getName()))
                .map(ReflObject::getObject)
                .filter(Objects::nonNull)
                .map(f -> (Enum) f)
                .collect(Collectors.toList());
        Enum[] result = (Enum[]) Array.newInstance(enumClass, tmp.size());
        for (int i = 0; i < tmp.size(); i++) result[i] = tmp.get(i);
        return result;
    }
}
