package it.angrybear.Bukkit.Utils;

import it.angrybear.Utils.ServerUtils;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class PluginsUtil {

    public static <P> P getPluginFromClass(Class<?> aClass) {
        if (aClass == null) return null;
        return getPluginFromClass(aClass.getCanonicalName());
    }

    public static <P> P getPluginFromClass(String className, Class<?>... superClasses) {
        if (className == null) return null;
        Collection<?> plugins = ServerUtils.getPlugins();
        if (plugins == null) return null;
        return (P) plugins.stream()
                .filter(p -> Arrays.stream(superClasses).allMatch(c -> Arrays.asList(ReflUtil.getClassAndSuperClasses(p.getClass())).contains(c)))
                .min(Comparator.comparing(p -> Math.abs(p.getClass().getCanonicalName().compareTo(className))))
                .orElse(null);
    }
}