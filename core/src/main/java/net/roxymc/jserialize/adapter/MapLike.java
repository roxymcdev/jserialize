package net.roxymc.jserialize.adapter;

import java.util.Map;

public interface MapLike<R> {
    void put(String key, R value);

    void putAll(Map<?, ?> map) throws Throwable;

    Map<?, ?> asMap() throws Throwable;

    Map<String, R> asRawMap();
}
