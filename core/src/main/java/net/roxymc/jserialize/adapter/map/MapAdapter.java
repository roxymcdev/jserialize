package net.roxymc.jserialize.adapter.map;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

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

    private <K, V> MapType<K, V> resolveMapType(TypeToken<? extends Map<?, ?>> type) {
        @SuppressWarnings("unchecked")
        MapType<K, V> mapType = (MapType<K, V>) cache.computeIfAbsent(type.getType(), $ -> new MapType<>(type));
        return mapType;
    }

    @Override
    public @Nullable Map<?, ?> mutate(
            Reader reader, TypeToken<? extends Map<?, ?>> type, @Nullable Map<?, ?> value, ReadContext ctx
    ) throws IOException {
        return mutate0(reader, type, value, ctx);
    }

    private <K extends @Nullable Object, V extends @Nullable Object> @Nullable Map<K, V> mutate0(
            Reader reader, TypeToken<? extends Map<?, ?>> type, @Nullable Map<K, V> map, ReadContext ctx
    ) throws IOException {
        if (reader.peek() == TokenType.NULL) {
            reader.readNull();
            return map;
        }

        MapType<K, V> mapType = resolveMapType(type);

        if (map == null) {
            map = mapType.createMap(providers);
        }

        KeyAdapter<@NonNull K> keyAdapter = mapType.keyAdapter(ctx.typeAdapters());
        TypeAdapter<@NonNull V> valueAdapter = mapType.valueAdapter(ctx.typeAdapters());

        reader.readObjectStart();

        while (reader.peek() == TokenType.NAME) {
            String name = reader.readName();

            K key = keyAdapter.decode(name);
            V value = valueAdapter.read(reader, mapType.valueType, ctx.withKey(name));

            map.put(key, value);
        }

        reader.readObjectEnd();

        return map;
    }

    @Override
    public void write(
            Writer writer, TypeToken<? extends Map<?, ?>> type, @Nullable Map<?, ?> value, WriteContext ctx
    ) throws IOException {
        write0(writer, type, value, ctx);
    }

    private <K extends @Nullable Object, V extends @Nullable Object> void write0(
            Writer writer, TypeToken<? extends Map<?, ?>> type, @Nullable Map<K, V> map, WriteContext ctx
    ) throws IOException {
        if (map == null) {
            writer.writeNull();
            return;
        }

        MapType<K, V> mapType = resolveMapType(type);

        KeyAdapter<@NonNull K> keyAdapter = mapType.keyAdapter(ctx.typeAdapters());
        TypeAdapter<@NonNull V> valueAdapter = mapType.valueAdapter(ctx.typeAdapters());

        writer.writeObjectStart();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            String name = keyAdapter.encode(entry.getKey());
            writer.writeName(name);

            valueAdapter.write(writer, mapType.valueType, entry.getValue(), ctx);
        }

        writer.writeObjectEnd();
    }

}
