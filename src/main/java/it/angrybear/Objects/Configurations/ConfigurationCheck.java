package it.angrybear.Objects.Configurations;

import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationCheck {
    private final List<String> missingEntries;
    private final List<InvalidType> invalidTypes;

    public ConfigurationCheck(IBearPlugin<?> plugin, File dataFolder, String configName, String internalConfig) throws IOException {
        File configFile = new File(dataFolder, configName);
        this.missingEntries = new ArrayList<>();
        this.invalidTypes = new ArrayList<>();
        if (!configFile.exists()) return;
        File tmpFile = new File(dataFolder, "tmp.yml");
        if (tmpFile.exists()) FileUtils.deleteFile(tmpFile);
        FileUtils.createNewFile(tmpFile);
        FileUtils.writeToFile(tmpFile, plugin.getResource(internalConfig));

        Configuration config1 = ConfigUtils.loadConfiguration(configFile);
        Configuration config2 = ConfigUtils.loadConfiguration(tmpFile);

        config2.getKeys(true).forEach(entry -> {
            if (!config1.contains(entry)) this.missingEntries.add(entry);
            else {
                Class<?> obj1 = config1.get(entry).getClass();
                Class<?> obj2 = config2.get(entry).getClass();
                if (!obj1.equals(obj2)) this.invalidTypes.add(new InvalidType(entry, obj2, obj1));
            }
        });

        FileUtils.deleteFile(tmpFile);
    }

    public boolean isInvalid(String key) {
        return this.invalidTypes.stream().map(InvalidType::getEntry).anyMatch(e -> e.equalsIgnoreCase(key));
    }

    public List<String> getMissingEntries() {
        return missingEntries;
    }

    public List<InvalidType> getInvalidTypes() {
        return invalidTypes;
    }

    public boolean isEmpty() {
        return missingEntries.isEmpty() && invalidTypes.isEmpty();
    }
}