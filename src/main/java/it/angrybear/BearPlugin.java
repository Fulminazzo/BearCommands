package it.angrybear;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Enums.BearPermission;
import it.angrybear.Exceptions.DisablePlugin;
import it.angrybear.Listeners.BearPlayerListener;
import it.angrybear.Listeners.PlaceholderListener;
import it.angrybear.Managers.BearPlayerManager;
import it.angrybear.Managers.OfflineBearPlayerManager;
import it.angrybear.Objects.BearPlayer;
import it.angrybear.Objects.ConfigurationCheck;
import it.angrybear.Objects.InvalidType;
import it.angrybear.Objects.Placeholder;
import it.angrybear.Objects.YamlElements.YamlPair;
import it.angrybear.Utils.ConfigUtils;
import it.angrybear.Utils.PluginsUtil;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class BearPlugin<OnlinePlayer extends BearPlayer, OfflinePlayer extends BearPlayer> extends JavaPlugin {
    protected static BearPlugin<?, ?> instance;
    protected FileConfiguration lang;
    // Player Manager
    protected BearPlayerManager<OnlinePlayer> bearPlayersManager;
    protected Class<? extends BearPlayerManager<OnlinePlayer>> bearPlayerManagerClass;
    protected Class<OnlinePlayer> playerClass;
    // Offline Player Manager
    protected OfflineBearPlayerManager<OfflinePlayer> offlineBearPlayersManager;
    protected Class<? extends OfflineBearPlayerManager<OfflinePlayer>> offlineBearPlayersManagerClass;
    protected Class<OfflinePlayer> offlinePlayerClass;
    protected BearPlayerListener<OnlinePlayer, OfflinePlayer> playerListener;

    // Permissions
    private Class<? extends BearPermission> permissionsClass;

    // Placeholders
    private PlaceholderListener placeholderListener;
    private final List<Placeholder> placeholders = new ArrayList<>();
    private boolean placeholderApiRequired = true;

    private List<YamlPair<?>> additionalYamlPairs = new ArrayList<>();
    private boolean reloadSupported = true;

    @Override
    public void onEnable() {
        try {
            instance = this;
            loadAll();
        } catch (Exception e) {
            if (!(e instanceof DisablePlugin))
                logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "enabling plugin", "%error%", e.getMessage()));
            Bukkit.getPluginManager().disablePlugin(this);
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
                logWarning(BearLoggingMessage.RELOAD_UNSUPPORTED, "%plugin%", getName());
            unloadAll();
        } catch (Exception e) {
            logWarning(BearLoggingMessage.GENERAL_ERROR_OCCURRED.getMessage(
                    "%task%", "disabling plugin", "%error%", e.getMessage()));
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void loadAll() throws Exception {
        if (getResource("config.yml") != null) loadConfig();
        if (getResource("messages.yml") != null) loadLang();
        loadPermissions();
        loadManagers();
        loadListeners();
        loadPlaceholders();
    }

    public void loadConfig() throws Exception {
        loadGeneral("config.yml", true);
        reloadConfig();
    }

    public void loadLang() throws Exception {
        this.lang = loadGeneral("messages.yml", true);
    }

    public FileConfiguration loadGeneral(String configName, boolean check) throws Exception {
        if (check) {
            ConfigurationCheck configurationCheck = ConfigUtils.checkConfiguration(this, getDataFolder(), configName);
            if (!configurationCheck.isEmpty()) {
                logWarning(BearLoggingMessage.CONFIG_ERROR.getMessage("%config%", configName));
                logWarning(BearLoggingMessage.MISSING_ENTRIES.getMessage());
                configurationCheck.getMissingEntries().forEach(BearPlugin::logWarning);
                logWarning(BearLoggingMessage.INVALID_ENTRIES.getMessage());
                configurationCheck.getInvalidTypes().stream().map(InvalidType::toString).forEach(BearPlugin::logWarning);
                logWarning(BearLoggingMessage.AUTO_CORRECT.getMessage());
            }
        }
        return ConfigUtils.loadConfiguration(this, getDataFolder(), configName, check);
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

    public void loadManagers() {
        if (playerClass != null && bearPlayerManagerClass != null) {
            bearPlayersManager = new ReflObject<>(bearPlayerManagerClass, this, playerClass).getObject();
            bearPlayersManager.reloadPlayers();
        }
        if (offlinePlayerClass != null && offlineBearPlayersManagerClass != null) {
            offlineBearPlayersManager = new ReflObject<>(offlineBearPlayersManagerClass, this, offlinePlayerClass).getObject();
            offlineBearPlayersManager.reloadPlayers();
        }
    }

    public void loadListeners() {
        if (playerListener != null) unloadListeners();
        playerListener = new BearPlayerListener<>(this);
        Bukkit.getPluginManager().registerEvents(playerListener, this);
    }

    public void loadPlaceholders() throws Exception {
        if (placeholders.isEmpty()) return;
        Plugin placeholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderApi != null && placeholderApi.isEnabled()) {
            placeholderListener = new PlaceholderListener(this);
            logInfo(BearLoggingMessage.REGISTERED_PLACEHOLDERS,
                    "%plugin-name%", placeholderApi.getName(),
                    "%plugin-version%", placeholderApi.getDescription().getVersion());
            placeholderListener.register();
        } else {
            if (isPlaceholderApiRequired()) {
                logWarning(BearLoggingMessage.PLACEHOLDER_API_REQUIRED);
                throw new DisablePlugin();
            } else {
                logInfo(BearLoggingMessage.PLACEHOLDER_API_NOT_FOUND);
            }
        }
    }

    public void unloadAll() throws Exception {
        if (bearPlayersManager != null) getPlayersManager().saveAll();
        if (offlineBearPlayersManager != null) getOfflinePlayersManager().saveAll();
        unloadManagers();
        unloadListeners();
        unloadPlaceholders();
        if (additionalYamlPairs != null) additionalYamlPairs.clear();
    }

    public void unloadPermissions(BearPlugin<?, ?> plugin) {
        if (plugin != null)
            new ArrayList<>(Bukkit.getPluginManager().getPermissions()).stream()
                .filter(p -> p.getName().toLowerCase().startsWith(plugin.getName().toLowerCase()))
                .forEach(p -> Bukkit.getPluginManager().removePermission(p.getName()));
    }

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

    public void unloadListeners() {
        if (playerListener != null) HandlerList.unregisterAll(playerListener);
        HandlerList.unregisterAll(this);
    }

    public void unloadPlaceholders() {
        if (placeholderListener != null) placeholderListener.unregister();
    }

    // Online Player
    public void setPlayerManagerClass(Class<? extends BearPlayerManager<OnlinePlayer>> managerClass, Class<OnlinePlayer> playerClass) {
        this.bearPlayerManagerClass = managerClass;
        setPlayerClass(playerClass);
    }

    public <M extends BearPlayerManager<OnlinePlayer>> M getPlayersManager() {
        return (M) bearPlayersManager;
    }

    private void setPlayerClass(Class<OnlinePlayer> playerClass) {
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
    public void addAdditionalYamlPairs(YamlPair<?>... yamlPairs) {
        this.additionalYamlPairs.addAll(Arrays.asList(yamlPairs));
    }

    public YamlPair<?>[] getAdditionalYamlPairs() {
        this.additionalYamlPairs = this.additionalYamlPairs.stream()
                .sorted(Comparator.comparing(c -> ReflUtil.getClassAndSuperClasses(c.getClass()).length))
                .collect(Collectors.toList());
        return additionalYamlPairs.toArray(new YamlPair<?>[this.additionalYamlPairs.size()]);
    }

    public void addReloadSupport() {
        this.reloadSupported = true;
    }

    public void removeReloadSupport() {
        this.reloadSupported = false;
    }

    public FileConfiguration getLang() {
        return lang;
    }

    public static void sendConsole(BearLoggingMessage bearLoggingMessage, String... strings) {
        sendConsole(bearLoggingMessage.getMessage(strings));
    }

    public static void sendConsole(String message) {
        if (message == null) message = "";
        message = ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void logInfo(BearLoggingMessage bearLoggingMessage, String... strings) {
        logInfo(bearLoggingMessage.getMessage(strings));
    }

    public static void logInfo(String message) {
        getInstance().getLogger().info(message);
    }

    public static void logWarning(BearLoggingMessage bearLoggingMessage, String... strings) {
        logWarning(bearLoggingMessage.getMessage(strings));
    }

    public static void logWarning(String message) {
        getInstance().getLogger().warning(message);
    }

    public static void logError(BearLoggingMessage bearLoggingMessage, String... strings) {
        logError(bearLoggingMessage.getMessage(strings));
    }

    public static void logError(String message) {
        getInstance().getLogger().severe(message);
    }

    public static <M extends BearPlugin<?, ?>> M getInstance() {
        Plugin plugin = null;
        Iterator<StackTraceElement> stack = Arrays.stream(Thread.currentThread().getStackTrace()).iterator();
        while (stack.hasNext())
            try {
                Class<?> aClass = ReflUtil.getClass(stack.next().getClassName());
                Plugin p = PluginsUtil.getPluginFromClass(aClass);
                if (Arrays.asList(ReflUtil.getClassAndSuperClasses(p.getClass())).contains(BearPlugin.class) &&
                        !p.equals(BearPlugin.getProvidingPlugin(BearPlugin.class))) plugin = p;
            } catch (Exception ignored) {}
        if (plugin == null) plugin = BearPlugin.getProvidingPlugin(BearPlugin.class);
        return (M) plugin;
    }
}
