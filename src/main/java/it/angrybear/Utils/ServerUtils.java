package it.angrybear.Utils;

import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerUtils {
    private final static String bukkitPlayerClass = "CraftPlayer";
    private final static String bukkitConfigurationSection = "ConfigurationSection";
    private final static String bungeePlayerClass = "UserConnection";
    private final static String bungeeConfigurationSection = "Configuration";

    public static String getVersion() {
        if (isBukkit()) {
            ReflObject<?> bukkit = getBukkit();
            String version = bukkit.getMethodObject("getBukkitVersion");
            return version.split("-")[0];
        } else {
            ReflObject<?> proxyServer = getProxyServerInstance();
            String version = proxyServer.getMethodObject("getVersion");
            return version.split(":")[2].split("-")[0];
        }
    }

    public static ReflObject<?> getBukkit() {
        return new ReflObject<>("org.bukkit.Bukkit", false);
    }

    public static ReflObject<?> getPluginManager() {
        ReflObject<?> instance = isBukkit() ? getBukkit() : getProxyServerInstance();
        return instance.callMethod("getPluginManager");
    }

    public static ReflObject<?> getScheduler() {
        return (ServerUtils.isBukkit() ? getBukkit() : getProxyServerInstance()).callMethod("getScheduler");
    }

    public static ReflObject<?> getProxyServerInstance() {
        return new ReflObject<>("net.md_5.bungee.api.ProxyServer", false).callMethod("getInstance");
    }

    public static ReflObject<?> getConfigurationProvider() {
        // return ConfigurationProvider.getProvider(YamlConfiguration.class);
        ReflObject<?> configurationProvider = new ReflObject<>("net.md_5.bungee.config.ConfigurationProvider", false);
        Class<?> yamlConfiguration = ReflUtil.getClass("net.md_5.bungee.config.YamlConfiguration");
        return configurationProvider.callMethod("getProvider", yamlConfiguration);
    }

    public static boolean isPlayer(Object object) {
        return isClass(object, bukkitPlayerClass, bungeePlayerClass);
    }

    public static boolean isConfigurationSection(Object object) {
        return isClass(object, bukkitConfigurationSection, bungeeConfigurationSection);
    }

    private static boolean isClass(Object object, String bukkitClass, String bungeeClass) {
        if (object == null) return false;
        List<String> classesAndSuperClasses = Arrays.stream(ReflUtil.getClassAndSuperClasses(object.getClass()))
                .map(Class::getSimpleName)
                .collect(Collectors.toList());
        if (isBukkit()) return classesAndSuperClasses.contains(bukkitClass);
        else return classesAndSuperClasses.contains(bungeeClass);
    }

    public static boolean isBukkit() {
        try {
            Class.forName("org.bukkit.Bukkit");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Class<?> getPlayerClass() {
        return ReflUtil.getClass(isBukkit() ? bukkitPlayerClass : bungeePlayerClass);
    }
}