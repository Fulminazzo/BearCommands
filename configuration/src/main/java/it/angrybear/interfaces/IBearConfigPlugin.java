package it.angrybear.interfaces;

import it.angrybear.enums.Language;
import it.angrybear.objects.configurations.ConfigManager;
import it.angrybear.objects.configurations.LangConfigManager;
import it.angrybear.objects.configurations.MultiConfigManager;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;
import it.fulminazzo.yamlparser.utils.JarUtils;

import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * IBearConfigPlugin is responsible for automatically loading any
 * configuration specified in the JAR file.
 * It will handle autonomously the loading on storage, checking and fixing of these files.
 */
@SuppressWarnings({"unchecked"})
public interface IBearConfigPlugin extends IBearPlugin {

    @Override
    default void loadAll() throws Exception {
        IBearPlugin.super.loadAll();
        loadConfigurations();
    }

    /**
     * Load every configuration found in the JAR file.
     *
     * @throws Exception the exception
     */
    default void loadConfigurations() throws Exception {
        JarFile jarFile = JarUtils.getJarFile(IBearConfigPlugin.class);
        Iterator<JarEntry> entries = jarFile.entries().asIterator();
        while (entries.hasNext()) {
            JarEntry entry = entries.next();
            String name = entry.getName();
            if (!name.endsWith(".yml")) continue;
            name = name.substring(0, name.length() - ".yml".length());
            ConfigManager configManager = getConfigManager(name);
            if (configManager == null) return;
            configManager.load(this);
        }
    }

    /**
     * Gets config.yml (if present).
     *
     * @return the configuration
     */
    default FileConfiguration getConfiguration() {
        return getConfig("config");
    }

    /**
     * Gets messages.yml (if present, or the default language if multiple languages are used).
     *
     * @return the lang
     */
    default FileConfiguration getLang() {
        return getLang(null);
    }

    /**
     * Gets the corresponding lang.yml file.
     *
     * @param lang the language
     * @return the lang
     */
    default FileConfiguration getLang(Language lang) {
        ConfigManager configManager = getLangManager();
        if (lang != null && configManager instanceof LangConfigManager) {
            FileConfiguration config = ((LangConfigManager) configManager).getConfig(lang);
            if (config == null) config = configManager.getConfig();
            return config;
        } else return configManager.getConfig();
    }

    /**
     * Gets the languages manager.
     *
     * @return the lang manager
     */
    default ConfigManager getLangManager() {
        ConfigManager configManger = getConfigManager("messages", false);
        if (configManger == null) configManger = getConfigManager("lang", false);
        return configManger;
    }

    /**
     * Gets a general config.
     *
     * @param configName the config name
     * @return the config
     */
    default FileConfiguration getConfig(String configName) {
        ConfigManager configManager = getConfigManager(configName, false);
        return configManager == null ? null : configManager.getConfig();
    }

    /**
     * Gets a general config manager. If none is found, create it.
     *
     * @param <C>        the type of the config manager
     * @param configName the config name
     * @return the config manager
     */
    default <C extends ConfigManager> C getConfigManager(String configName) {
        return getConfigManager(configName, true);
    }

    /**
     * Gets a general config manager. If none is found, create it.
     *
     * @param <C>        the type of the config manager
     * @param configName the config name
     * @param create     if true, create if not found
     * @return the config manager
     */
    default <C extends ConfigManager> C getConfigManager(String configName, boolean create) {
        if (configName == null) return null;
        List<ConfigManager> configurations = getAllConfigurations();
        if (configurations == null) return null;
        ConfigManager configManager = configurations.stream()
                .filter(c -> c.getConfigName().equalsIgnoreCase(configName))
                .findFirst().orElse(null);
        if (configManager == null && create) {
            if (configName.contains("/")) {
                String folderName = configName.substring(0, configName.indexOf("/"));
                configManager = configurations.stream()
                        .filter(c -> c.getConfigName().equalsIgnoreCase(folderName))
                        .filter(c -> c instanceof MultiConfigManager)
                        .findFirst().orElse(null);
                if (configManager == null) {
                    configManager = (folderName.equalsIgnoreCase("messages") ||
                            folderName.equalsIgnoreCase("lang")) ?
                            new LangConfigManager(folderName) : new MultiConfigManager(folderName);
                    configurations.add(configManager);
                }
            } else {
                configManager = new ConfigManager(configName.toLowerCase());
                configurations.add(configManager);
            }
        }
        return (C) configManager;
    }

    /**
     * Gets all configurations.
     *
     * @return the all configurations
     */
    List<ConfigManager> getAllConfigurations();
}
