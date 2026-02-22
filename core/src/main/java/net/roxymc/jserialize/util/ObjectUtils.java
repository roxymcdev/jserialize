package net.roxymc.jserialize.util;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class ObjectUtils {
    private ObjectUtils() {
    }

    public static <T> T nonNull(@Nullable T obj, String name) {
        return Objects.requireNonNull(obj, name + " == null");
    }
}
