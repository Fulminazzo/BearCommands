package it.angrybear.Objects;

import it.angrybear.Annotations.PreventSaving;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.YamlElementException;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Utils.FileUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Savable<P extends IBearPlugin<?>> extends Printable {
    @PreventSaving
    private final P plugin;
    @PreventSaving
    protected final File file;

    protected Savable(P plugin, File file) {
        this.file = file;
        this.plugin = plugin;
    }

    public void reload() {
        if (file == null || !file.exists()) return;
        load(getPlayerConfiguration());
    }

    public void save(String... fields) throws IOException {
        if (file == null) return;
        if (!file.exists()) FileUtils.createNewFile(file);
        Configuration playerConfiguration = getPlayerConfiguration();
        dump(playerConfiguration, fields);
        ConfigUtils.saveConfig(playerConfiguration, file);
    }

    public Configuration getPlayerConfiguration() {
        return ConfigUtils.loadConfiguration(file);
    }

    public void load(Configuration configurationSection) {
        getYamlFields().stream()
                .filter(y -> new ReflObject<>(this).getFields().stream().map(Field::getName).anyMatch(f -> f.equals(y.getFieldName())))
                .forEach(f -> {
                    try {
                        f.setObject(configurationSection, this);
                    } catch (YamlElementException e) {
                        IBearPlugin.logWarning(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    public void dump(Configuration configurationSection, String... fields) {
        List<String> fieldsFilter = Arrays.stream(fields).filter(Objects::nonNull).collect(Collectors.toList());
        for (YamlField yamlField : getYamlFields())
            try {
                if (!fieldsFilter.isEmpty() && fieldsFilter.stream().noneMatch(f -> yamlField.getFieldName().equalsIgnoreCase(f)))
                    continue;
                yamlField.save(configurationSection);
            } catch (YamlElementException e) {
                IBearPlugin.logWarning(e.getMessage());
            }
    }

    protected List<YamlField> getYamlFields() {
        return new ReflObject<>(this).getFields().stream()
                .filter(f -> !f.isAnnotationPresent(PreventSaving.class))
                .map(f -> {
                    try {
                        return new YamlField(plugin, f, this);
                    } catch (IllegalAccessException e) {
                        IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                                "%task%", String.format("saving object %s", this),
                                "%error%", e.getMessage());
                        return null;
                    } catch (YamlElementException e) {
                        IBearPlugin.logWarning(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public P getPlugin() {
        return plugin;
    }
}