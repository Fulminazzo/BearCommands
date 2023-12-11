package it.angrybear.enums;

/**
 * The LoadPolicy enum specifies how a YAML file
 * loading should be handled.
 * Check {@link it.angrybear.objects.configurations.ConfigManager} and
 * {@link it.angrybear.utils.ConfigUtils} for more.
 */
public enum LoadPolicy {
    /**
     * Warns the user of the missing keys
     * and invalid values.
     */
    WARN,
    /**
     * Warns the user of the missing keys
     * and invalid values and tries to correct them.
     * It does so by creating a backup of the previous
     * configuration and overwriting it with correct
     * values from the reference configuration.
     */
    WARN_AND_CORRECT,
    /**
     * Ignores any missing keys and
     * invalid values.
     */
    IGNORE
}