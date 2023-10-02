package it.angrybear.Bukkit.Utils;

import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.World;

import java.lang.reflect.Field;

public class PacketsUtils {
    public static ReflObject<?> getEntityPlayer(Object player) {
        // return ((CraftPlayer) player).getHandle();
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
        // return ((EntityPlayer) player).c;
        return entityPlayer.obtainField(field.getName());
    }

    public static ReflObject<?> getWorldConnection(World world) {
        // return ((CraftWorld) world).getHandle();
        return new ReflObject<>(world).callMethod("getHandle");
    }

    public static void sendPacket(Object player, ReflObject<?> packet) {
        // return getPlayerConnection().sendPacket(packet);
        getPlayerConnection(player).callMethodNameless(packet.getObject());
    }
}