package net.roxymc.jserialize.util;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

@FunctionalInterface
public interface IOBiConsumer<T extends @Nullable Object, U extends @Nullable Object> {
    void accept(T t, U u) throws IOException;
}
