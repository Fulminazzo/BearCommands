package it.angrybear;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Objects.BearPlayer;
import it.angrybear.Utils.JarUtils;
import it.angrybear.Utils.PluginsUtil;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BearCommandsPlugin<OnlinePlayer extends BearPlayer, OfflinePlayer extends BearPlayer> extends BearPlugin<OnlinePlayer, OfflinePlayer> implements Listener {
    private BukkitTask startTask;
    public List<String> loadedPlugins;

    public BearCommandsPlugin() {
        new ReflObject<>(getDescription()).setField("loadBefore", Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(this::isDependingPlugin)
                .map(Plugin::getName)
                .collect(Collectors.toList()));
        disableOtherBearCommands();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (!isEnabled()) return;
        loadedPlugins = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, this);

        startTask = Bukkit.getScheduler().runTaskLater(this, () -> {
            new ArrayList<>(Arrays.asList(Bukkit.getPluginManager().getPlugins())).stream()
                    .filter(this::isDependingPlugin)
                    .filter(p -> loadedPlugins.stream().noneMatch(s -> p.getName().equalsIgnoreCase(s)))
                    .sorted(Comparator.comparing(p -> p.getDescription().getDepend().size() + p.getDescription().getSoftDepend().size()))
                    .forEach(p -> {
                        try {PluginsUtil.reloadPlugin(p);}
                        catch (Exception e) {
                            logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                                    "%task%", String.format("reloading plugin %s", p.getName()),
                                    "%error%", e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
                        }
                    });
            HandlerList.unregisterAll((Listener) this);
            try {
                reloadPluginsInFolder(getDataFolder().getParentFile());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, 20 * 3);

        //TODO: Soon to be BearCommandsBungee.
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "staffcore:channel");

        Arrays.stream(BearLoggingMessage.ENABLING.getMessage(
                        "%plugin-name%", getName(), "%plugin-version%", getDescription().getVersion())
                .split("\n")).forEach(BearPlugin::sendConsole);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (startTask != null) startTask.cancel();
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(this::isDependingPlugin)
                .forEach(p -> Bukkit.getPluginManager().disablePlugin(p));
        Arrays.stream(BearLoggingMessage.DISABLING.getMessage(
                        "%plugin-name%", getName(), "%plugin-version%", getDescription().getVersion())
                .split("\n")).forEach(BearPlugin::sendConsole);
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin plugin = event.getPlugin();
        if (!isDependingPlugin(plugin)) return;
        loadedPlugins.remove(plugin.getName());
        loadedPlugins.add(plugin.getName());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        loadedPlugins.remove(event.getPlugin().getName());
    }

    public boolean isDependingPlugin(String pluginName) {
        return pluginName != null && isDependingPlugin(Bukkit.getPluginManager().getPlugin(pluginName));
    }

    public boolean isDependingPlugin(Plugin plugin) {
        if (plugin == null) return false;
        return isDependingPlugin(getPluginDependencies(plugin));
    }

    public boolean isDependingPlugin(List<String> pluginDependencies) {
        if (pluginDependencies == null) return false;
        List<String> expectedDependencies = getExpectedDependencies();
        return pluginDependencies.stream().anyMatch(d -> expectedDependencies.stream().anyMatch(e -> e.equalsIgnoreCase(d)));
    }

    public static List<String> getPluginDependencies(Plugin plugin) {
        if (plugin == null) return new ArrayList<>();
        PluginDescriptionFile description = plugin.getDescription();
        return Stream.concat(description.getDepend().stream(), description.getSoftDepend().stream())
                .distinct().collect(Collectors.toList());
    }

    public List<String> getExpectedDependencies() {
        List<String> expectedDependencies = new ArrayList<>();
        Class<?> aClass = this.getClass();
        while (aClass != JavaPlugin.class && aClass != BearCommandsPlugin.class.getSuperclass()) {
            if (aClass.equals(Object.class)) continue;
            String name = aClass.getSimpleName();
            if (expectedDependencies.contains(name)) continue;
            expectedDependencies.add(name);
            if (name.endsWith("Plugin")) name = name.substring(0, name.length() - "Plugin".length());
            expectedDependencies.add(name);
            aClass = aClass.getSuperclass();
        }
        return expectedDependencies;
    }

    private void disableOtherBearCommands() {
        String className = BearCommandsPlugin.class.getCanonicalName();
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(p -> !p.equals(this))
                .filter(Plugin::isEnabled)
                .filter(p -> {
                    try {
                        Class<?> mainClass = Class.forName(p.getDescription().getMain());
                        return Arrays.stream(ReflUtil.getClassAndSuperClasses(mainClass))
                                .map(Class::getName)
                                .anyMatch(c -> c.equals(className));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .forEach(p -> {
                    String logMessage = String.format("[%s] %s", getName(),
                            BearLoggingMessage.DISABLING_CONFLICT_PLUGIN.getMessage("%plugin-name%", p.getName()));
                    logInfo(logMessage);
                    try {
                        PluginsUtil.unloadPlugin(p);
                    } catch (IllegalAccessException e) {
                        logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                                "%task%", String.format("disabling plugin %s", p.getName()),
                                "%error%", e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
                    }
                });
    }

    private void reloadPluginsInFolder(File folder) throws FileNotFoundException {
        if (folder == null || !folder.isDirectory())
            throw new FileNotFoundException(String.format("%s (Not a directory)", folder == null ? "null" : folder.getAbsolutePath()));
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) continue;
            InputStream inputStream = JarUtils.getJarFile(file, "plugin.yml");
            if (inputStream == null) continue;
            try {
                PluginDescriptionFile pluginDescription = new PluginDescriptionFile(inputStream);
                inputStream.close();
                String name = pluginDescription.getName();
                Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
                if (plugin != null && plugin.isEnabled()) continue;
                else if (plugin != null && !plugin.isEnabled()) {
                    if (!isDependingPlugin(plugin)) continue;
                    try {
                        Bukkit.getPluginManager().enablePlugin(plugin);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                List<String> pluginDependencies = Stream.concat(pluginDescription.getDepend().stream(), pluginDescription.getSoftDepend().stream())
                        .distinct().collect(Collectors.toList());
                if (!isDependingPlugin(pluginDependencies)) continue;
                PluginsUtil.loadPlugin(file);
            } catch (IOException | InvalidDescriptionException | InvalidPluginException ignored) {}
        }
    }
}