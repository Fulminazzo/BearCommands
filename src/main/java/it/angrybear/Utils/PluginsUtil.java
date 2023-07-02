package it.angrybear.Utils;

import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;

public class PluginsUtil {
    private static final String paperClass = "io.papermc.paper.plugin.manager.PaperPluginManagerImpl";

    public static boolean isPluginEnabled(String pluginName) {
        return isPluginEnabled(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    public static boolean isPluginEnabled(Plugin plugin) {
        return plugin != null && plugin.isEnabled();
    }

    public static void reloadPlugin(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException, IllegalAccessException {
        if (plugin == null) return;
        unloadPlugin(plugin);
        loadPlugin((JavaPlugin) plugin);
    }

    public static void loadPlugin(String pluginName) throws InvalidPluginException, InvalidDescriptionException {
        loadPlugin((JavaPlugin) Bukkit.getPluginManager().getPlugin(pluginName));
    }

    public static void loadPlugin(JavaPlugin javaPlugin) throws InvalidPluginException, InvalidDescriptionException {
        if (javaPlugin == null || javaPlugin.isEnabled()) return;
        ReflObject<File> file = new ReflObject<>(javaPlugin, JavaPlugin.class).callMethod("getFile");
        loadPlugin(file.getObject());
    }

    public static void loadPlugin(File jarFile) throws InvalidPluginException, InvalidDescriptionException {
        Plugin plugin = Bukkit.getPluginManager().loadPlugin(jarFile);
        if (plugin != null) Bukkit.getPluginManager().enablePlugin(plugin);
        CommandUtils.syncCommands();
    }

    public static void unloadPlugin(Plugin plugin) throws IllegalAccessException {
        if (plugin == null) return;
        String pluginName = plugin.getName();
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (plugin.isEnabled()) pluginManager.disablePlugin(plugin);
        ReflObject<PluginManager> reflPluginManager = new ReflObject<>(pluginManager);
        reflPluginManager.setShowErrors(false);

        SimpleCommandMap commandMap = reflPluginManager.getFieldObject("commandMap");
        List<Plugin> plugins = reflPluginManager.getFieldObject("plugins");
        Map<String, Plugin> names = reflPluginManager.getFieldObject("lookupNames");
        Map<Event, SortedSet<RegisteredListener>> listeners = reflPluginManager.getFieldObject("listeners");
        Map<String, Command> commands = new ReflObject<>(commandMap).getFieldObject("knownCommands");

        pluginManager.disablePlugin(plugin);

        if (listeners != null)
            for (SortedSet<RegisteredListener> set : listeners.values())
                set.removeIf(value -> value.getPlugin() == plugin);

        if (commandMap != null)
            for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Command> entry = it.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        it.remove();
                    }
                } else try {
                    Field pluginField = Arrays.stream(entry.getValue().getClass().getDeclaredFields())
                            .filter(field -> Plugin.class.isAssignableFrom(field.getType()))
                            .findFirst().orElse(null);
                    if (pluginField != null) {
                        Plugin owningPlugin;
                        pluginField.setAccessible(true);
                        owningPlugin = (Plugin) pluginField.get(entry.getValue());
                        if (owningPlugin.getName().equalsIgnoreCase(plugin.getName())) {
                            entry.getValue().unregister(commandMap);
                            it.remove();
                        }
                    }
                } catch (IllegalStateException e) {
                    if (e.getMessage().equalsIgnoreCase("zip file closed")) {
                        entry.getValue().unregister(commandMap);
                        it.remove();
                    } else throw e;
                }
            }

        if (plugins != null) plugins.remove(plugin);
        if (names != null) names.remove(pluginName);

        // Attempt to close the classloader to unlock any handles on the plugin's jar file.
        ClassLoader classLoader = plugin.getClass().getClassLoader();

        if (classLoader instanceof URLClassLoader) {
            ReflObject<URLClassLoader> classLoaderReflObject = new ReflObject<>((URLClassLoader) classLoader);
            classLoaderReflObject.setField("plugin", null);
            classLoaderReflObject.setField("pluginInit", null);
            classLoaderReflObject.callMethod("close");
        }

        if (isPaper()) {
            ReflObject<?> paperPluginManager = new ReflObject<>(paperClass, false).callMethod("getInstance");
            ReflObject<?> instanceManager = paperPluginManager.obtainField("instanceManager");
            Map<String, Object> lookupNames = instanceManager.getFieldObject("lookupNames");
            instanceManager.callMethod("disablePlugin", plugin);
            lookupNames.remove(pluginName.toLowerCase());
            List<Plugin> pluginList = instanceManager.getFieldObject("plugins");
            pluginList.remove(plugin);
        }

        // Will not work on processes started with the -XX:+DisableExplicitGC flag, but lets try it anyway.
        // This tries to get around the issue where Windows refuses to unlock jar files that were previously loaded into the JVM.
        System.gc();
    }

    public static boolean isPaper() {
        try {
            Class.forName(paperClass);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Plugin getPluginFromClass(Class<?> aClass) {
        if (aClass == null) return null;
        return getPluginFromClass(aClass.getCanonicalName());
    }

    public static Plugin getPluginFromClass(String className, Class<?>... superClasses) {
        if (className == null) return null;
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(p -> Arrays.stream(superClasses).allMatch(c -> Arrays.asList(ReflUtil.getClassAndSuperClasses(p.getClass())).contains(c)))
                .min(Comparator.comparing(p -> Math.abs(p.getClass().getCanonicalName().compareTo(className))))
                .orElse(null);
    }
}