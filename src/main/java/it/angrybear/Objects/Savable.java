package it.angrybear.Objects;

import it.angrybear.Annotations.PreventSaving;
import it.angrybear.BearPlugin;
import it.angrybear.Commands.BearCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.YamlElementException;
import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Utils.FileUtils;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Savable extends Printable {
    @PreventSaving
    private final BearPlugin<?, ?> plugin;
    @PreventSaving
    protected final File file;

    protected Savable(BearPlugin<?, ?> plugin, File file) {
        this.file = file;
        this.plugin = plugin;
    }

    public void reload() {
        if (file == null || !file.exists()) return;
        FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(file);
        load(playerConfiguration);
    }

    public void save(String... fields) throws IOException {
        if (file == null) return;
        if (!file.exists()) FileUtils.createNewFile(file);
        FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(file);
        dump(playerConfiguration, fields);
        ConfigUtils.saveConfig(playerConfiguration, file);
    }

    public void load(ConfigurationSection configurationSection) {
        getYamlFields().stream()
                .filter(y -> Arrays.stream(BearCommand.class.getDeclaredFields()).map(Field::getName).noneMatch(f -> f.equals(y.getFieldName())))
                .forEach(f -> {
                    try {
                        f.setObject(configurationSection, this);
                    } catch (YamlElementException e) {
                        BearPlugin.logWarning(e.getMessage());
                    }
                });
    }

    public void dump(ConfigurationSection configurationSection, String... fields) {
        List<String> fieldsFilter = Arrays.stream(fields).filter(Objects::nonNull).collect(Collectors.toList());
        for (YamlField yamlField : getYamlFields())
            try {
                if (!fieldsFilter.isEmpty() && fieldsFilter.stream().noneMatch(f -> yamlField.getFieldName().equalsIgnoreCase(f)))
                    continue;
                yamlField.save(configurationSection);
            } catch (YamlElementException e) {
                BearPlugin.logWarning(e.getMessage());
            }
    }

    private List<YamlField> getYamlFields() {
        return Arrays.stream(ReflUtil.getDeclaredFields(this.getClass()))
                .filter(f -> !f.isAnnotationPresent(PreventSaving.class))
                .map(f -> {
                    try {
                        return new YamlField(plugin, f, this);
                    } catch (IllegalAccessException e) {
                        BearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                                "%task%", String.format("saving object %s", this),
                                "%error%", e.getMessage());
                        return null;
                    } catch (YamlElementException e) {
                        BearPlugin.logWarning(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}