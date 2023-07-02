package it.angrybear.Objects.YamlElements;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.YamlElementException;
import it.angrybear.Objects.BearPlayer;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;

@SuppressWarnings("unchecked")
public class BearPlayerYamlObject<P extends BearPlayer> extends YamlObject<P> {
    private final Class<P> playerClass;

    public BearPlayerYamlObject(Class<?> playerClass, YamlPair<?>... yamlPairs) {
        super(yamlPairs);
        this.playerClass = (Class<P>) playerClass;
    }

    public BearPlayerYamlObject(P object, Class<?> playerClass, YamlPair<?>... yamlPairs) {
        super(object, yamlPairs);
        this.playerClass = (Class<P>) playerClass;
    }

    @Override
    public P load(ConfigurationSection configurationSection, String path) throws Exception {
        if (playerClass == null) throw new YamlElementException(BearLoggingMessage.GENERAL_CANNOT_BE_NULL,
                "%object%", "PlayerClass");
        ConfigurationSection playerSection = configurationSection.getConfigurationSection(path);
        if (playerSection == null) return null;
        Constructor<P> playerConstructor;
        try {
            playerConstructor = ReflUtil.getConstructor(playerClass);
            if (playerConstructor == null) throw new NoSuchMethodException();
        } catch (NoSuchMethodException e) {
            throw new YamlElementException(BearLoggingMessage.EMPTY_CONSTRUCTOR_NOT_FOUND,
                    "%class%", playerClass.getName());
        }
        P player = playerConstructor.newInstance();
        player.load(playerSection);
        return player;
    }

    @Override
    public void dump(ConfigurationSection configurationSection, String path) {
        configurationSection.set(path, null);
        if (object == null) return;
        ConfigurationSection playerSection = configurationSection.createSection(path);
        object.dump(playerSection);
    }
}
