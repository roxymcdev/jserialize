package net.roxymc.jserialize.util;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T extends @Nullable Object> {
    void accept(T t) throws IOException;
}
