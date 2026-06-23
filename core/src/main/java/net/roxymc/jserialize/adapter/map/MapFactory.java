package net.roxymc.jserialize.adapter.map;

import org.jspecify.annotations.Nullable;

import java.util.Map;

@FunctionalInterface
public interface MapFactory<K extends @Nullable Object, V extends @Nullable Object> {
    Map<K, V> create();
}
