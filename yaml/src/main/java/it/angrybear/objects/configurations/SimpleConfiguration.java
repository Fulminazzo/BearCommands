package it.angrybear.objects.configurations;

import it.angrybear.interfaces.IConfiguration;
import lombok.Getter;

import java.util.Map;

/**
 * Represents a Simple YAML Configuration.
 */
public abstract class SimpleConfiguration implements IConfiguration {
    @Getter
    protected final String name;
    protected final Map<String, Object> map;
    protected boolean nonNull;

    protected SimpleConfiguration(String name, Map<Object, Object> map) {
        this.name = name;
        this.map = IConfiguration.generalToConfigMap(this, map);
        setNonNull(false);
    }

    /**
     * Converts the current configuration to a map.
     *
     * @return the map
     */
    @Override
    public Map<String, Object> toMap() {
        return map;
    }

    /**
     * Sets the nullability of the configuration.
     * If set to true, the plugin will not accept null objects
     * when calling get methods.
     *
     * @param nonNull the non-null boolean
     */
    @Override
    public void setNonNull(boolean nonNull) {
        this.nonNull = nonNull;
        IConfiguration.super.setNonNull(nonNull);
    }

    /**
     * Checks the nullability of the section.
     *
     * @return true if nullability is not allowed
     */
    @Override
    public boolean checkNonNull() {
        return nonNull;
    }
}
