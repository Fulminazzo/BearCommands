package it.angrybear.Bukkit.Objects;

import it.angrybear.Bukkit.Interfaces.IBossBar;
import it.angrybear.Bukkit.Objects.Reflections.NMSReflObject;
import it.angrybear.Bukkit.Utils.NMSUtils;
import it.angrybear.Bukkit.Utils.PacketsUtils;
import it.angrybear.Interfaces.IBearPlugin;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.stream.Collectors;

public class LegacyBossBar implements IBossBar, Listener {
    private final HashMap<UUID, ReflObject<?>> dragons;
    private final List<UUID> players;
    private String title;
    private float progress;
    private boolean visible;

    public LegacyBossBar(String title) {
        this.dragons = new HashMap<>();
        this.players = new ArrayList<>();
        this.title = title;
        Bukkit.getPluginManager().registerEvents(this, IBearPlugin.getInstance());
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        updateBar();
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public void setProgress(double progress) {
        this.progress = (float) progress;
        updateBar();
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (visible) restorePlayer(player);
            else savePlayer(player);
        });
    }

    /*
        Thanks to https://www.spigotmc.org/threads/nms-1-8-x-boss-bar-utility-so-simple.150263/
     */

    @Override
    public void addPlayer(Player player) {
        if (player == null) return;
        Location location = player.getLocation();
        ReflObject<?> worldServer = PacketsUtils.getWorldConnection(player.getWorld());

        ReflObject<?> dragon = new NMSReflObject<>("EntityEnderDragon", worldServer.getObject());
        dragon.callMethod("setLocation", location.getX(), location.getY() - 100, location.getZ(), 0.0F, 0.0F);

        ReflObject<?> packet = new NMSReflObject<>("PacketPlayOutSpawnEntityLiving", dragon.getObject());
        packet.setField("l", createDataWatcher().getObject());

        dragons.put(player.getUniqueId(), dragon);
        players.add(player.getUniqueId());
        PacketsUtils.sendPacket(player, packet);
    }

    @Override
    public void removePlayer(Player player) {
        if (player == null || !isBossBarPlayer(player)) return;
        ReflObject<?> packet = new NMSReflObject<>("PacketPlayOutEntityDestroy",
                new Class[]{int[].class},
                (Object) new int[]{dragons.get(player.getUniqueId()).getMethodObject("getId")});
        dragons.remove(player.getUniqueId());
        players.remove(player.getUniqueId());
        PacketsUtils.sendPacket(player, packet);
    }
    private void updateBar() {
        ReflObject<?> watcher = createDataWatcher();

        Bukkit.getOnlinePlayers().stream().filter(this::isBossBarPlayer).forEach(player -> {
            NMSReflObject<?> packet = new NMSReflObject<>("PacketPlayOutEntityMetadata",
                    new Class[]{int.class, watcher.getaClass(), boolean.class},
                    dragons.get(player.getUniqueId()).getMethodObject("getId"), watcher.getObject(), true);
            PacketsUtils.sendPacket(player, packet);
        });
    }

    private ReflObject<?> createDataWatcher() {
        ReflObject<?> watcher = new NMSReflObject<>("DataWatcher",
                new Class[]{NMSUtils.getNMSClass("Entity")}, (Object) null);
        watcher.callMethod("a", 0, (byte) 0x20);
        watcher.callMethod("a", 6, progress * 200);
        watcher.callMethod("a", 10, title);
        watcher.callMethod("a", 2, title);
        watcher.callMethod("a", 11, (byte) 1);
        watcher.callMethod("a", 3, (byte) 1);
        return watcher;
    }

    @Override
    public void removeAll() {
        getPlayers().forEach(this::removePlayer);
    }

    private void restorePlayer(Player player) {
        if (players.contains(player.getUniqueId())) {
            players.remove(player.getUniqueId());
            addPlayer(player);
        }
    }

    private void savePlayer(Player player) {
        if (isBossBarPlayer(player)) {
            removePlayer(player);
            players.add(player.getUniqueId());
        }
    }
    
    private boolean isBossBarPlayer(Player player) {
        return dragons.getOrDefault(player.getUniqueId(), null) != null;
    }

    public void teleportBar(Player player) {
        if (player == null || !isBossBarPlayer(player)) return;
        Location location = player.getLocation();
        ReflObject<?> packet = new NMSReflObject<>("PacketPlayOutEntityTeleport",
                (int) dragons.get(player.getUniqueId()).getMethodObject("getId"),
                (int) location.getX() * 32, (int) (location.getY() - 100) * 32, (int) location.getZ() * 32,
                (byte) ((int) location.getYaw() * 256 / 360), (byte) ((int) location.getPitch() * 256 / 360), false);
        PacketsUtils.sendPacket(player, packet);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        teleportBar(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        restorePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        savePlayer(event.getPlayer());
    }

    @Override
    public List<Player> getPlayers() {
        return dragons.keySet().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
