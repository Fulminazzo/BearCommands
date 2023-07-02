package it.angrybear.Utils;

import it.angrybear.Objects.ConfigurationCheck;
import it.angrybear.Enums.BearLoggingMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class ConfigUtils {

    public static FileConfiguration loadConfiguration(JavaPlugin plugin, File dataFolder, String configName) throws IOException {
        return loadConfiguration(plugin, dataFolder, configName, configName, true);
    }

    public static FileConfiguration loadConfiguration(JavaPlugin plugin, File dataFolder, String configName, boolean autoFix) throws IOException {
        return loadConfiguration(plugin, dataFolder, configName, configName, autoFix);
    }

    public static FileConfiguration loadConfiguration(JavaPlugin plugin, File dataFolder, String configName, String resultFile) throws IOException {
        return loadConfiguration(plugin, dataFolder, configName, resultFile, true);
    }

    public static FileConfiguration loadConfiguration(JavaPlugin plugin, File dataFolder, String configName, String resultFile, boolean autoFix) throws IOException {
        File configFile = new File(dataFolder, resultFile);
        if (!configFile.exists()) {
            FileUtils.createNewFile(configFile);
            FileUtils.writeToFile(configFile, plugin.getResource(configName));
        }
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationCheck configurationCheck = checkConfiguration(plugin, dataFolder, configName, resultFile);
        if (!autoFix || configurationCheck.isEmpty()) return fileConfiguration;

        FileUtils.renameFile(configFile, new File(dataFolder, String.format("broken-%s-", new Date().getTime()) + resultFile));
        File newConfigFile = new File(dataFolder, resultFile);
        FileUtils.createNewFile(newConfigFile);
        FileUtils.writeToFile(newConfigFile, plugin.getResource(configName));

        FileConfiguration newfileConfiguration = YamlConfiguration.loadConfiguration(newConfigFile);
        TreeMap<String, Object> values = new TreeMap<>();
        newfileConfiguration.getKeys(true).forEach(key -> values.put(key, newfileConfiguration.get(key)));
        FileUtils.copyFile(configFile, newConfigFile);

        for (String key : values.keySet())
            if (!configurationCheck.getMissingEntries().contains(key) && !configurationCheck.isInvalid(key))
                newfileConfiguration.set(key, fileConfiguration.get(key));
            else newfileConfiguration.set(key, values.get(key));

        saveConfig(newfileConfiguration, newConfigFile);
        return newfileConfiguration;
    }

    public static ConfigurationCheck checkConfiguration(JavaPlugin plugin, File dataFolder, String configName) throws IOException {
        return checkConfiguration(plugin, dataFolder, configName, configName);
    }

    public static ConfigurationCheck checkConfiguration(JavaPlugin plugin, File dataFolder, String configName, String resultFile) throws IOException {
        return new ConfigurationCheck(plugin, dataFolder, configName, resultFile);
    }

    public static void saveConfig(FileConfiguration config, File file) throws IOException {
        if (!file.exists()) FileUtils.createNewFile(file);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new IOException(BearLoggingMessage.SAVE_CONFIG_ERROR.getMessage()
                    .replace("%config%", config.getName())
                    .replace("%file%", file.getName())
                    .replace("%error%", e.getMessage()));
        }
    }
}
