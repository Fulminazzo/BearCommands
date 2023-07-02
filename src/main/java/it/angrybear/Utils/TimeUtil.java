package it.angrybear.Utils;

public class TimeUtil {
    
    public static String parseTime(long timeInSeconds, String timeFormat, String splitter,
                                   String seconds, String second,
                                   String minutes, String minute,
                                   String hours, String hour,
                                   String days, String day,
                                   String months, String month,
                                   String years, String year) {
        String message = formatTime(null, timeFormat, splitter, timeInSeconds / getSecondsInYear(), years, year);
        timeInSeconds %= getSecondsInYear();
        message = formatTime(message, timeFormat, splitter, timeInSeconds / getSecondsInMonth(), months, month);
        timeInSeconds %= getSecondsInMonth();
        message = formatTime(message, timeFormat, splitter, timeInSeconds / getSecondsInDay(), days, day);
        timeInSeconds %= getSecondsInDay();
        message = formatTime(message, timeFormat, splitter, timeInSeconds / getSecondsInHour(), hours, hour);
        timeInSeconds %= getSecondsInHour();
        message = formatTime(message, timeFormat, splitter, timeInSeconds / getSecondsInMinute(), minutes, minute);
        timeInSeconds %= getSecondsInMinute();
        message = formatTime(message, timeFormat, splitter, timeInSeconds, seconds, second);
        return message;
    }
    
    private static String formatTime(String message, String timeFormat, String splitter, 
                              long timeInSeconds, String timeUnitPlural, String timeUnitSingular) {
        if (timeInSeconds < 1) return message;
        if (timeUnitPlural == null && timeUnitSingular == null) return message;
        String unit = timeUnitPlural;
        if (unit == null || timeInSeconds == 1) unit = timeUnitSingular;
        if (unit == null) unit = timeUnitPlural;
        if (message == null) message = "";
        if (!message.equals("")) message += splitter;
        return message + timeFormat
                .replace("%time%", String.valueOf(timeInSeconds))
                .replace("%time-unit%", unit);
    }
    
    public static long getSecondsInYear() {
        return 12 * getSecondsInMonth();
    }
    
    public static long getSecondsInMonth() {
        return 30 * getSecondsInDay();
    }
    
    public static long getSecondsInDay() {
        return 24 * getSecondsInHour();
    }
    
    public static long getSecondsInHour() {
        return 60 * getSecondsInMinute();
    }
    
    public static long getSecondsInMinute() {
        return 60;
    }
}