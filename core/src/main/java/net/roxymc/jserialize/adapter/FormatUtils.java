package net.roxymc.jserialize.adapter;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

public interface FormatUtils<R> {
    default @Nullable String idPropertyName() {
        return null;
    }

    Class<R> rawType();

    MapLike<R> createMap(Type mapType);
}
