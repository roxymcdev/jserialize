package net.roxymc.jserialize.util;

public final class NumberUtils {
    private NumberUtils() {
    }

    public static boolean equals(double a, double b) {
        return Double.doubleToRawLongBits(a) == Double.doubleToRawLongBits(b);
    }
}
