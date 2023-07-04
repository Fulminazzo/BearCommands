package it.angrybear.Utils;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.Configurations.ConfigurationCheck;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

public class ConfigUtils {

    public static Configuration loadConfiguration(IBearPlugin<?> plugin, File dataFolder, String configName) throws IOException {
        return loadConfiguration(plugin, dataFolder, configName, configName, true);
    }

    public static Configuration loadConfiguration(IBearPlugin<?> plugin, File dataFolder, String configName, boolean autoFix) throws IOException {
        return loadConfiguration(plugin, dataFolder, configName, configName, autoFix);
    }

    public static Configuration loadConfiguration(IBearPlugin<?> plugin, File dataFolder, String configName, String resultFile) throws IOException {
        return loadConfiguration(plugin, dataFolder, configName, resultFile, true);
    }

    public static Configuration loadConfiguration(IBearPlugin<?> plugin, File dataFolder, String configName, String resultFile, boolean autoFix) throws IOException {
        File configFile = new File(dataFolder, resultFile);
        if (!configFile.exists()) {
            FileUtils.createNewFile(configFile);
            FileUtils.writeToFile(configFile, plugin.getResource(configName));
        }
        Configuration fileConfiguration = loadConfiguration(configFile);
        ConfigurationCheck configurationCheck = checkConfiguration(plugin, dataFolder, configName, resultFile);
        if (!autoFix || configurationCheck.isEmpty()) return fileConfiguration;

        FileUtils.renameFile(configFile, new File(dataFolder, String.format("broken-%s-", new Date().getTime()) + resultFile));
        File newConfigFile = new File(dataFolder, resultFile);
        FileUtils.createNewFile(newConfigFile);
        FileUtils.writeToFile(newConfigFile, plugin.getResource(configName));

        Configuration newFileConfiguration = loadConfiguration(newConfigFile);
        TreeMap<String, Object> values = new TreeMap<>();
        newFileConfiguration.getKeys(true).forEach(key -> values.put(key, newFileConfiguration.get(key)));
        FileUtils.copyFile(configFile, newConfigFile);

        for (String key : values.keySet())
            if (!configurationCheck.getMissingEntries().contains(key) && !configurationCheck.isInvalid(key))
                newFileConfiguration.set(key, fileConfiguration.get(key));
            else newFileConfiguration.set(key, values.get(key));

        saveConfig(newFileConfiguration, newConfigFile);
        return newFileConfiguration;
    }

    public static Configuration loadConfiguration(File file) {
        if (ServerUtils.isBukkit()) {
            ReflObject<?> yamlConfiguration = new ReflObject<>("org.bukkit.configuration.file.YamlConfiguration", false);
            return new Configuration(yamlConfiguration.getMethodObject("loadConfiguration", file));
        } else {
            ReflObject<?> configurationProvider = ServerUtils.getConfigurationProvider();
            return new Configuration(configurationProvider.getMethodObject("load", file));
        }
    }

    public static ConfigurationCheck checkConfiguration(IBearPlugin<?> plugin, File dataFolder, String configName) throws IOException {
        return checkConfiguration(plugin, dataFolder, configName, configName);
    }

    public static ConfigurationCheck checkConfiguration(IBearPlugin<?> plugin, File dataFolder, String configName, String resultFile) throws IOException {
        return new ConfigurationCheck(plugin, dataFolder, configName, resultFile);
    }

    public static void saveConfig(Configuration cfg, File file) throws IOException {
        ReflObject<?> config = new ReflObject<>(cfg.getInnerConfigurationSection());
        if (!file.exists()) FileUtils.createNewFile(file);
        String configName = ServerUtils.isBukkit() ? config.getMethodObject("getName") : "BungeeCordConfiguration";
        try {
            if (ServerUtils.isBukkit()) {
                // config.save(file);
                config.callMethod("save", file);
            }
            else {
                // ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
                ServerUtils.getConfigurationProvider().callMethod("save", config.getObject(), file);
            }
        } catch (Exception e) {
            throw new IOException(BearLoggingMessage.SAVE_CONFIG_ERROR.getMessage()
                    .replace("%config%", configName)
                    .replace("%file%", file.getName())
                    .replace("%error%", e.getMessage()));
        }
    }
}
