package it.angrybear.Bukkit.Utils;

import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.World;

import java.lang.reflect.Field;

public class PacketsUtils {
    public static ReflObject<?> getEntityPlayer(Object player) {
        return new ReflObject<>(player).callMethod("getHandle");
    }

    public static ReflObject<?> getPlayerConnection(Object player) {
        Class<?> playerConnectionClass = NMSUtils.getNMSClass("PlayerConnection", "server.network");
        if (playerConnectionClass == null) return new ReflObject<>(null);
        ReflObject<?> entityPlayer = getEntityPlayer(player);
        Field field = entityPlayer.getFields().stream()
                .filter(f -> f.getType().equals(playerConnectionClass))
                .findAny().orElse(null);
        if (field == null) return new ReflObject<>(null);
        return entityPlayer.obtainField(field.getName());
    }

    public static ReflObject<?> getWorldConnection(World world) {
        return new ReflObject<>(world).callMethod("getHandle");
    }

    public static void sendPacket(Object player, ReflObject<?> packet) {
        getPlayerConnection(player).callMethodNameless(packet.getObject());
    }
}