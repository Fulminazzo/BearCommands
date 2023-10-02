package it.angrybear.Utils;

public class TimeUtils {

    /**
     * Parses the given time as a string.
     * @param timeInSeconds: the time;
     * @param timeFormat: the format with which parse the time. Supports %time% and %time-unit% as placeholders;
     * @param splitter: the splitter to be used between two time units (for example a comma);
     * @param seconds: the word to be used as seconds (plural);
     * @param second: the word to be used as second (singular);
     * @param minutes: the word to be used as minutes (plural);
     * @param minute: the word to be used as minute (singular);
     * @param hours: the word to be used as hours (plural);
     * @param hour: the word to be used as hour (singular);
     * @param days: the word to be used as days (plural);
     * @param day: the word to be used as day (singular);
     * @param months: the word to be used as months (plural);
     * @param month: the word to be used as month (singular);
     * @param years: the word to be used as years (plural);
     * @param year: the word to be used as year (singular).
     * @return the formatted time as string.
     */
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

    /**
     * Formats the given time adding the result to a given string.
     * @param message: the message to start with;
     * @param timeFormat: the format with which parse the time. Supports %time% and %time-unit% as placeholders;
     * @param splitter: the splitter to be used between two time units (for example a comma);
     * @param timeInSeconds: the time;
     * @param timeUnitPlural: the unit of time (plural);
     * @param timeUnitSingular: the unit of time (singular).
     * @return the new string.
     */
    private static String formatTime(String message, String timeFormat, String splitter,
                              long timeInSeconds, String timeUnitPlural, String timeUnitSingular) {
        if (timeInSeconds < 1) return message;
        if (timeUnitPlural == null && timeUnitSingular == null) return message;
        String unit = timeUnitPlural;
        if (unit == null || timeInSeconds == 1) unit = timeUnitSingular;
        if (unit == null) unit = timeUnitPlural;
        if (message == null) message = "";
        if (!message.isEmpty()) message += splitter;
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