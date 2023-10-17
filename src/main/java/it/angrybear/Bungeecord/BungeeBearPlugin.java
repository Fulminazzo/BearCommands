package it.angrybear.Bungeecord;

import it.angrybear.Bungeecord.Listeners.BungeeBearPlayerListener;
import it.angrybear.Bungeecord.Listeners.BungeeMessagingListener;
import it.angrybear.Bungeecord.Objects.BungeeBearPlayer;
import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Exceptions.DisablePlugin;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.MessagingListener;
import it.angrybear.Managers.BearPlayersManager;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.YamlPair;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public abstract class BungeeBearPlugin<OnlinePlayer extends BungeeBearPlayer<?>> extends Plugin implements IBearPlugin<OnlinePlayer> {
    protected static BungeeBearPlugin<?> instance;
    protected Configuration config;
    protected Configuration lang;
    // Player Manager
    protected BearPlayersManager<OnlinePlayer> bearPlayersManager;
    protected Class<? extends BearPlayersManager<OnlinePlayer>> bearPlayerManagerClass;
    protected Class<OnlinePlayer> playerClass;

    protected BungeeBearPlayerListener<OnlinePlayer> playerListener;

    // PluginMessaging
    private final List<MessagingChannel> messagingChannels = new ArrayList<>();
    private final List<BungeeMessagingListener> pluginMessagingListeners = new ArrayList<>();

    private List<YamlPair<?>> additionalYamlPairs = new ArrayList<>();
    private final List<String> requiredPlugins = new ArrayList<>();

    private boolean enabled;
    private boolean loaded;

    @Override
    public void onEnable() {
        try {
            instance = this;
            enabled = true;
            loadAll();
        } catch (Exception e) {
            e.printStackTrace();
            if (!(e instanceof DisablePlugin))
                IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "enabling plugin", "%error%", e.getMessage()));
            disablePlugin();
        }
    }

    @Override
    public void onDisable() {
        try {
            unloadAll();
        } catch (Exception e) {
            IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "disabling plugin", "%error%", e.getMessage()));
        }
    }

    @Override
    public void loadAll() throws Exception {
        if (isLoaded()) return;
        loaded = true;
        IBearPlugin.super.loadAll();
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
            bearPlayersManager.reloadPlayers(getProxy().getPlayers());
        }
    }

    @Override
    public void loadListeners() throws Exception {
        if (playerListener != null) unloadListeners();
        playerListener = new BungeeBearPlayerListener<>(this);
        getProxy().getPluginManager().registerListener(this, playerListener);
        this.pluginMessagingListeners.forEach(l -> getProxy().getPluginManager().registerListener(this, l));
    }

    @Override
    public void loadMessagingChannels() throws Exception {
        Stream.concat(this.messagingChannels.stream(), this.pluginMessagingListeners.stream().map(MessagingListener::getChannel))
                .map(MessagingChannel::toString)
                .distinct()
                .forEach(c -> getProxy().registerChannel(c));
    }

    @Override
    public void unloadAll() throws Exception {
        if (!isLoaded()) return;
        loaded = false;
        unloadManagers();
        unloadListeners();
        unloadMessagingChannels();
        if (additionalYamlPairs != null) additionalYamlPairs.clear();
    }

    @Override
    public void unloadManagers() throws Exception {
        if (bearPlayersManager != null) {
            if (bearPlayersManager.getQuitAction() != null)
                getProxy().getPlayers()
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
        getProxy().getPluginManager().unregisterListeners(this);
    }

    public void unloadMessagingChannels() throws Exception {
        this.messagingChannels.stream()
                .map(MessagingChannel::toString)
                .forEach(c -> getProxy().unregisterChannel(c));
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
        this.pluginMessagingListeners.add(new BungeeMessagingListener(this, channel, commands));
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
    public void requires(String... plugins) {
        this.requiredPlugins.addAll(Arrays.asList(plugins));
    }

    @Override
    public List<String> getRequiredPlugins() {
        return requiredPlugins;
    }

    @Override
    public void disablePlugin() {
        enabled = false;
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.unregisterListeners(this);
        pluginManager.unregisterCommands(this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public String getName() {
        return getDescription().getName();
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public InputStream getResource(String path) {
        InputStream inputStream = this.getClass().getResourceAsStream(path);
        if (inputStream == null)
            if (path.startsWith("/")) inputStream = this.getClass().getResourceAsStream(path.substring(1));
            else inputStream = this.getClass().getResourceAsStream("/" + path);
        return inputStream;
    }

    public static void sendConsole(BearLoggingMessage bearLoggingMessage, String... strings) {
        sendConsole(bearLoggingMessage.getMessage(strings));
    }

    public static void sendConsole(String message) {
        if (message == null) message = "";
        message = ChatColor.translateAlternateColorCodes('&', message);
        ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(message));
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
