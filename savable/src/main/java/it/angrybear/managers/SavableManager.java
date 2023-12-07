package it.angrybear.managers;

import it.angrybear.enums.BearLoggingMessage;
import it.angrybear.exceptions.PluginRuntimeException;
import it.angrybear.interfaces.IBearPlugin;
import it.angrybear.interfaces.functions.BiFunctionException;
import it.angrybear.objects.Savable;

import java.io.File;

/**
 * Savable manager. Allows creating managers for custom savable objects.
 *
 * @param <T> the type of the object
 * @param <P> the type of the plugin
 */
public class SavableManager<T extends Savable<P>, P extends IBearPlugin> extends Manager<T> {
    protected final P plugin;
    protected final File folder;
    protected BiFunctionException<P, String, T> createNewTFromString;
    protected BiFunctionException<P, File, T> createNewTFromFile;

    public SavableManager(P plugin, String folderName) {
        this(plugin, folderName, null, null);
    }

    public SavableManager(P plugin, File folder) {
        this(plugin, folder, null, null);
    }

    public SavableManager(P plugin, String folderName, BiFunctionException<P, String, T> createNewTFromString,
                          BiFunctionException<P, File, T> createNewTFromFile) {
        this(plugin, new File(plugin.getDataFolder(), folderName), createNewTFromString, createNewTFromFile);
    }

    public SavableManager(P plugin, File folder, BiFunctionException<P, String, T> createNewTFromString,
                          BiFunctionException<P, File, T> createNewTFromFile) {
        this.plugin = plugin;
        this.folder = folder;
        this.createNewTFromString = createNewTFromString;
        this.createNewTFromFile = createNewTFromFile;
    }

    /**
     * Reloads all the objects from file.
     */
    @Override
    public void reload() {
        if (folder == null || !folder.isDirectory() || createNewTFromFile == null) return;
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File f : files)
            try {
                this.objects.add(createNewTFromFile.apply(plugin, f));
            } catch (Exception e) {
                throw new PluginRuntimeException(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
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
        if (createNewTFromString != null)
            try {
                this.objects.add(createNewTFromString.apply(plugin, name));
            } catch (Exception e) {
                throw new PluginRuntimeException(BearLoggingMessage.GENERAL_ERROR_OCCURRED,
                        "%task%", String.format("creating object of name %s", name),
                        "%error%", e.getMessage());
            }
    }
}
