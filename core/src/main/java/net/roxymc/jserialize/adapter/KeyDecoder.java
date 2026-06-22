package net.roxymc.jserialize.adapter;

import org.jspecify.annotations.Nullable;

public interface KeyDecoder<T> {
    @Nullable T decode(@Nullable String value);
}
