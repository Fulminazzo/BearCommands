package it.angrybear.objects;

import it.angrybear.annotations.PreventSaving;
import it.angrybear.enums.BearLoggingMessage;
import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.interfaces.functions.BiConsumerException;
import it.fulminazzo.reflectionutils.objects.ReflObject;
import it.fulminazzo.yamlparser.interfaces.IConfiguration;
import it.fulminazzo.yamlparser.objects.configurations.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * The Savable Class is a special class that allows
 * objects to be automatically loadable and savable.
 * Every NON-FINAL field will be loaded if the
 * &#064;PreventSaving annotation is not present.
 *
 * @param <P> the plugin
 */
public abstract class Savable<P extends IBearPlugin> extends Printable {
    @PreventSaving
    protected final P plugin;
    @PreventSaving
    protected final File file;

    public Savable(P plugin, IConfiguration configuration) {
        this(plugin, (File) null);
        load(configuration);
    }

    public Savable(P plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        reload();
    }

    public Savable(P plugin, File folder, String fileName) {
        this.plugin = plugin;
        if (folder == null) folder = plugin.getDataFolder();
        if (!fileName.endsWith(".yml")) fileName += ".yml";
        this.file = new File(folder, fileName);
    }

    /**
     * Reloads every field from the file.
     */
    public void reload() {
        if (file != null && file.isFile()) load(getConfiguration());
    }

    /**
     * Loads the specified fields from the configuration section.
     *
     * @param configurationSection the configuration section
     * @param fields               the fields
     */
    public void load(IConfiguration configurationSection, String... fields) {
        if (configurationSection == null) return;
        executeForFields((savableObject, field) -> {
            if (field == null || field.isAnnotationPresent(PreventSaving.class)) return;
            String fieldName = field.getName();
            String yamlName = FileUtils.formatStringToYaml(fieldName);
            savableObject.setField(fieldName, configurationSection.get(yamlName, field.getType()));
        }, BearLoggingMessage.LOAD_ERROR, fields);
    }

    /**
     * Dumps the specified fields in the configuration section.
     *
     * @param configurationSection the configuration section
     * @param fields               the fields
     */
    public void dump(IConfiguration configurationSection, String... fields) {
        if (configurationSection == null) return;
        executeForFields((savableObject, field) -> {
            if (field == null || field.isAnnotationPresent(PreventSaving.class)) return;
            String fieldName = field.getName();
            configurationSection.set(FileUtils.formatStringToYaml(fieldName),
                    savableObject.getFieldObject(fieldName));
            }, BearLoggingMessage.DUMP_ERROR, fields);
    }

    /**
     * Executes the given action for the specified fields.
     * If an error occurs, you can specify a message to send.
     *
     * @param action       the action
     * @param errorMessage the error message
     * @param fields       the fields
     */
    public void executeForFields(BiConsumerException<ReflObject<? extends Savable<P>>, Field> action,
                                 BearLoggingMessage errorMessage, String... fields) {
        final List<String> fieldNames = new ArrayList<>();
        if (fields != null) fieldNames.addAll(List.of(fields));
        final ReflObject<? extends Savable<P>> savableObject = new ReflObject<>(this);
        for (Field field : savableObject.getFields()) {
            String fieldName = field.getName();
            if (!fieldNames.isEmpty() && fieldNames.stream().noneMatch(f ->
                    f.equalsIgnoreCase(fieldName))) continue;
            try {
                action.accept(savableObject, field);
            } catch (Exception e) {
                if (errorMessage != null)
                    plugin.logWarning(errorMessage.getMessage(
                        "%field%", fieldName,
                        "%object%", this.getClass().getSimpleName(),
                        "%error%", e.getMessage()));
            }
        }
    }

    /**
     * Gets the file configuration.
     *
     * @return the configuration
     */
    public FileConfiguration getConfiguration() {
        return file == null ? null : new FileConfiguration(file);
    }

    /**
     * Saves the object.
     */
    public void save() {
        try {
            if (file == null) return;
            if (!file.exists()) FileUtils.createNewFile(file);
            FileConfiguration configuration = getConfiguration();
            dump(configuration);
            configuration.save();
        } catch (Exception e) {
            plugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                    "%task%", String.format("saving object %s", this.getClass().getSimpleName()),
                    "%error%", e.getMessage());
        }
    }
}
