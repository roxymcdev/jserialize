package net.roxymc.jserialize.adapter;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

public interface FormatUtils {
    default @Nullable String idPropertyName() {
        return null;
    }

    Class<?> rawType();

    MapLike createMap(Type mapType);
}
