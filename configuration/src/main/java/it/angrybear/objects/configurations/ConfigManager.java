package it.angrybear.objects.configurations;

import it.angrybear.enums.LoadPolicy;
import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.utils.ConfigUtils;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A wrapper responsible for handling FileConfigurations loading.
 * Uses {@link LoadPolicy} to check and correct any mistakes.
 */
@Getter
public class ConfigManager {
    protected final String configName;
    protected final List<String> ignoredKeys;
    @Setter
    protected LoadPolicy loadPolicy;
    private FileConfiguration config;

    public ConfigManager(String configName) {
        this.configName = configName;
        this.loadPolicy = LoadPolicy.WARN_AND_CORRECT;
        this.ignoredKeys = new ArrayList<>();
    }

    /**
     * Loads the configuration.
     *
     * @param plugin the plugin
     * @throws Exception the exception
     */
    public void load(IBearPlugin plugin) throws Exception {
        this.config = ConfigUtils.loadConfiguration(plugin, configName, loadPolicy, getIgnoredKeys());
    }

    /**
     * Add ignored keys.
     *
     * @param keys the keys
     */
    public void addIgnoredKeys(String... keys) {
        if (keys != null) {
            removeIgnoredKeys(keys);
            this.ignoredKeys.addAll(Arrays.stream(keys).map(String::toLowerCase).distinct().collect(Collectors.toList()));
        }
    }

    /**
     * Remove ignored keys.
     *
     * @param keys the keys
     */
    public void removeIgnoredKeys(String... keys) {
        if (keys != null) Arrays.stream(keys).forEach(k -> ignoredKeys.removeIf(s -> s.equalsIgnoreCase(k)));
    }

    /**
     * Get ignored keys string.
     *
     * @return the array of keys
     */
    public String[] getIgnoredKeys() {
        return ignoredKeys.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return String.format("%s {configName: %s.yml, loadPolicy: %s}", getClass().getSimpleName(), configName, loadPolicy);
    }
}