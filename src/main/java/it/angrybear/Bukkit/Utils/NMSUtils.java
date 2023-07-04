package it.angrybear.Bukkit.Utils;

import it.angrybear.Bukkit.Objects.Reflections.NMSReflObject;
import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.Bukkit;

public class NMSUtils {
    public static Class<?> convertCraftClassToSpigotClass(Class<?> craftClass) {
        if (craftClass == null) return null;
        String classPath = craftClass.getCanonicalName();
        String craftPath = getCraftBukkitPath();
        if (!classPath.contains(craftPath)) return craftClass;
        classPath = classPath.substring(craftPath.length());
        String[] tmp = classPath.split("\\.");
        String last = tmp[tmp.length - 1];
        String inventory = "Inventory";
        if (last.startsWith("Craft")) last = last.substring("Craft".length());
        if (last.equals("InventoryCustom")) last = inventory;
        if (last.startsWith(inventory)) last = last.substring(inventory.length()) + inventory;
        tmp[tmp.length - 1] = last;
        return ReflUtil.getClass("org.bukkit" + String.join(".", tmp));
    }

    public static Class<?> getBukkitClass(String className) {
        return ReflUtil.getClass("org.bukkit." + className);
    }

    public static Class<?> getNMSClass(String className) {
        return getNMSClass(className, "");
    }

    public static Class<?> getNMSClass(String className, String className117) {
        String classPath = "net.minecraft.";
        if (VersionsUtils.is1_17()) {
            if (className117 != null) {
                classPath += className117;
                if (!className117.equals("") && !className117.endsWith(".")) classPath += ".";
            }
        } else classPath += "server." + getVersion() + ".";
        return ReflUtil.getClass(classPath + className);
    }

    public static Class<?> getCraftBukkitClass(String className) {
        return ReflUtil.getClass(getCraftBukkitPath() + "." + className);
    }

    public static String getCraftBukkitPath() {
        return "org.bukkit.craftbukkit." + getVersion();
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static ReflObject<?> getChatSerializerObject(String text) {
        String formattedText = "{\"text\": \"" + text + "\"}";
        return new NMSReflObject<>("IChatBaseComponent.ChatSerializer", "network.chat", false)
                .callMethod("a", formattedText);
    }
}