package net.roxymc.jserialize.creator;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface PropertyValue<T> {
    Single<?> NULL = single(null);

    static <T> Single<T> single(@Nullable T value) {
        return $ -> value;
    }

    default Single<T> asSingle(String name) {
        if (this instanceof Single) {
            return (Single<T>) this;
        }

        throw new IllegalStateException("Expected property '" + name + "' to be a single value");
    }

    default Mutating<T> asMutating(String name) {
        if (this instanceof Mutating) {
            return (Mutating<T>) this;
        }

        throw new IllegalStateException("Expected property '" + name + "' to be a mutating value");
    }

    @FunctionalInterface
    interface Single<T> extends PropertyValue<T> {
        @Nullable T get(@Nullable Object parent) throws Throwable;
    }

    @FunctionalInterface
    interface Mutating<T> extends PropertyValue<T> {
        void mutate(@Nullable Object parent, T instance) throws Throwable;
    }
}
