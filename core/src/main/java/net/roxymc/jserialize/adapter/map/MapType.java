package net.roxymc.jserialize.adapter.map;

import net.roxymc.jserialize.type.TypeRef;
import net.roxymc.jserialize.util.VarHandles;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AnnotatedParameterizedType;
import java.util.Map;

import static net.roxymc.jserialize.util.TypeUtils.resolveTypeParams;

final class MapType<K, V> {
    private static final VarHandle MAP_FACTORY_HANDLE = VarHandles.find(MapType.class, "mapFactory", MapFactory.class);

    final TypeRef<? extends Map<K, V>> mapType;
    final TypeRef<K> keyType;
    final TypeRef<V> valueType;

    private @Nullable MapFactory<K, V> mapFactory;

    MapType(TypeRef<? extends Map<K, V>> mapType) {
        AnnotatedParameterizedType ptype = resolveTypeParams(mapType, Map.class);

        this.mapType = mapType;
        this.keyType = TypeRef.of(ptype.getAnnotatedActualTypeArguments()[0]);
        this.valueType = TypeRef.of(ptype.getAnnotatedActualTypeArguments()[1]);
    }

    @SuppressWarnings("unchecked")
    Map<K, V> createMap(MapProvider[] providers) {
        MapFactory<K, V> value = (MapFactory<K, V>) MAP_FACTORY_HANDLE.getAcquire(this);

        if (value == null) {
            MapFactory<K, V> resolved = null;

            for (MapProvider provider : providers) {
                MapFactory<K, V> factory = provider.resolve(mapType);

                if (factory != null) {
                    resolved = factory;
                    break;
                }
            }

            if (resolved == null) {
                resolved = () -> {
                    throw new IllegalStateException("Could not find map factory for " + mapType.getType());
                };
            }

            MapFactory<K, V> witness = (MapFactory<K, V>) MAP_FACTORY_HANDLE.compareAndExchangeRelease(this, null, resolved);
            value = witness != null ? witness : resolved;
        }

        return value.create();
    }
}
