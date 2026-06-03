package net.roxymc.jserialize.creator;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PropertyValue<T> {
    PropertyValue<?> NULL = of(null);

    static <T> PropertyValue<T> of(@Nullable T value) {
        return $ -> value;
    }

    @Nullable T get(@Nullable Object parent) throws Throwable;

    default @Nullable T getSafe(String name, @Nullable Object parent) {
        try {
            return get(parent);
        } catch (Throwable ex) {
            throw new RuntimeException("Failed to process property: " + name, ex);
        }
    }

    @FunctionalInterface
    interface Mutable<T> extends PropertyValue<T> {
        Mutable<?> NOOP = (parent, instance) -> instance;

        @Override
        default @Nullable T get(@Nullable Object parent) throws Throwable {
            return mutate(parent, null);
        }

        @Nullable T mutate(@Nullable Object parent, @Nullable T instance) throws Throwable;

        default @Nullable T mutateSafe(String name, @Nullable Object parent, @Nullable T instance) {
            try {
                return mutate(parent, instance);
            } catch (Throwable ex) {
                throw new RuntimeException("Failed to process property: " + name, ex);
            }
        }
    }
}
