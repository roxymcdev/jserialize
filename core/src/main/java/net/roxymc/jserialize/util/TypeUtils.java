package net.roxymc.jserialize.util;

import net.roxymc.jserialize.type.TypeToken;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static boolean isPrimitive(TypeToken<?> type) {
        return isPrimitive(type.getRawType());
    }

    public static boolean isPrimitive(Class<?> type) {
        return !Object.class.isAssignableFrom(type);
    }

    public static boolean isRecord(Class<?> type) {
        Class<?> supertype = type.getSuperclass();
        return supertype != null && supertype.getName().equals("java.lang.Record");
    }
}
