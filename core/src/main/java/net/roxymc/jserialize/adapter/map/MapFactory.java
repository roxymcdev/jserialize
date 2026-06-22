package net.roxymc.jserialize.adapter.map;

import java.util.Map;

@FunctionalInterface
public interface MapFactory<K, V> {
    Map<K, V> create();
}
