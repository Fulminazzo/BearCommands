package it.angrybear.Velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.DisablePlugin;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.MessagingListener;
import it.angrybear.Managers.BearPlayersManager;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.YamlPair;
import it.angrybear.Velocity.Listeners.VelocityBearPlayerListener;
import it.angrybear.Velocity.Listeners.VelocityMessagingListener;
import it.angrybear.Velocity.Objects.VelocityBearPlayer;
import it.angrybear.Velocity.Utils.MessageUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public abstract class VelocityBearPlugin<OnlinePlayer extends VelocityBearPlayer> implements IBearPlugin<OnlinePlayer> {
    protected final ProxyServer proxyServer;
    protected final Logger logger;
    protected final Path dataDirectory;
    protected static VelocityBearPlugin<?> instance;
    protected Configuration config;
    protected Configuration lang;
    // Player Manager
    protected BearPlayersManager<OnlinePlayer> bearPlayersManager;
    protected Class<? extends BearPlayersManager<OnlinePlayer>> bearPlayerManagerClass;
    protected Class<OnlinePlayer> playerClass;

    protected VelocityBearPlayerListener<OnlinePlayer> playerListener;

    // PluginMessaging
    private final List<MessagingChannel> messagingChannels = new ArrayList<>();
    private final List<VelocityMessagingListener> pluginMessagingListeners = new ArrayList<>();

    private List<YamlPair<?>> additionalYamlPairs = new ArrayList<>();

    public VelocityBearPlugin(ProxyServer proxyServer, Logger logger, Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        onEnable();
    }

    public void onEnable() {
        try {
            instance = this;
            loadAll();
        } catch (Exception e) {
            if (!(e instanceof DisablePlugin))
                IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                        "%task%", "enabling plugin", "%error%", e.getMessage()));
            disablePlugin();
        }
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        onDisable();
    }

    public void onDisable() {
        try {
            unloadAll();
        } catch (Exception e) {
            IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "disabling plugin", "%error%", e.getMessage()));
            disablePlugin();
        }
    }

    @Override
    public void loadAll() throws Exception {
        loadConfigurations();
        loadManagers();
        loadMessagingChannels();
        loadListeners();
    }

    @Override
    public void loadConfigurations() throws Exception {
        if (getResource("config.yml") != null) loadConfig();
        if (getResource("messages.yml") != null) loadLang();
    }

    @Override
    public void loadConfig() throws Exception {
        this.config = loadGeneral("config.yml", true);
    }

    @Override
    public void loadLang() throws Exception {
        this.lang = loadGeneral("messages.yml", true);
    }

    @Override
    public void loadManagers() throws Exception {
        if (playerClass != null && bearPlayerManagerClass != null) {
            bearPlayersManager = new ReflObject<>(bearPlayerManagerClass, this, playerClass).getObject();
            bearPlayersManager.reloadPlayers(getProxyServer().getAllPlayers());
        }
    }

    @Override
    public void loadListeners() throws Exception {
        if (playerListener != null) unloadListeners();
        playerListener = new VelocityBearPlayerListener<>(this);
        getProxyServer().getEventManager().register(this, playerListener);
        this.pluginMessagingListeners.forEach(l -> getProxyServer().getEventManager().register(this, l));
    }

    public void loadMessagingChannels() throws Exception {
        Stream.concat(this.messagingChannels.stream(), this.pluginMessagingListeners.stream().map(MessagingListener::getChannel))
                .map(MessagingChannel::toString)
                .distinct()
                .forEach(c -> getProxyServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from(c)));
    }

    @Override
    public void unloadAll() throws Exception {
        unloadManagers();
        unloadListeners();
        unloadMessagingChannels();
        if (additionalYamlPairs != null) additionalYamlPairs.clear();
    }

    @Override
    public void unloadManagers() throws IOException {
        if (bearPlayersManager != null) {
            if (bearPlayersManager.getQuitAction() != null)
                getProxyServer().getAllPlayers()
                    .stream()
                    .map(p -> bearPlayersManager.getPlayer(p))
                    .filter(Objects::nonNull)
                    .forEach(p -> bearPlayersManager.getQuitAction().accept(p));
            getPlayersManager().saveAll();
            getPlayersManager().removeAll();
        }
    }

    @Override
    public void unloadListeners() throws Exception {
        getProxyServer().getEventManager().unregisterListeners(this);
    }

    public void unloadMessagingChannels() throws Exception {
        this.messagingChannels.stream()
                .map(MessagingChannel::toString)
                .forEach(c -> getProxyServer().getChannelRegistrar().unregister(MinecraftChannelIdentifier.from(c)));
    }

    // Online Player
    @Override
    public void setPlayerManagerClass(Class<? extends BearPlayersManager<OnlinePlayer>> managerClass, Class<OnlinePlayer> playerClass) {
        this.bearPlayerManagerClass = managerClass;
        setPlayerClass(playerClass);
    }

    @Override
    public <M extends BearPlayersManager<OnlinePlayer>> M getPlayersManager() {
        return (M) bearPlayersManager;
    }

    @Override
    public void setPlayerClass(Class<OnlinePlayer> playerClass) {
        this.playerClass = playerClass;
    }

    // Custom Yaml Objects
    @Override
    public void addAdditionalYamlPairs(YamlPair<?>... yamlPairs) {
        this.additionalYamlPairs.addAll(Arrays.asList(yamlPairs));
    }

    @Override
    public YamlPair<?>[] getAdditionalYamlPairs() {
        this.additionalYamlPairs = this.additionalYamlPairs.stream()
                .sorted(Comparator.comparing(c -> ReflUtil.getClassAndSuperClasses(c.getClass()).length))
                .collect(Collectors.toList());
        return additionalYamlPairs.toArray(new YamlPair<?>[this.additionalYamlPairs.size()]);
    }

    // PluginMessaging
    @Override
    public void addMessagingChannel(MessagingChannel channel) {
        removeMessagingChannel(channel);
        this.messagingChannels.add(channel);
    }

    @Override
    public void removeMessagingChannel(MessagingChannel channel) {
        this.messagingChannels.removeIf(c -> c.equals(channel));
        removeMessagingListener(channel);
    }

    @Override
    public void addMessagingListener(MessagingChannel channel, MessagingCommand... commands) {
        removeMessagingListener(channel);
        this.pluginMessagingListeners.add(new VelocityMessagingListener(this, channel, commands));
    }

    @Override
    public void removeMessagingListener(MessagingChannel channel) {
        this.pluginMessagingListeners.removeIf(l -> l.getChannel().equals(channel));
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public Configuration getLang() {
        return lang;
    }

    @Override
    public void disablePlugin() {
        //TODO: ??
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    @Override
    public Object getLogger() {
        return logger;
    }

    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
    }

    @Override
    public InputStream getResource(String path) {
        if (!path.startsWith("/")) path = "/" + path;
        return this.getClass().getResourceAsStream(path);
    }

    public static void sendConsole(BearLoggingMessage bearLoggingMessage, String... strings) {
        sendConsole(bearLoggingMessage.getMessage(strings));
    }

    public static void sendConsole(String message) {
        if (message == null) message = "";
        VelocityBearPlugin.instance.getProxyServer().getConsoleCommandSource().sendMessage(MessageUtils.messageToComponent(message));
    }

    public static void logInfo(BearLoggingMessage loggingMessage, String... strings) {
        IBearPlugin.logInfo(loggingMessage, strings);
    }

    public static void logInfo(String message) {
        IBearPlugin.logInfo(message);
    }

    public static void logWarning(BearLoggingMessage loggingMessage, String... strings) {
        IBearPlugin.logWarning(loggingMessage, strings);
    }

    public static void logWarning(String message) {
        IBearPlugin.logWarning(message);
    }

    public static void logError(BearLoggingMessage loggingMessage, String... strings) {
        IBearPlugin.logError(loggingMessage, strings);
    }

    public static void logError(String message) {
        IBearPlugin.logError(message);
    }
}
