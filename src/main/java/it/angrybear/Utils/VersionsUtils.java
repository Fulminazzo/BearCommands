package it.angrybear.Utils;

import it.angrybear.Enums.BearLoggingMessage;
import it.angrybear.Interfaces.IBearPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VersionsUtils {

    /**
     * Check on which version your plugin is being run.
     * @param version: the number of the version.
     * @return true if the version matches or is higher than the one specified.
     */
    public static boolean is1_(int version) {
        try {
            Method method = VersionsUtils.class.getMethod("is1_" + version);
            method.setAccessible(true);
            return (boolean) method.invoke(VersionsUtils.class);
        } catch (NoSuchMethodException e) {
            IBearPlugin.logWarning(BearLoggingMessage.VERSION_NOT_FOUND, "%version%", String.valueOf(version));
            return false;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean is1_20() {
        return is_1_2x(0);
    }

    public static boolean is1_19() {
        return is_1_1x(9);
    }

    public static boolean is1_18() {
        return is_1_1x(8);
    }

    public static boolean is1_17() {
        return is_1_1x(7);
    }

    public static boolean is1_16() {
        return is_1_1x(6);
    }

    public static boolean is1_15() {
        return is_1_1x(5);
    }

    public static boolean is1_14() {
        return is_1_1x(4);
    }

    public static boolean is1_13() {
        return is_1_1x(3);
    }

    public static boolean is1_12() {
        return is_1_1x(2);
    }

    public static boolean is1_11() {
        return is_1_1x(1);
    }

    public static boolean is1_10() {
        return is_1_1x(0);
    }

    public static boolean is1_9() {
        return isAbsolute1_9() || is1_10();
    }

    public static boolean isAbsolute1_9() {
        return is_1_x(9);
    }

    public static boolean is1_8() {
        return isAbsolute1_8() || is1_9();
    }

    public static boolean isAbsolute1_8() {
        return is_1_x(8);
    }

    public static boolean is1_7() {
        return is_1_x(7);
    }

    private static boolean is_1_2x(int x) {
        String version = ServerUtils.getVersion();
        if (version.contains("1.2") && !version.contains("1.2.")) {
            int index = version.indexOf("1.2");
            return Integer.parseInt(version.substring(index + 2, index + 4)) >= (10 + x);
        } else return false;
    }

    private static boolean is_1_1x(int x) {
        String version = ServerUtils.getVersion();
        if (is1_20()) return true;
        if (version.contains("1.1") && !version.contains("1.1.")) {
            int index = version.indexOf("1.1");
            return Integer.parseInt(version.substring(index + 2, index + 4)) >= (10 + x);
        } else return false;
    }
    
    private static boolean is_1_x(int x) {
        String version = ServerUtils.getVersion();
        if (version.contains("1.1") && !version.contains("1.1.")) return false;
        else {
            int index = version.indexOf("1.");
            return Integer.parseInt(version.substring(index + 2, index + 3)) >= x;
        }
    }

    public static boolean is_1_x_y(int x, int y) {
        String version = ServerUtils.getVersion();
        int index = version.indexOf("1.");
        if (index == -1) return false;
        version = version.substring(index + 2);
        if (version.isEmpty()) return false;
        index = version.indexOf(".");
        if (index == -1) return false;
        String xString = version.substring(0, index);
        if (Integer.parseInt(xString) != x) return false;
        if (y == 0) return true;
        version = version.substring(index + 1);
        String yString = version.substring(0, 1);
        return Integer.parseInt(yString) == y;
    }
}