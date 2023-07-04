package it.angrybear.Bukkit;

import it.angrybear.Bukkit.Listeners.BukkitBearPlayerListener;
import it.angrybear.Bukkit.Listeners.BukkitMessagingListener;
import it.angrybear.Bukkit.Listeners.PlaceholderListener;
import it.angrybear.Bukkit.Managers.OfflineBearPlayerManager;
import it.angrybear.Bukkit.Objects.BearPlayer;
import it.angrybear.Bukkit.Objects.Placeholder;
import it.angrybear.Bukkit.Objects.YamlElements.*;
import it.angrybear.Commands.MessagingCommand;
import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Exceptions.DisablePlugin;
import it.angrybear.Interfaces.IBearPlugin;
import it.angrybear.Listeners.MessagingListener;
import it.angrybear.Managers.BearPlayerManager;
import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Objects.MessagingChannel;
import it.angrybear.Objects.YamlPair;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public abstract class BearPlugin<OnlinePlayer extends BearPlayer, OfflinePlayer extends BearPlayer> extends JavaPlugin implements IBearPlugin<OnlinePlayer> {
    protected static BearPlugin<?, ?> instance;
    protected Configuration lang;
    // Player Manager
    protected BearPlayerManager<OnlinePlayer> bearPlayersManager;
    protected Class<? extends BearPlayerManager<OnlinePlayer>> bearPlayerManagerClass;
    protected Class<OnlinePlayer> playerClass;
    // Offline Player Manager
    protected OfflineBearPlayerManager<OfflinePlayer> offlineBearPlayersManager;
    protected Class<? extends OfflineBearPlayerManager<OfflinePlayer>> offlineBearPlayersManagerClass;
    protected Class<OfflinePlayer> offlinePlayerClass;
    protected BukkitBearPlayerListener<OnlinePlayer, OfflinePlayer> playerListener;

    // Permissions
    private Class<? extends BearPermission> permissionsClass;

    // Placeholders
    private PlaceholderListener placeholderListener;
    private final List<Placeholder> placeholders = new ArrayList<>();
    private boolean placeholderApiRequired = true;

    // PluginMessaging
    private final List<MessagingChannel> messagingChannels = new ArrayList<>();
    private final List<BukkitMessagingListener> pluginMessagingListeners = new ArrayList<>();

    private List<YamlPair<?>> additionalYamlPairs = new ArrayList<>();
    private boolean reloadSupported = true;

    @Override
    public void onEnable() {
        try {
            instance = this;
            addAdditionalYamlPairs(new YamlPair<>(ItemStack.class, ItemStackYamlObject.class),
                    new YamlPair<>(Inventory.class, InventoryYamlObject.class),
                    new YamlPair<>(World.class, WorldYamlObject.class),
                    new YamlPair<>(Location.class, LocationYamlObject.class),
                    new YamlPair<>(Enchantment.class, EnchantmentYamlObject.class));
            loadAll();
        } catch (Exception e) {
            if (!(e instanceof DisablePlugin))
                IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "enabling plugin", "%error%", e.getMessage()));
            disablePlugin();
        }
    }

    @Override
    public void onDisable() {
        try {
            // Check if is reload.
            if (!reloadSupported && Arrays.stream(Thread.currentThread().getStackTrace())
                    .filter(s -> !s.toString().contains("java.base"))
                    .filter(s -> !s.toString().contains("org.bukkit"))
                    .filter(s -> !s.toString().contains("net.minecraft.server"))
                    .anyMatch(s -> !s.toString().contains(getName())))
                IBearPlugin.logWarning(BearLoggingMessage.RELOAD_UNSUPPORTED, "%plugin%", getName());
            unloadAll();
        } catch (Exception e) {
            IBearPlugin.logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "disabling plugin", "%error%", e.getMessage()));
            disablePlugin();
        }
    }

    @Override
    public void loadAll() throws Exception {
        if (getResource("config.yml") != null) loadConfig();
        if (getResource("messages.yml") != null) loadLang();
        loadPermissions();
        loadManagers();
        loadMessagingChannels();
        loadListeners();
        loadPlaceholders();
    }

    @Override
    public void loadConfig() throws Exception {
        loadGeneral("config.yml", true);
        reloadConfig();
    }

    @Override
    public void loadLang() throws Exception {
        this.lang = loadGeneral("messages.yml", true);
    }

    public void loadPermissions() {
        if (permissionsClass == null) return;
        List<String> permissions = getPermissionsStringList();
        if (permissions.isEmpty()) return;
        while (!permissions.isEmpty())
            loadPermissionRecursive(null, permissions.remove(0).toLowerCase(), permissions);
    }

    private void loadPermissionRecursive(Permission parent, String permission, List<String> permissions) {
        List<String> children = new ArrayList<>();
        List<String> tmp = new ArrayList<>(permissions);
        while (!tmp.isEmpty()) {
            String perm = tmp.remove(0).toLowerCase();
            if (perm.startsWith(permission)) {
                if (!perm.equals(permission)) children.add(perm);
                permissions.remove(perm);
            }
        }
        Permission newPermission = Bukkit.getPluginManager().getPermission(permission);
        if (newPermission == null) newPermission = new Permission(permission);
        else Bukkit.getPluginManager().removePermission(permission);
        while (!children.isEmpty()) {
            String perm = children.remove(0).toLowerCase();
            loadPermissionRecursive(newPermission, perm, children);
        }
        if (parent == null) Bukkit.getPluginManager().addPermission(newPermission);
        else newPermission.addParent(parent, true);
    }

    @Override
    public void loadManagers() {
        if (playerClass != null && bearPlayerManagerClass != null) {
            bearPlayersManager = new ReflObject<>(bearPlayerManagerClass, this, playerClass).getObject();
            bearPlayersManager.reloadPlayers(Bukkit.getOnlinePlayers());
        }
        if (offlinePlayerClass != null && offlineBearPlayersManagerClass != null) {
            offlineBearPlayersManager = new ReflObject<>(offlineBearPlayersManagerClass, this, offlinePlayerClass).getObject();
            offlineBearPlayersManager.reloadPlayers();
        }
    }

    @Override
    public void loadListeners() {
        if (playerListener != null) unloadListeners();
        playerListener = new BukkitBearPlayerListener<>(this);
        Bukkit.getPluginManager().registerEvents(playerListener, this);
        this.pluginMessagingListeners.forEach(l ->
                getServer().getMessenger().registerIncomingPluginChannel(this, l.getChannel().toString(), l));
    }

    public void loadMessagingChannels() {
        this.messagingChannels.stream()
                .map(MessagingChannel::toString)
                .distinct()
                .forEach(c -> getServer().getMessenger().registerOutgoingPluginChannel(this, c));
    }

    public void loadPlaceholders() throws Exception {
        if (placeholders.isEmpty()) return;
        Plugin placeholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderApi != null && placeholderApi.isEnabled()) {
            placeholderListener = new PlaceholderListener(this);
            IBearPlugin.logInfo(BearLoggingMessage.REGISTERED_PLACEHOLDERS,
                    "%plugin-name%", placeholderApi.getName(),
                    "%plugin-version%", placeholderApi.getDescription().getVersion());
            placeholderListener.register();
        } else {
            if (isPlaceholderApiRequired()) {
                IBearPlugin.logWarning(BearLoggingMessage.PLACEHOLDER_API_REQUIRED);
                throw new DisablePlugin();
            } else {
                IBearPlugin.logInfo(BearLoggingMessage.PLACEHOLDER_API_NOT_FOUND);
            }
        }
    }

    @Override
    public void unloadAll() throws Exception {
        if (bearPlayersManager != null) getPlayersManager().saveAll();
        if (offlineBearPlayersManager != null) getOfflinePlayersManager().saveAll();
        unloadManagers();
        unloadPermissions(this);
        unloadListeners();
        unloadMessagingChannels();
        unloadPlaceholders();
        if (additionalYamlPairs != null) additionalYamlPairs.clear();
    }

    public void unloadPermissions(BearPlugin<?, ?> plugin) {
        if (plugin != null)
            new ArrayList<>(Bukkit.getPluginManager().getPermissions()).stream()
                .filter(p -> p.getName().toLowerCase().startsWith(plugin.getName().toLowerCase()))
                .forEach(p -> Bukkit.getPluginManager().removePermission(p.getName()));
    }

    @Override
    public void unloadManagers() {
        if (bearPlayersManager != null && bearPlayersManager.getQuitAction() != null)
            Bukkit.getOnlinePlayers()
                    .stream()
                    .map(p -> bearPlayersManager.getPlayer(p))
                    .filter(Objects::nonNull)
                    .forEach(p -> bearPlayersManager.getQuitAction().accept(p));
        if (offlineBearPlayersManager != null && offlineBearPlayersManager.getQuitAction() != null)
            Bukkit.getOnlinePlayers()
                    .stream()
                    .map(p -> offlineBearPlayersManager.getPlayer(p))
                    .filter(Objects::nonNull)
                    .forEach(p -> offlineBearPlayersManager.getQuitAction().accept(p));
    }

    @Override
    public void unloadListeners() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        HandlerList.unregisterAll(this);
    }

    public void unloadPlaceholders() {
        if (placeholderListener != null) placeholderListener.unregister();
    }

    public void unloadMessagingChannels() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    // Online Player
    @Override
    public void setPlayerManagerClass(Class<? extends BearPlayerManager<OnlinePlayer>> managerClass, Class<OnlinePlayer> playerClass) {
        this.bearPlayerManagerClass = managerClass;
        setPlayerClass(playerClass);
    }

    @Override
    public <M extends BearPlayerManager<OnlinePlayer>> M getPlayersManager() {
        return (M) bearPlayersManager;
    }

    @Override
    public void setPlayerClass(Class<OnlinePlayer> playerClass) {
        this.playerClass = playerClass;
    }

    // Offline Player
    public void setOfflinePlayersManagerClass(Class<? extends OfflineBearPlayerManager<OfflinePlayer>> managerClass, Class<OfflinePlayer> offlinePlayerClass) {
        this.offlineBearPlayersManagerClass = managerClass;
        setOfflinePlayerClass(offlinePlayerClass);
    }

    public <M extends OfflineBearPlayerManager<OfflinePlayer>> M getOfflinePlayersManager() {
        return (M) offlineBearPlayersManager;
    }

    private void setOfflinePlayerClass(Class<OfflinePlayer> offlinePlayerClass) {
        this.offlinePlayerClass = offlinePlayerClass;
    }

    // Permissions
    public void setPermissionsClass(Class<? extends BearPermission> permissionsClass) {
        this.permissionsClass = permissionsClass;
    }

    private List<String> getPermissionsStringList() {
        if (permissionsClass == null) return new ArrayList<>();
        else return Arrays.stream(permissionsClass.getDeclaredFields())
                .filter(f -> f.getType().equals(permissionsClass))
                .map(f -> new ReflObject<>(permissionsClass.getCanonicalName(), false).<BearPermission>getFieldObject(f.getName()))
                .map(BearPermission::getPermission)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .filter(s -> !s.contains("%"))
                .sorted(Comparator.comparing(String::length))
                .collect(Collectors.toList());
    }

    // Placeholders
    public void addPlaceholder(String identifier, BiFunction<Player, String, String> subPlaceholdersHandler) {
        addPlaceholder(new Placeholder(identifier, subPlaceholdersHandler));
    }

    public void addPlaceholder(String identifier, Function<Player, String> value) {
        addPlaceholder(new Placeholder(identifier, value));
    }

    public void addPlaceholder(String identifier, Function<Player, String> value, BiFunction<Player, String, String> subPlaceholdersHandler) {
        addPlaceholder(new Placeholder(identifier, value, subPlaceholdersHandler));
    }

    public void addPlaceholder(String identifier, Placeholder... subPlaceholders) {
        addPlaceholder(new Placeholder(identifier, subPlaceholders));
    }

    public void addPlaceholder(String identifier, String value, Placeholder... subPlaceholders) {
        addPlaceholder(new Placeholder(identifier, value, subPlaceholders));
    }

    public void addPlaceholder(String identifier, Function<Player, String> value, Placeholder... subPlaceholders) {
        addPlaceholder(new Placeholder(identifier, value, subPlaceholders));
    }

    public void addPlaceholder(Placeholder placeholder) {
        if (placeholder == null || placeholder.getIdentifier() == null) return;
        this.placeholders.removeIf(p -> p.getIdentifier().equals(placeholder.getIdentifier()));
        this.placeholders.add(placeholder);
    }

    public List<Placeholder> getPlaceholders() {
        return placeholders;
    }

    public boolean isPlaceholderApiRequired() {
        return placeholderApiRequired;
    }

    public void setPlaceholderApiRequired(boolean required) {
        this.placeholderApiRequired = required;
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
        this.pluginMessagingListeners.add(new BukkitMessagingListener(this, channel, commands));
    }

    @Override
    public void removeMessagingListener(MessagingChannel channel) {
        this.pluginMessagingListeners.removeIf(l -> l.getChannel().equals(channel));
    }

    public void addReloadSupport() {
        this.reloadSupported = true;
    }

    public void removeReloadSupport() {
        this.reloadSupported = false;
    }

    @Override
    public Configuration getConfiguration() {
        return new Configuration(getConfig());
    }

    @Override
    public Configuration getLang() {
        return lang;
    }

    @Override
    public void disablePlugin() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public static void sendConsole(BearLoggingMessage bearLoggingMessage, String... strings) {
        sendConsole(bearLoggingMessage.getMessage(strings));
    }

    public static void sendConsole(String message) {
        if (message == null) message = "";
        message = ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.getConsoleSender().sendMessage(message);
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
