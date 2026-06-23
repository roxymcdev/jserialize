package net.roxymc.jserialize.adapter.map;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface MapProvider {
    <K extends @Nullable Object, V extends @Nullable Object> @Nullable MapFactory<K, V> resolve(TypeRef<? extends Map<K, V>> type);
}
