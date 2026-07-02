package net.roxymc.jserialize.adapter.map;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.*;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class MapAdapter<K, V> implements TypeAdapter.Mutable<Map<@Nullable K, @Nullable V>> {
    private static final Factory FACTORY = factory(DefaultMapProvider.INSTANCE);

    private final MapType<K, V> mapType;
    private final MapProvider[] providers;

    public MapAdapter(TypeRef<? extends Map<K, V>> mapType, MapProvider[] providers) {
        this.mapType = new MapType<>(mapType);
        this.providers = providers;
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(MapProvider... providers) {
        MapProvider[] providers0 = nonNull(providers, "providers").clone();

        @SuppressWarnings({"unchecked", "rawtypes"})
        Factory factory = Factory.polymorphic(Map.class, type -> new MapAdapter(type, providers0));
        return factory;
    }

    @Override
    public @Nullable Map<@Nullable K, @Nullable V> mutate(Reader reader, @Nullable Map<@Nullable K, @Nullable V> map, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();

            if (map != null) {
                map.clear();
            }

            return map;
        }

        if (map == null) {
            map = mapType.createMap(providers);
        }

        KeyDecoder<K> keyDecoder = ctx.typeAdapters().getKeyOrThrow(mapType.keyType);
        TypeReader<V> valueReader = ctx.typeAdapters().getOrThrow(mapType.valueType);

        reader.readObjectStart();

        while (reader.peek() == TokenTypes.NAME) {
            String name = reader.readName();

            K key = keyDecoder.decode(name);
            V value = valueReader.read(reader, ctx.withKey(name));

            map.put(key, value);
        }

        reader.readObjectEnd();

        return map;
    }

    @Override
    public void write(Writer writer, @Nullable Map<@Nullable K, @Nullable V> map, WriteContext ctx) throws IOException {
        if (map == null) {
            writer.writeNull();
            return;
        }

        KeyEncoder<K> keyEncoder = ctx.typeAdapters().getKeyOrThrow(mapType.keyType);
        TypeWriter<V> valueWriter = ctx.typeAdapters().getOrThrow(mapType.valueType);

        writer.writeObjectStart();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            String name = keyEncoder.encode(entry.getKey());
            writer.writeName(name);

            valueWriter.write(writer, entry.getValue(), ctx);
        }

        writer.writeObjectEnd();
    }

    @Override
    public TypeRef<? extends Map<@Nullable K, @Nullable V>> type() {
        return mapType.mapType;
    }
}
