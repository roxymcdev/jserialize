package net.roxymc.jserialize.adapter.map;

import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class DefaultMapProvider implements MapProvider {
    public static final DefaultMapProvider INSTANCE = new DefaultMapProvider();

    private DefaultMapProvider() {
    }

    @Override
    public <K extends @Nullable Object, V extends @Nullable Object> @Nullable MapFactory<K, V> resolve(TypeToken<? extends Map<K, V>> type) {
        Class<?> rawType = type.getRawType();

        if (rawType.isAssignableFrom(LinkedHashMap.class)) {
            return LinkedHashMap::new;
        }

        if (rawType.isAssignableFrom(TreeMap.class)) {
            return TreeMap::new;
        }

        if (rawType.isAssignableFrom(ConcurrentHashMap.class)) {
            return ConcurrentHashMap::new;
        }

        if (rawType.isAssignableFrom(ConcurrentSkipListMap.class)) {
            return ConcurrentSkipListMap::new;
        }

        return null;
    }
}
