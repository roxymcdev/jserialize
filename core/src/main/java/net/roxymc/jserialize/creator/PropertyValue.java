package net.roxymc.jserialize.creator;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface PropertyValue<T> {
    PropertyValue<?> NULL = of(null);

    static <T> PropertyValue<T> of(@Nullable T value) {
        return $ -> value;
    }

    @Nullable T get(@Nullable Object parent) throws Throwable;

    default Mutating<T> asMutating(String name) {
        if (this instanceof Mutating) {
            return (Mutating<T>) this;
        }

        throw new IllegalStateException("Expected property '" + name + "' to be a mutating value");
    }

    @FunctionalInterface
    interface Mutating<T> extends PropertyValue<T> {
        @Override
        default T get(@Nullable Object parent) throws Throwable {
            return mutate(parent, null);
        }

        T mutate(@Nullable Object parent, @Nullable T instance) throws Throwable;
    }
}
