package net.roxymc.jserialize.util;

import org.jspecify.annotations.Nullable;

import java.io.IOException;

@FunctionalInterface
public interface IOFunction<T extends @Nullable Object, R extends @Nullable Object> {
    R apply(T t) throws IOException;
}
