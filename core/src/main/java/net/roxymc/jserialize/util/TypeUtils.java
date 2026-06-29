package net.roxymc.jserialize.util;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static boolean isEnum(Class<?> type) {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }
}
