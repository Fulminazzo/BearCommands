package it.angrybear.Objects.YamlElements;

import it.angrybear.Bukkit.Utils.NMSUtils;
import it.angrybear.Objects.ABearPlayer;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Utils.ServerUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public abstract class YamlObject<O> {
    protected O object;
    protected final YamlPair<?>[] yamlPairs;
    private static final YamlPair<?>[] defaultPairs = new YamlPair[]{
            new YamlPair<>(UUID.class, UUIDYamlObject.class),
            new YamlPair<>(Date.class, DateYamlObject.class),
            new YamlPair<>(ABearPlayer.class, BearPlayerYamlObject.class),
            new YamlPair<>(Map.class, MapYamlObject.class),
            new YamlPair<>(Collection.class, CollectionYamlObject.class),
            new YamlPair<>(Enum.class, EnumYamlObject.class),
    };

    public YamlObject(YamlPair<?>[] yamlPairs) {
        this.yamlPairs = yamlPairs;
    }

    public YamlObject(O object, YamlPair<?>[] yamlPairs) {
        this.object = object;
        this.yamlPairs = yamlPairs;
    }

    public O getObject() {
        return object;
    }

    public abstract O load(Configuration configurationSection, String path) throws Exception;

    public abstract void dump(Configuration configurationSection, String path) throws Exception;

    public static <Y> Y newObject(Class<?> aClass, YamlPair<?>[] yamlPairs) {
        if (aClass == null) return null;
        Class<Y> yamlClass = (Class<Y>) getYamlClass(aClass, yamlPairs);
        if (yamlClass == null)
            if (aClass.isArray()) return (Y) new ArrayYamlObject<>(yamlPairs);
            else return (Y) new GeneralYamlObject(yamlPairs);
        else if (yamlClass == BearPlayerYamlObject.class || aClass.isEnum()) return new ReflObject<>(yamlClass, aClass, yamlPairs).getObject();
        else return new ReflObject<>(yamlClass, (Object) yamlPairs).getObject();
    }

    public static <Y> Y newObject(Object object, YamlPair<?>[] yamlPairs) {
        Class<Y> yamlClass = (Class<Y>) getYamlClass(object == null ? null : object.getClass(), yamlPairs);
        if (yamlClass == null)
            if ((object != null && object.getClass().isArray())) return (Y) new ArrayYamlObject<>(object, yamlPairs);
            else return (Y) new GeneralYamlObject(object, yamlPairs);
        else if (object instanceof ABearPlayer) return new ReflObject<>(yamlClass, object, object.getClass(), yamlPairs).getObject();
        else return new ReflObject<>(yamlClass, object, yamlPairs).getObject();
    }

    public static <O> Class<? extends YamlObject<O>> getYamlClass(Class<?> aClass, YamlPair<?>[] yamlPairs) {
        if (aClass == null) return null;
        if (ServerUtils.isBukkit())
            try {aClass = NMSUtils.convertCraftClassToSpigotClass(aClass);}
            catch (Exception ignored) {}
        List<Class<?>> classAndSuperClasses = Arrays.asList(ReflUtil.getClassAndSuperClasses(aClass));
        return (Class<? extends YamlObject<O>>) Stream.concat(Arrays.stream(yamlPairs), Arrays.stream(defaultPairs))
                .distinct()
                .filter(p -> classAndSuperClasses.contains(p.getObjectClass()))
                .map(YamlPair::getYamlClass)
                .findFirst().orElse(null);
    }
}
