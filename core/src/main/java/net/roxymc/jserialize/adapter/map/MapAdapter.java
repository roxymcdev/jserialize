package net.roxymc.jserialize.adapter.map;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.*;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;
import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

public final class MapAdapter implements TypeAdapter.Mutable<Map<?, ?>> {
    private static final TypeAdapter.Factory FACTORY = factory(DefaultMapProvider.INSTANCE);

    private final Map<Type, MapType<?, ?>> cache = new ConcurrentHashMap<>();
    private final MapProvider[] providers;

    public MapAdapter(MapProvider... providers) {
        this.providers = nonNull(providers, "providers").clone();
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(MapProvider... providers) {
        return TypeAdapter.Factory.polymorphic(Map.class, new MapAdapter(providers));
    }

    private <K, V> MapType<K, V> resolveMapType(TypeRef<? extends Map<?, ?>> type) {
        @SuppressWarnings("unchecked")
        MapType<K, V> mapType = (MapType<K, V>) cache.computeIfAbsent(type.getType(), $ -> new MapType<>(type));
        return mapType;
    }

    @Override
    public @Nullable Map<?, ?> mutate(
            Reader reader, TypeRef<? extends Map<?, ?>> type, @Nullable Map<?, ?> value, ReadContext ctx
    ) throws IOException {
        return mutate0(reader, type, value, ctx);
    }

    private <K extends @Nullable Object, V extends @Nullable Object> @Nullable Map<K, V> mutate0(
            Reader reader, TypeRef<? extends Map<?, ?>> type, @Nullable Map<K, V> map, ReadContext ctx
    ) throws IOException {
        checkAssignable(Map.class, type.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return map;
        }

        MapType<K, V> mapType = resolveMapType(type);

        if (map == null) {
            map = mapType.createMap(providers);
        }

        KeyDecoder<@NonNull K> keyDecoder = ctx.typeAdapters().getKeyOrThrow(mapType.keyType);
        TypeReader<@NonNull V> valueReader = ctx.typeAdapters().getOrThrow(mapType.valueType);

        reader.readObjectStart();

        while (reader.peek() == TokenTypes.NAME) {
            String name = reader.readName();

            K key = keyDecoder.decode(name);
            V value = valueReader.read(reader, mapType.valueType, ctx.withKey(name));

            map.put(key, value);
        }

        reader.readObjectEnd();

        return map;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends Map<?, ?>> type, @Nullable Map<?, ?> value, WriteContext ctx
    ) throws IOException {
        write0(writer, type, value, ctx);
    }

    private <K extends @Nullable Object, V extends @Nullable Object> void write0(
            Writer writer, TypeRef<? extends Map<?, ?>> type, @Nullable Map<K, V> map, WriteContext ctx
    ) throws IOException {
        checkAssignable(Map.class, type.getRawType());

        if (map == null) {
            writer.writeNull();
            return;
        }

        MapType<K, V> mapType = resolveMapType(type);

        KeyEncoder<@NonNull K> keyEncoder = ctx.typeAdapters().getKeyOrThrow(mapType.keyType);
        TypeWriter<@NonNull V> valueWriter = ctx.typeAdapters().getOrThrow(mapType.valueType);

        writer.writeObjectStart();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            String name = keyEncoder.encode(entry.getKey());
            writer.writeName(name);

            valueWriter.write(writer, mapType.valueType, entry.getValue(), ctx);
        }

        writer.writeObjectEnd();
    }
}
