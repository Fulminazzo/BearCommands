package it.angrybear.utils;

public class VersionsUtils {

    /**
     * Checks which version your plugin is being run on.
     * @param version the number of the version
     * @return true if the version matches or is higher than the one specified
     */
    public static boolean is1_(int version) {
        //TODO: Fix
        //String serverVersion = Bukkit.getBukkitVersion().split("-")[0];
        String serverVersion = "Hello";
        serverVersion = serverVersion.substring(serverVersion.indexOf(".") + 1);
        serverVersion = serverVersion.substring(0, serverVersion.indexOf("."));
        return version <= Integer.parseInt(serverVersion);
    }

    public static boolean is1_20() {
        return is1_(20);
    }

    public static boolean is1_19() {
        return is1_(19);
    }

    public static boolean is1_18() {
        return is1_(18);
    }

    public static boolean is1_17() {
        return is1_(17);
    }

    public static boolean is1_16() {
        return is1_(16);
    }

    public static boolean is1_15() {
        return is1_(15);
    }

    public static boolean is1_14() {
        return is1_(14);
    }

    public static boolean is1_13() {
        return is1_(13);
    }

    public static boolean is1_12() {
        return is1_(12);
    }

    public static boolean is1_11() {
        return is1_(11);
    }

    public static boolean is1_10() {
        return is1_(10);
    }

    public static boolean is1_9() {
        return is1_(9);
    }

    public static boolean is1_8() {
        return is1_(8);
    }

    public static boolean is1_7() {
        return is1_(7);
    }
}