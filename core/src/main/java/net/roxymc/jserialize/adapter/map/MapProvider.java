package net.roxymc.jserialize.adapter.map;

import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface MapProvider {
    <K, V> @Nullable MapFactory<K, V> resolve(TypeToken<? extends Map<K, V>> type);
}
