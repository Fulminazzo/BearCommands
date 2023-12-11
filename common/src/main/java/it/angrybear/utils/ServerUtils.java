package it.angrybear.utils;

import it.fulminazzo.reflectionutils.objects.ReflObject;
import it.fulminazzo.reflectionutils.utils.ReflUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

//TODO: Full doc
@SuppressWarnings("unchecked")
public class ServerUtils {
    //TODO: parse ???
    private final static String bukkitPlayerClass = "CraftPlayer";
    private final static String bukkitConfigurationSection = "ConfigurationSection";
    private final static String bungeePlayerClass = "UserConnection";
    private final static String bungeeConfigurationSection = "Configuration";
    private final static String velocityPlayerClass = "ConnectedPlayer";

    public static boolean isPluginEnabled(String pluginName) {
        if (pluginName == null) return false;
        else if (isBukkit()) return new ReflObject<>("it.angrybear.Utils.Bukkit.BukkitUtils", false)
                .getMethodObject("isPluginEnabled", pluginName);
        else if (isVelocity()) return getPluginManager().callMethod("getPlugin", pluginName).getMethodObject("orElse", (Object) null) != null;
        else return getPluginManager().getMethodObject("getPlugin", pluginName) != null;
    }

    public static String getVersion() {
        if (isBukkit()) {
            ReflObject<?> bukkit = getBukkit();
            String version = bukkit.getMethodObject("getBukkitVersion");
            return version.split("-")[0];
        } else if (isVelocity()) {
            return getProxyServerInstance().callMethod("getVersion")
                    .getMethodObject("getVersion")
                    .toString().split(" ")[0];
        } else {
            ReflObject<?> proxyServer = getProxyServerInstance();
            String version = proxyServer.getMethodObject("getVersion");
            return version.split(":")[2].split("-")[0];
        }
    }

    public static ReflObject<?> getBukkit() {
        return new ReflObject<>("org.bukkit.Bukkit", false);
    }

    public static ReflObject<?> getProxyServerInstance() {
        if (isVelocity()) return new ReflObject<>("it.angrybear.Velocity.VelocityBearCommandsPlugin", false)
                .callMethod("getPlugin").callMethod("getProxyServer");
        else return new ReflObject<>("net.md_5.bungee.api.ProxyServer", false).callMethod("getInstance");
    }

    public static ReflObject<?> getPluginManager() {
        ReflObject<?> instance = isBukkit() ? getBukkit() : getProxyServerInstance();
        return instance.callMethod("getPluginManager");
    }

    public static ReflObject<?> getScheduler() {
        if (ServerUtils.isFolia()) return getBukkit().callMethod("getGlobalRegionScheduler");
        return (ServerUtils.isBukkit() ? getBukkit() : getProxyServerInstance()).callMethod("getScheduler");
    }

    public static ReflObject<?> getConfigurationProvider() {
        // return ConfigurationProvider.getProvider(YamlConfiguration.class);
        ReflObject<?> configurationProvider = new ReflObject<>("net.md_5.bungee.config.ConfigurationProvider", false);
        Class<?> yamlConfiguration = ReflUtil.getClass("net.md_5.bungee.config.YamlConfiguration");
        return configurationProvider.callMethod("getProvider", yamlConfiguration);
    }

    public static boolean isPlayer(Object object) {
        if (object == null) return false;
        if (object instanceof ReflObject<?>) object = ((ReflObject<?>) object).getObject();
        return isClass(object, bukkitPlayerClass, bungeePlayerClass, velocityPlayerClass);
    }

    public static Class<?> getUniversalClass(String bukkitClass, String bungeeClass, String velocityClass) {
        return ReflUtil.getClass(isBukkit() ? bukkitClass : (isVelocity() ? velocityClass : bungeeClass));
    }

    public static Class<?> getUniversalClass(Class<?> bukkitClass, Class<?> bungeeClass, Class<?> velocityClass) {
        return isBukkit() ? bukkitClass : (isVelocity() ? velocityClass : bungeeClass);
    }

    private static boolean isClass(Object object, String bukkitClass, String bungeeClass, String velocityClass) {
        if (object == null) return false;
        List<String> classesAndSuperClasses = Arrays.stream(ReflUtil.getClassAndSuperClasses(object.getClass()))
                .map(Class::getSimpleName)
                .collect(Collectors.toList());
        if (isBukkit()) return classesAndSuperClasses.contains(bukkitClass);
        else if (isVelocity()) return classesAndSuperClasses.contains(velocityClass);
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

    public static boolean isFolia() {
        if (!isBukkit()) return false;
        ReflObject<?> bukkit = getBukkit();
        bukkit.setShowErrors(false);
        String version = bukkit.getMethodObject("getVersion");
        return version != null && version.toLowerCase().contains("folia");
    }

    public static boolean isBungeeCord() {
        try {
            Class.forName("net.md_5.bungee.api.plugin.Plugin");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isVelocity() {
        try {
            Class.forName("com.velocitypowered.api.plugin.Plugin");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getPlayerClass() {
        return isBukkit() ? bukkitPlayerClass : isVelocity() ? velocityPlayerClass : bungeePlayerClass;
    }

    public static Collection<?> getPlugins() {
        Collection<?> plugins;
        Object tmp = getPluginManager().getMethodObject("getPlugins");
        if (tmp == null) return null;
        if (tmp.getClass().isArray()) plugins = Arrays.asList((Object[]) tmp);
        else plugins = (Collection<?>) tmp;
        return plugins;
    }

    public static <P> P getPluginFromClass(Class<?> aClass) {
        if (aClass == null) return null;
        if (isBukkit()) {
            ReflObject<?> javaPlugin = new ReflObject<>("org.bukkit.plugin.java.JavaPlugin", false);
            javaPlugin.setShowErrors(false);
            return javaPlugin.getMethodObject("getProvidingPlugin", aClass);
        } else if (isVelocity()) {
            Collection<?> plugins = getPlugins();
            if (plugins != null)
                for (Object plugin : plugins)
                    if (plugin.getClass().getClassLoader().equals(aClass.getClassLoader())) return (P) plugin;
        } else {
            ReflObject<?> classLoader = new ReflObject<>(aClass.getClassLoader());
            classLoader.setShowErrors(false);
            return classLoader.getFieldObject("plugin");
        }
        return null;
    }
}