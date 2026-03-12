package net.roxymc.jserialize.creator;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PropertyValue<T> {
    PropertyValue<?> NULL = of(null);

    static <T> PropertyValue<T> of(@Nullable T value) {
        return $ -> value;
    }

    @Nullable T get(@Nullable Object parent) throws Throwable;

    default Mutable<T> asMutable(String name) {
        if (this instanceof Mutable) {
            return (Mutable<T>) this;
        }

        throw new IllegalStateException("Expected property '" + name + "' to be a mutable value");
    }

    @FunctionalInterface
    interface Mutable<T> extends PropertyValue<T> {
        Mutable<?> NOOP = (parent, instance) -> instance;

        @Override
        default @Nullable T get(@Nullable Object parent) throws Throwable {
            return mutate(parent, null);
        }

        @Nullable T mutate(@Nullable Object parent, @Nullable T instance) throws Throwable;
    }
}
