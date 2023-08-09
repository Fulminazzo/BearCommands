package it.angrybear.Enums;

import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.lang.reflect.Field;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public abstract class ClassEnum {
    public String name() {
        Class<? extends ClassEnum> clazz = this.getClass();
        return Arrays.stream(clazz.getDeclaredFields()).filter(f -> {
            Object field = new ReflObject<>(clazz.getCanonicalName(), false).getFieldObject(f.getName());
            return field != null && field.equals(this);
        }).map(Field::getName).findAny().orElse(null);
    }
}
