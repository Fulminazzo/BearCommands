package it.angrybear.utils;

import it.angrybear.enums.BearLoggingMessage;
import it.angrybear.enums.LoadPolicy;
import it.angrybear.interfaces.IBearPlugin;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;
import it.fulminazzo.yamlparser.objects.configurations.checkers.ConfigurationChecker;
import it.fulminazzo.yamlparser.objects.configurations.checkers.ConfigurationInvalidType;
import it.fulminazzo.yamlparser.utils.FileUtils;
import it.fulminazzo.yamlparser.utils.JarUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.stream.Stream;

public class ConfigUtils {

    /**
     * Loads a YAML file as FileConfiguration.
     * Depending on {@link LoadPolicy}, checks and corrects any mistakes.
     * During this process, ignores every key specified in ignoredKeys.
     *
     * @param plugin      the plugin
     * @param configName  the config name
     * @param loadPolicy  the load policy
     * @param ignoredKeys the ignored keys
     * @return the file configuration
     * @throws IOException any exception
     */
    public static FileConfiguration loadConfiguration(IBearPlugin plugin, String configName,
                                                      LoadPolicy loadPolicy, String... ignoredKeys) throws IOException {
        if (configName == null) return null;
        if (!configName.endsWith(".yml")) {
            if (configName.contains(".")) return null;
            configName = configName + ".yml";
        }
        File configFile = new File(plugin.getDataFolder(), configName);

        if (!configFile.exists()) {
            FileUtils.createNewFile(configFile);
            InputStream resource = JarUtils.getResource(configName);
            if (resource == null) return new FileConfiguration(configFile);
            FileUtils.writeToFile(configFile, resource);
            return new FileConfiguration(configFile);
        }

        FileConfiguration currentConfig = new FileConfiguration(configFile);
        InputStream resource = JarUtils.getResource(configName);
        if (resource == null) return currentConfig;
        FileConfiguration realConfig = new FileConfiguration(resource);
        return checkConfigurations(plugin, configName, currentConfig, realConfig, loadPolicy, ignoredKeys);
    }

    /**
     * Checks two FileConfigurations.
     * Depending on {@link LoadPolicy}, checks and corrects any mistakes.
     * During this process, ignores every key specified in ignoredKeys.
     *
     * @param plugin        the plugin
     * @param configName    the config name
     * @param currentConfig the current config
     * @param realConfig    the reference config
     * @param loadPolicy    the load policy
     * @param ignoredKeys   the ignored keys
     * @return the corrected file configuration
     * @throws IOException any exception
     */
    public static FileConfiguration checkConfigurations(IBearPlugin plugin, String configName,
                                                        FileConfiguration currentConfig, FileConfiguration realConfig,
                                                        LoadPolicy loadPolicy, String... ignoredKeys) throws IOException {
        if (loadPolicy == null || loadPolicy.equals(LoadPolicy.IGNORE)) return currentConfig;

        ConfigurationChecker checker = realConfig.compare(currentConfig, ignoredKeys);
        if (checker.isEmpty()) return currentConfig;

        File configFile = new File(plugin.getDataFolder(), configName);
        if (loadPolicy.name().startsWith(LoadPolicy.WARN.name())) {
            plugin.logWarning(BearLoggingMessage.CONFIG_ERROR.getMessage("%config%", configName));
            if (!checker.getMissingKeys().isEmpty()) {
                plugin.logWarning(BearLoggingMessage.MISSING_KEYS.getMessage());
                checker.getMissingKeys().forEach(k -> plugin.logWarning("- " + k));
            }
            if (!checker.getInvalidValues().isEmpty()) {
                plugin.logWarning(BearLoggingMessage.INVALID_VALUES.getMessage());
                checker.getInvalidValues().stream()
                        .map(v -> "- " + BearLoggingMessage.INVALID_TYPE.getMessage(
                                "%entry%", v.getEntry(),
                                "%expected%", v.getExpectedType().getSimpleName(),
                                "%received%", v.getReceivedType().getSimpleName()))
                        .forEach(plugin::logWarning);
            }

            if (loadPolicy.equals(LoadPolicy.WARN_AND_CORRECT)) {
                plugin.logWarning(BearLoggingMessage.AUTO_CORRECT.getMessage());
                String backupFile = configName.replace(".yml", String.format("-%s.yml", new Date().getTime()));
                FileUtils.copyFile(configFile, new File(plugin.getDataFolder(), backupFile));
                Stream.concat(checker.getMissingKeys().stream(),
                                checker.getInvalidValues().stream().map(ConfigurationInvalidType::getEntry))
                        .forEach(key -> currentConfig.set(key, realConfig.getObject(key)));
                currentConfig.save();
            }
        }
        return currentConfig;
    }
}