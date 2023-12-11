package it.angrybear.objects.configurations;

import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.utils.ConfigUtils;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;
import it.fulminazzo.yamlparser.utils.JarUtils;

import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Unlike {@link ConfigManager}, MultiConfigManager handles multiple configurations.
 * Say you have a folder with many files (config/tmp1.yml, config/tmp2.yml, config/tmp3.yml).
 * Those will all be loaded from this class in a map.
 */
public class MultiConfigManager extends ConfigManager {
    protected final HashMap<String, FileConfiguration> configurations;

    public MultiConfigManager(String folderName) {
        super(folderName);
        this.configurations = new HashMap<>();
    }

    /**
     * Checks the jar for the directory name (configName).
     * Then, load every YAML file found inside it and, if necessary, download it in the plugin data folder.
     *
     * @param plugin the plugin
     * @throws Exception the exception
     */
    @Override
    public void load(IBearPlugin plugin) throws Exception {
        configurations.clear();
        JarFile jarFile = JarUtils.getJarFile(MultiConfigManager.class);
        Iterator<JarEntry> entries = jarFile.entries().asIterator();
        while (entries.hasNext()) {
            JarEntry entry = entries.next();
            String entryName = entry.getName();
            if (!entryName.startsWith(configName)) continue;
            if (!entryName.endsWith(".yml")) continue;
            String[] tmp = entryName.split("/");
            if (tmp.length > 2) continue;
            FileConfiguration config = ConfigUtils.loadConfiguration(plugin, entryName, loadPolicy, getIgnoredKeys());
            String fileName = tmp[1];
            fileName = fileName.substring(0, fileName.length() - ".yml".length());
            configurations.put(fileName.toLowerCase(), config);
        }
    }

    @Override
    public FileConfiguration getConfig() {
        List<FileConfiguration> configurations = getConfigurations();
        return configurations.isEmpty() ? null : configurations.get(0);
    }

    /**
     * Gets config.
     *
     * @param configName the config name
     * @return the config
     */
    public FileConfiguration getConfig(String configName) {
        return configName == null ? null : configurations.get(configName.toLowerCase());
    }

    /**
     * Gets configurations.
     *
     * @return the configurations
     */
    public List<FileConfiguration> getConfigurations() {
        return new ArrayList<>(configurations.values());
    }

    @Override
    public String toString() {
        return String.format("%s {configDirName: %s, loadPolicy: %s, configs: %s}",
                getClass().getSimpleName(), configName, loadPolicy, configurations.keySet());
    }
}
