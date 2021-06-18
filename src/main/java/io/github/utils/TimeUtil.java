package io.github.utils;

public class TimeUtil {
    public static String formatTimeMillis(final long millis) {
        long seconds = millis / 1000L;

        if (seconds <= 0) {
            return "0 seconds";
        }

        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long day = hours / 24;
        hours = hours % 24;
        final long years = day / 365;
        day = day % 365;

        final StringBuilder time = new StringBuilder();

        if (years != 0) {
            time.append(years).append(years == 1 ? "y " : "y ");
        }

        if (day != 0) {
            time.append(day).append(day == 1 ? "d " : "d ");
        }

        if (hours != 0) {
            time.append(hours).append(hours == 1 ? "h " : "h ");
        }

        if (minutes != 0) {
            time.append(minutes).append(minutes == 1 ? "m " : "m ");
        }

        if (seconds != 0) {
            time.append(seconds).append(seconds == 1 ? "s " : "s ");
        }

        return time.toString().trim();
    }
}
