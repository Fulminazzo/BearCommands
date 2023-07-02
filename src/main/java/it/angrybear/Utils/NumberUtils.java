package it.angrybear.Utils;

public class NumberUtils {
    public static Boolean isNatural(String string) {
        try {
            return Integer.parseInt(string) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static Boolean isNaturalLong(String string) {
        try {
            return Long.parseLong(string) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Boolean isLong(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static Boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Boolean isFloat(String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
