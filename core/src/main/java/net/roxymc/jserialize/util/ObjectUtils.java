package net.roxymc.jserialize.util;

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.StringJoiner;

public final class ObjectUtils {
    private ObjectUtils() {
    }

    public static <T> T nonNull(@Nullable T obj, String name) {
        return Objects.requireNonNull(obj, name + " == null");
    }

    public static StringJoiner toString(Object obj) {
        return toString(obj.getClass());
    }

    public static StringJoiner toString(Class<?> clazz) {
        return new StringJoiner(", ", clazz.getSimpleName() + "[", "]");
    }
}
