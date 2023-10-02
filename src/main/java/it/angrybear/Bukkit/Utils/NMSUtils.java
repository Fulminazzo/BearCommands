package it.angrybear.Bukkit.Utils;

import it.angrybear.Bukkit.Objects.Reflections.NMSReflObject;
import it.angrybear.Utils.VersionsUtils;
import it.fulminazzo.reflectionutils.Objects.ReflObject;
import it.fulminazzo.reflectionutils.Utils.ReflUtil;
import org.bukkit.Bukkit;

public class NMSUtils {
    /**
     * As the name suggests, this function tries to convert a CraftBukkit class
     * to its corresponding Spigot class.
     * An example would be the CraftPlayer that corresponds to Player.
     * @param craftClass: the CraftBukkit class.
     * @return Spigot class (if found).
     */
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

    /**
     * Returns the corresponding "org.bukkit." + className Bukkit class.
     * @param className: the name or path of the class.
     * @return the class (if found).
     */
    public static Class<?> getBukkitClass(String className) {
        return ReflUtil.getClass("org.bukkit." + className);
    }

    /**
     * Returns the corresponding NMS class.
     * @param className: the name of the class.
     * @return the class (if found).
     */
    public static Class<?> getNMSClass(String className) {
        return getNMSClass(className, "");
    }

    /**
     * Returns the corresponding NMS class.
     * @param className: the name of the class.
     * @param className117: the name of the class if the server is in 1.17 or above.
     * @return the class (if found).
     */
    public static Class<?> getNMSClass(String className, String className117) {
        String classPath = "net.minecraft.";
        if (VersionsUtils.is1_17()) {
            if (className117 != null) {
                classPath += className117;
                if (!className117.isEmpty() && !className117.endsWith(".")) classPath += ".";
            }
        } else classPath += "server." + getVersion() + ".";
        return ReflUtil.getClass(classPath + className);
    }

    /**
     * Returns the corresponding "org.bukkit.craftbukkit." + version + "." + className CraftBukkit class.
     * @param className: the name of the class.
     * @return the class(if found).
     */
    public static Class<?> getCraftBukkitClass(String className) {
        return ReflUtil.getClass(getCraftBukkitPath() + "." + className);
    }

    /**
     * Returns the current CraftBukkit path according to the server version.
     * @return the path.
     */
    public static String getCraftBukkitPath() {
        return "org.bukkit.craftbukkit." + getVersion();
    }

    /**
     * Returns the version of the server.
     * (For better precision, you should check VersionsUtils).
     * @return the version.
     */
    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Returns a ChatSerializer object, useful in utilities like Actionbars.
     * @param text: the text to be serialized.
     * @return a ReflObject containing the serialized text.
     */
    public static ReflObject<?> getChatSerializerObject(String text) {
        String formattedText = "{\"text\": \"" + text + "\"}";
        return new NMSReflObject<>("IChatBaseComponent.ChatSerializer", "network.chat", false)
                .callMethod("a", formattedText);
    }
}