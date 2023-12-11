package it.angrybear.objects.configurations;

import it.angrybear.enums.Language;
import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.utils.ConfigUtils;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link MultiConfigManager} implementation responsible for handling
 * multi-language YAML files.
 * Uses {@link Language} to check any valid file.
 * To use this class, the directory name MUST be "messages".
 */
public class LangConfigManager extends MultiConfigManager {

    public LangConfigManager(String folderName) {
        super(folderName);
    }

    @Override
    public void load(IBearPlugin plugin) throws Exception {
        super.load(plugin);
        List<Language> languages = List.of(Language.values());

        FileConfiguration mainConfig = getConfig();

        for (String key : new ArrayList<>(this.configurations.keySet())) {
            FileConfiguration config = this.configurations.get(key);
            if (config == null || config.equals(mainConfig)) continue;
            String configName = this.configName + "/" + key + ".yml";
            if (languages.stream().noneMatch(a -> a.equals(key))) {
                this.configurations.remove(key);
                FileUtils.deleteFile(new File(plugin.getDataFolder(), configName));
            } else if (mainConfig != null) {
                config = ConfigUtils.checkConfigurations(plugin, configName,
                        config, mainConfig, loadPolicy, getIgnoredKeys());
                this.configurations.put(key, config);
            }
        }
    }

    /**
     * Gets config.
     *
     * @param language the language
     * @return the config
     */
    public FileConfiguration getConfig(Language language) {
        return language == null ? null : getConfig(language.name());
    }

    @Override
    public FileConfiguration getConfig() {
        FileConfiguration config = getConfig("en");
        if (config == null) config = super.getConfig();
        return config;
    }
}
