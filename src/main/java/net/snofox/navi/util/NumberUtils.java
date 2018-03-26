package net.snofox.navi.util;

public class NumberUtils {
    public static int clamp(final int value, final int min, final int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static long clamp(final long value, final long min, final long max) {
        return Math.max(min, Math.min(max, value));
    }

    public static String millisToTimestamp(final long millis) {
        long seconds, minutes, hours;
        seconds = millis / 1000 % 60;
        minutes = millis / (1000 * 60) % 60;
        hours = millis / (1000 * 60 * 60) % 24;
        StringBuilder sb = new StringBuilder();
        if(hours > 0) {
            sb.append(hours);
            sb.append(':');
        }
        sb.append(String.format("%02d", minutes));
        sb.append(':');
        sb.append(String.format("%02d", seconds));
        return sb.toString();
    }
}
