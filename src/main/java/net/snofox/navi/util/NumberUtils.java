package net.snofox.navi.util;

public class NumberUtils {
    public static int clamp(int value, final int min, final int max) {
        return Math.max(min, Math.min(max, value));
    }
}
