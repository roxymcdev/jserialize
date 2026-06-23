package net.roxymc.jserialize.adapter.map;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;
import net.roxymc.jserialize.util.VarHandles;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Map;

import static io.leangen.geantyref.GenericTypeReflector.capture;
import static io.leangen.geantyref.GenericTypeReflector.getExactSuperType;

final class MapType<K extends @UnknownNullability Object, V extends @UnknownNullability Object> {
    private static final VarHandle MAP_FACTORY_HANDLE = VarHandles.find(MapType.class, "mapFactory", MapFactory.class);

    final TypeToken<? extends Map<K, V>> mapType;
    final TypeToken<K> keyType;
    final TypeToken<V> valueType;
    private @Nullable MapFactory<K, V> mapFactory;

    MapType(TypeToken<? extends Map<?, ?>> mapType) {
        AnnotatedType type = getExactSuperType(capture(mapType.getAnnotatedType()), Map.class);
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(mapType.getRawType() + " must be parameterized");
        }

        AnnotatedParameterizedType ptype = (AnnotatedParameterizedType) type;

        this.mapType = TypeToken.of(ptype);
        this.keyType = TypeToken.of(ptype.getAnnotatedActualTypeArguments()[0]);
        this.valueType = TypeToken.of(ptype.getAnnotatedActualTypeArguments()[1]);
    }

    KeyAdapter<@NonNull K> keyAdapter(TypeAdapters adapters) {
        return adapters.getKeyOrThrow(keyType);
    }

    TypeAdapter<@NonNull V> valueAdapter(TypeAdapters adapters) {
        return adapters.getOrThrow(valueType);
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
