package it.angrybear.Interfaces;

import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Managers.BearPlayerManager;
import it.angrybear.Objects.ABearPlayer;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.Configurations.ConfigurationCheck;
import it.angrybear.Objects.Configurations.InvalidType;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Utils.ServerUtils;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public interface IBearPlugin<OnlinePlayer extends ABearPlayer> {

    void loadAll() throws Exception;

    void loadConfig() throws Exception;

    void loadLang() throws Exception;

    default Configuration loadGeneral(String configName, boolean check) throws Exception {
        if (check) {
            ConfigurationCheck configurationCheck = ConfigUtils.checkConfiguration(this, getDataFolder(), configName);
            if (!configurationCheck.isEmpty()) {
                IBearPlugin.logWarning(BearLoggingMessage.CONFIG_ERROR.getMessage("%config%", configName));
                IBearPlugin.logWarning(BearLoggingMessage.MISSING_ENTRIES.getMessage());
                configurationCheck.getMissingEntries().forEach(IBearPlugin::logWarning);
                IBearPlugin.logWarning(BearLoggingMessage.INVALID_ENTRIES.getMessage());
                configurationCheck.getInvalidTypes().stream().map(InvalidType::toString).forEach(IBearPlugin::logWarning);
                IBearPlugin.logWarning(BearLoggingMessage.AUTO_CORRECT.getMessage());
            }
        }
        return ConfigUtils.loadConfiguration(this, getDataFolder(), configName, check);
    }

    void loadManagers();

    void loadListeners();

    void unloadAll() throws Exception;

    void unloadManagers();

    void unloadListeners();

    // Online Player
    void setPlayerManagerClass(Class<? extends BearPlayerManager<OnlinePlayer>> managerClass, Class<OnlinePlayer> playerClass);

    <M extends BearPlayerManager<OnlinePlayer>> M getPlayersManager();

    void setPlayerClass(Class<OnlinePlayer> playerClass);

    // Custom Yaml Objects
    void addAdditionalYamlPairs(YamlPair<?>... yamlPairs);

    YamlPair<?>[] getAdditionalYamlPairs();

    // PluginMessaging
    default void addMessagingChannel(String channel) {
        addMessagingChannel(new MessagingChannel(this, channel));
    }

    void addMessagingChannel(MessagingChannel channel);

    default void removeMessagingChannel(String channel) {
        removeMessagingChannel(new MessagingChannel(this, channel));
    }

    void removeMessagingChannel(MessagingChannel channel);

    default void addMessagingListener(String channel, MessagingCommand... commands) {
        addMessagingListener(new MessagingChannel(this, channel), commands);
    }

    void addMessagingListener(MessagingChannel channel, MessagingCommand... commands);

    default void removeMessagingListener(String channel) {
        removeMessagingListener(new MessagingChannel(this, channel));
    }

    void removeMessagingListener(MessagingChannel channel);

    void disablePlugin();

    Configuration getLang();

    Configuration getConfiguration();

    String getName();

    File getDataFolder();

    InputStream getResource(String path);

    Logger getLogger();

    static void logInfo(BearLoggingMessage loggingMessage, String... strings) {
        logInfo(loggingMessage.getMessage(strings));
    }

    static void logInfo(String message) {
        getInstance().getLogger().info(message);
    }

    static void logWarning(BearLoggingMessage loggingMessage, String... strings) {
        logWarning(loggingMessage.getMessage(strings));
    }

    static void logWarning(String message) {
        getInstance().getLogger().warning(message);
    }

    static void logError(BearLoggingMessage loggingMessage, String... strings) {
        logError(loggingMessage.getMessage(strings));
    }

    static void logError(String message) {
        getInstance().getLogger().severe(message);
    }

    static <M extends IBearPlugin<?>> M getInstance() {
        Object plugin = null;
        Iterator<StackTraceElement> stack = Arrays.stream(Thread.currentThread().getStackTrace()).iterator();
        while (stack.hasNext())
            try {
                Class<?> aClass = ReflUtil.getClass(stack.next().getClassName());
                Object p = ServerUtils.getPluginFromClass(aClass);
                if (Arrays.asList(ReflUtil.getClassAndSuperClasses(p.getClass())).contains(IBearPlugin.class) &&
                        !p.equals(ServerUtils.getPluginFromClass(IBearPlugin.class))) plugin = p;
            } catch (Exception ignored) {}
        if (plugin == null) {
            Collection<?> plugins = ServerUtils.getPlugins();
            if (plugins != null) plugin = plugins.stream()
                    .filter(p -> p.getClass().getClassLoader().equals(IBearPlugin.class.getClassLoader()))
                    .findFirst().orElse(null);
        }
        return (M) plugin;
    }
}