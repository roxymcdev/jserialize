package net.roxymc.jserialize.util;

public final class StringUtils {
    private StringUtils() {
    }

    public static String decapitalize(String s) {
        return s.isEmpty() ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static boolean hasPrefix(String s, String prefix) {
        return s.length() > prefix.length() && s.startsWith(prefix);
    }
}
