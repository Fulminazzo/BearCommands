package it.angrybear.Utils;

import it.fulminazzo.reflectionutils.Objects.ReflObject;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ParticlesUtils extends NMSUtils {

    public static void spawnParticleNearPlayer(Player player, Location location, String particleName) {
        player.getWorld().getPlayers().stream().filter(p -> {
            if (p.equals(player)) return true;
            else return player.getLocation().distance(p.getLocation()) <= Bukkit.getServer().getViewDistance() * 16;
        }).filter(p -> p.canSee(player)).forEach(p -> spawnParticle(p, location, particleName));
    }

    public static void spawnParticle(Player player, Location location, String particleName) {
        ReflObject<Player> reflPlayer = new ReflObject<>(player);

        if (VersionsUtils.is1_13())
            reflPlayer.callMethod("spawnParticle",
                    new ReflObject<>("org.bukkit.Particle", false).getFieldObject(particleName),
                    location, 0);
        else reflPlayer.callMethod("playEffect",
                location,
                new ReflObject<>(Effect.class.getCanonicalName(), false).getFieldObject(particleName),
                null);
    }

    public static void spawnBlockParticle(Player player, Location particleLocation, Block block, Object blockData, JavaPlugin plugin) {
        int blockColor = getBlockColor(block, blockData, plugin);
        spawnBlockParticle(player, particleLocation, blockColor);
    }

    public static void spawnBlockParticle(Player player, Location particleLocation, int blockColor) {
        if (VersionsUtils.is1_13()) {
            String particleClass = "org.bukkit.Particle";
            // DustOptions dustOption = new Particle.DustOptions(Color.fromRGB(blockColor), 3f);
            ReflObject<?> dustOption = new ReflObject<>(particleClass + ".DustOptions", Color.fromRGB(blockColor), 3f);
            // Particle redstone = Particle.REDSTONE;
            ReflObject<?> redstone = new ReflObject<>(particleClass, false).obtainField("REDSTONE");
            // player.spawnParticle(redstone, particleLocation, 1, dustOption);
            new ReflObject<>(player).callMethod("spawnParticle", redstone.getObject(), particleLocation, 1, dustOption.getObject());
        } else {
            Player.Spigot playerSpigot = player.spigot();
            Color color = Color.fromRGB(blockColor);

            ReflObject<?> colouredDust = new ReflObject<>(Effect.class.getCanonicalName(), false).obtainField("COLOURED_DUST");
            float red = (float) color.getRed() / 255;
            float green = (float) color.getGreen() / 255;
            float blue = (float) color.getBlue() / 255;

            double offset = 0.5;
            double add = 0.3;
            for (double x = -offset; x <= offset; x += add)
                for (double y = -offset; y <= offset; y += add)
                    for (double z = -offset; z <= offset; z += add) {
                        /*playerSpigot.playEffect(particleLocation.clone().add(x, y, z), (Effect) colouredDust.getObject(), 0, 1,
                                red, green, blue, 1, 0, 1);*/
                        new ReflObject<>(playerSpigot).callMethod("playEffect",
                                particleLocation.clone().add(x, y, z), colouredDust.getObject(), 0, 1,
                                red, green, blue, 1f, 0, 1);
                    }
        }
    }

    public static int getBlockColor(Block block, Material material, JavaPlugin plugin) {
        return getBlockColor(block, (Object) (VersionsUtils.is1_13() ?
                        new ReflObject<>(material).getMethodObject("createBlockData") : material), plugin);
    }

    public static int getBlockColor(Block block, Object blockData, JavaPlugin plugin) {
        if (blockData == null) return 0;
        if (!VersionsUtils.is1_13()) {
            Material previousType = block.getType();
            Bukkit.getScheduler().runTask(plugin, () -> block.setType((Material) blockData));
            Class<?> iBlockDataClass = getNMSClass("IBlockData");

            // BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
            Class<?> blockPositionClass = getNMSClass("BlockPosition");
            if (blockPositionClass == null) {
                try {
                    throw new ClassNotFoundException("BlockPosition");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return 0;
            }
            ReflObject<?> blockPosition = new ReflObject<>(blockPositionClass.getCanonicalName(),
                    block.getX(), block.getY(), block.getZ());

            // WorldServer worldServer = block.getWorld.getHandle();
            ReflObject<?> worldServer = new ReflObject<>(block.getWorld()).callMethod("getHandle");
            // Object type = worldServer.getType(blockPosition);
            ReflObject<?> type = worldServer.callMethod("getType", blockPosition);

            // Material material = type.getMaterial();
            ReflObject<?> nmsMaterial = type.callMethodFromReturnType(Material.class);
            /*if (VersionsUtil.is1_9()) {
                nmsMaterial = invokeMethod(type, getMethod(iBlockDataClass, "getMaterial"));
            } else if (VersionsUtil.is1_18()) {
                Object nmsBlock = invokeMethod(type, getMethod(iBlockDataClass, "b"));
                nmsMaterial = invokeMethod(nmsBlock, getMethod(nmsBlock.getClass(), "getMaterial"));
            } else {
                Object nmsBlock = invokeMethod(type, getMethod(iBlockDataClass, "getBlock"));
                nmsMaterial = invokeMethod(nmsBlock, getMethod(nmsBlock.getClass(), "getMaterial"));
            }*/

            ReflObject<?> materialMap = nmsMaterial.callMethod("r");
            Bukkit.getScheduler().runTask(plugin, () -> block.setType(previousType));
            return materialMap.getFieldObject(VersionsUtils.is1_12() ? "ac" : "L");
        }
        Object previousData = getBlockData(block);
        setBlockData(block, blockData);
        ReflObject<?> nms = new ReflObject<>(block).callMethod("getNMS");
        ReflObject<?> materialMap;
        if (VersionsUtils.is1_16()) {
            ReflObject<?> nmsBlock = nms.callMethod(VersionsUtils.is1_18() ? "b" : "getBlock");
            materialMap = nmsBlock.callMethod("s");
        } else {
            Class<?> iBlockAccess = getNMSClass("IBlockAccess");
            Class<?> blockPositionClass = getNMSClass("BlockPosition");
            if (blockPositionClass == null) return 0;
            Object worldServer = PacketsUtils.getWorldConnection(block.getWorld());

            Location blockLocation = block.getLocation();
            // BlockPosition blocKPosition = new BlockPosition(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
            ReflObject<?> blockPosition = new ReflObject<>(blockPositionClass.getCanonicalName(),
                    blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
            // Method c = getMethod(nms, "c", iBlockAccess, blockPositionClass);
            materialMap = nms.callMethod("c", worldServer, blockPosition);
        }
        Object color = materialMap.getFieldObject(VersionsUtils.is1_17() ? "al" : "rgb");
        setBlockData(block, previousData);
        return (int) color;
    }

    public static void setBlockData(Block block, Object blockData) {
        // block.setBlockData(blockData);
        new ReflObject<>(block).callMethod("setBlockData", blockData);
    }

    public static Object getBlockData(Block block) {
        // return block.getBlockData();
        return new ReflObject<>(block).getMethodObject("getBlockData");
    }
}