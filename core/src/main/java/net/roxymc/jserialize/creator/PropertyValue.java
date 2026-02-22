package net.roxymc.jserialize.creator;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PropertyValue<T> {
    PropertyValue<?> NULL = of(null);

    static <T> PropertyValue<T> of(@Nullable T value) {
        return ($0, $1) -> value;
    }

    @Nullable T get(@Nullable Object parent, @Nullable T instance) throws Throwable;
}
