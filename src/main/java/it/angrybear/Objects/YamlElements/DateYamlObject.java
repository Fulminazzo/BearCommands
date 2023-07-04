package it.angrybear.Objects.YamlElements;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.YamlPair;

import java.util.Date;

public class DateYamlObject extends YamlObject<Date> {
    public DateYamlObject(YamlPair<?>... yamlPairs) {
        super(yamlPairs);
    }

    public DateYamlObject(Date object, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
    }

    @Override
    public Date load(Configuration configurationSection, String path) throws Exception {
        Long date = configurationSection.contains(path) ? configurationSection.getLong(path) : null;
        object = date == null ? null : new Date(date);
        return object;
    }

    @Override
    public void dump(Configuration configurationSection, String path) throws Exception {
        configurationSection.set(path, object == null ? null : object.getTime());
    }
}
