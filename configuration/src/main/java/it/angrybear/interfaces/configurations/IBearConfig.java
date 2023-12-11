package it.angrybear.interfaces.configurations;

import it.angrybear.interfaces.IBearConfigPlugin;

/**
 * The basic implementation of IBearConfig.
 * This interface has been designed to be used
 * with enums.
 */
public interface IBearConfig {

    /**
     * Gets path.
     *
     * @return the path
     */
    String getPath();

    /**
     * Gets plugin.
     *
     * @return the plugin
     */
    IBearConfigPlugin getPlugin();
}
