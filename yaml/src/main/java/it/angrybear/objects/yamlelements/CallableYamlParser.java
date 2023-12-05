package it.angrybear.objects.yamlelements;

import it.angrybear.annotations.PreventSaving;
import it.angrybear.interfaces.IConfiguration;
import it.angrybear.interfaces.functions.TriConsumer;
import it.angrybear.objects.configurations.ConfigurationSection;
import it.angrybear.utils.StringUtils;
import it.fulminazzo.reflectionutils.objects.ReflObject;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CallableYamlParser<T> extends YamlParser<T> {
    private final Function<ConfigurationSection, T> function;

    public CallableYamlParser(Class<T> tClass, Function<ConfigurationSection, T> function) {
        super(tClass);
        this.function = function;
    }

    @Override
    protected BiFunction<IConfiguration, String, T> getLoader() {
        return (c, s) -> {
            ConfigurationSection section = c.getConfigurationSection(s);
            if (section == null) return null;
            T t = function.apply(section);
            if (t == null) return null;
            ReflObject<T> tReflObject = new ReflObject<>(t);
            for (Field field : tReflObject.getFields()) {
                if (field.isAnnotationPresent(PreventSaving.class)) continue;
                Object object = section.get(StringUtils.formatStringToYaml(field.getName()));
                if (object == null) continue;
                tReflObject.setField(field.getName(), object);
            }
            return t;
        };
    }

    @Override
    protected TriConsumer<IConfiguration, String, T> getDumper() {
        return (c, s, t) -> {
            ConfigurationSection section = c.createSection(s);
            if (t == null) return;
            ReflObject<T> tReflObject = new ReflObject<>(t);
            tReflObject.getFields().forEach(field -> {
                if (field.isAnnotationPresent(PreventSaving.class)) return;
                section.set(field.getName(), tReflObject.getFieldObject(field.getName()));
            });
        };
    }
}
