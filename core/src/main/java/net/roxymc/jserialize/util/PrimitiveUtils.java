package net.roxymc.jserialize.util;

import java.lang.reflect.Type;
import java.util.Map;

public final class PrimitiveUtils {
    private static final Map<Class<?>, ?> DEFAULT_VALUES = Map.of(
            boolean.class, false,
            byte.class, (byte) 0,
            char.class, '\0',
            double.class, 0d,
            float.class, 0f,
            int.class, 0,
            long.class, 0L,
            short.class, (short) 0
    );

    private PrimitiveUtils() {
    }

    public static boolean isPrimitive(Type type) {
        return type instanceof Class && ((Class<?>) type).isPrimitive();
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public static Object defaultValue(Type type) {
        if (!isPrimitive(type)) {
            throw new IllegalArgumentException("type is not primitive");
        }

        return DEFAULT_VALUES.get(type);
    }
}
