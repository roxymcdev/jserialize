package net.roxymc.jserialize.adapter;

import org.jspecify.annotations.Nullable;

public interface KeyEncoder<T> {
    String encode(@Nullable T value);
}
