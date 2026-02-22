package net.roxymc.jserialize.adapter;

import java.util.Map;

public interface MapLike {
    void put(String key, Object value);

    void putAll(Map<?, ?> map) throws Throwable;

    Map<?, ?> asMap() throws Throwable;

    Map<String, ?> asRawMap();
}
