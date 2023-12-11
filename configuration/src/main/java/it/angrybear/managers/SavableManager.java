package it.angrybear.managers;

import it.angrybear.enums.BearLoggingMessage;
import it.angrybear.exceptions.BearRuntimeException;
import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.interfaces.functions.BiFunctionException;
import it.angrybear.objects.Savable;
import it.fulminazzo.yamlparser.utils.FileUtils;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

/**
 * Savable manager. Allows creating managers for custom savable objects.
 *
 * @param <T> the type of the object
 * @param <P> the type of the plugin
 */
public class SavableManager<T extends Savable<P>, P extends IBearPlugin> extends Manager<T> {
    @Getter
    protected final P plugin;
    @Getter
    protected final File folder;
    protected BiFunctionException<SavableManager<T, P>, String, T> createNewTFromString;
    protected BiFunctionException<SavableManager<T, P>, File, T> createNewTFromFile;

    public SavableManager(P plugin, String folderName, BiFunctionException<SavableManager<T, P>, String, T> createNewTFromString,
                          BiFunctionException<SavableManager<T, P>, File, T> createNewTFromFile) {
        this(plugin, new File(plugin.getDataFolder(), folderName), createNewTFromString, createNewTFromFile);
    }

    public SavableManager(P plugin, File folder, BiFunctionException<SavableManager<T, P>, String, T> createNewTFromString,
                          BiFunctionException<SavableManager<T, P>, File, T> createNewTFromFile) {
        this.plugin = plugin;
        this.folder = folder;
        this.createNewTFromString = createNewTFromString;
        this.createNewTFromFile = createNewTFromFile;
    }

    /**
     * Reload all the objects from file.
     */
    @Override
    public void reload() {
        if (folder == null || !folder.isDirectory() || createNewTFromFile == null) return;
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File f : files)
            try {
                this.objects.add(createNewTFromFile.apply(this, f));
            } catch (Exception e) {
                throw new BearRuntimeException(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                        "%task%", String.format("loading object from file %s", f.getName()),
                        "%error%", e.getMessage());
            }
    }

    /**
     * Add an object from the name of the file.
     *
     * @param name the name
     */
    public void add(String name) {
        if (createNewTFromString != null && name != null)
            try {
                T t = createNewTFromString.apply(this, name);
                if (t != null) this.objects.add(t);
            } catch (Exception e) {
                throw new BearRuntimeException(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                        "%task%", String.format("creating object of name %s", name),
                        "%error%", e.getMessage());
            }
    }

    /**
     * Save all objects.
     */
    public void saveAll() {
        if (!folder.isDirectory()) {
            try {
                FileUtils.createFolder(folder);
            } catch (IOException e) {
                plugin.logWarning(e.getMessage());
            }
        }
        this.objects.forEach(Savable::save);
    }
}
