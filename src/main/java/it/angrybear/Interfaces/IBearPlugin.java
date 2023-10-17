package it.angrybear.Interfaces;

import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.DisablePlugin;
import it.angrybear.Managers.BearPlayersManager;
import it.angrybear.Objects.ABearPlayer;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.Configurations.ConfigurationCheck;
import it.angrybear.Objects.Configurations.InvalidType;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Utils.ServerUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public interface IBearPlugin<OnlinePlayer extends ABearPlayer<?>> {

    void onEnable();

    void onDisable();

    default void checkDependencies() throws DisablePlugin {
        String plugin = getRequiredPlugins().stream().filter(p -> !ServerUtils.isPluginEnabled(p)).findFirst().orElse(null);
        if (plugin != null) {
            logWarning(BearLoggingMessage.DEPENDENCY_REQUIRED, "%plugin%", plugin);
            throw new DisablePlugin();
        }
    }

    default void loadAll() throws Exception {
        checkDependencies();
        loadConfigurations();
        loadManagers();
        loadMessagingChannels();
        loadListeners();
    }

    void loadConfigurations() throws Exception;

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

    void loadManagers() throws Exception;

    void loadMessagingChannels() throws Exception;

    void loadListeners() throws Exception;

    void unloadAll() throws Exception;

    void unloadManagers() throws Exception;

    void unloadListeners() throws Exception;

    // Online Player
    void setPlayerManagerClass(Class<? extends BearPlayersManager<OnlinePlayer>> managerClass, Class<OnlinePlayer> playerClass);

    <M extends BearPlayersManager<OnlinePlayer>> M getPlayersManager();

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

    void requires(String... plugins);

    List<String> getRequiredPlugins();

    void disablePlugin();

    boolean isLoaded();

    boolean isEnabled();

    Configuration getLang();

    Configuration getConfiguration();

    String getName();

    String getVersion();

    File getDataFolder();

    InputStream getResource(String path);

    Object getLogger();

    static void logInfo(BearLoggingMessage loggingMessage, String... strings) {
        logInfo(loggingMessage.getMessage(strings));
    }

    static void logInfo(String message) {
        Object logger = getInstance().getLogger();
        if (logger instanceof Logger) ((Logger) logger).info(message);
        else ((org.slf4j.Logger) logger).info(message);
    }

    static void logWarning(BearLoggingMessage loggingMessage, String... strings) {
        logWarning(loggingMessage.getMessage(strings));
    }

    static void logWarning(String message) {
        Object logger = getInstance().getLogger();
        if (logger instanceof Logger) ((Logger) logger).warning(message);
        else ((org.slf4j.Logger) logger).warn(message);
    }

    static void logError(BearLoggingMessage loggingMessage, String... strings) {
        logError(loggingMessage.getMessage(strings));
    }

    static void logError(String message) {
        Object logger = getInstance().getLogger();
        if (logger instanceof Logger) ((Logger) logger).severe(message);
        else ((org.slf4j.Logger) logger).error(message);
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
        if (plugin == null && ServerUtils.isVelocity())
            plugin = new ReflObject<>("it.angrybear.Velocity.VelocityBearCommandsPlugin", false)
                .getMethodObject("getPlugin");
        return (M) plugin;
    }
}