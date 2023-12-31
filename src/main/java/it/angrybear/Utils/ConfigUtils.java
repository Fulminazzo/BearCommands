package it.angrybear.Utils;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.Configurations.ConfigurationCheck;
import it.angrybear.Velocity.Objects.Configurations.VelocityConfiguration;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    /**
     * Loads a YAML file from the plugin/<plugin-name> folder into memory.
     * If the file does not exist, it will be created by copying the content from
     * the resources of the .jar file.
     * @param plugin: an instance of the main plugin class.
     * @param dataFolder: the plugin/<plugin-name> folder.
     * @param configName: the name of the YAML file in the resources.
     * @param resultFile: the name of the YAML file in the plugin/<plugin-name> folder.
     * @param autoFix: if true, this option will trigger the automatic fix if the file requires it.
     *
     * @return an instance of Configuration, wrapping the loaded configuration file.
     */
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

    public static Configuration loadConfiguration(File configFile) {
        if (ServerUtils.isBukkit()) {
            ReflObject<?> yamlConfiguration = new ReflObject<>("org.bukkit.configuration.file.YamlConfiguration", false);
            return new Configuration(yamlConfiguration.getMethodObject("loadConfiguration", configFile));
        } else if (ServerUtils.isVelocity()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(configFile);
                Yaml yaml = new Yaml();
                Map<String, Object> data = (Map<String, Object>) yaml.load(fileInputStream);
                if (data == null) data = new HashMap<>();
                fileInputStream.close();
                return new Configuration(new VelocityConfiguration(data));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            ReflObject<?> configurationProvider = ServerUtils.getConfigurationProvider();
            return new Configuration(configurationProvider.getMethodObject("load", configFile));
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
            } else if (ServerUtils.isVelocity()) {
                Yaml yaml = new Yaml();
                yaml.dump(cfg.getInnerConfigurationSection(), new BufferedWriter(new FileWriter(file)));
            } else {
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
