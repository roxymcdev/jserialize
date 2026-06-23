package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeRef;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

final class BsonTypeAdapters implements TypeAdapters {
    private final CodecRegistry registry;
    private final TypeAdapters adapters;

    BsonTypeAdapters(CodecRegistry registry, TypeAdapters adapters) {
        this.registry = registry;
        this.adapters = adapters;
    }

    private <T> Codec<T> getCodec(TypeRef<? extends T> typeRef) {
        Type type = typeRef.getType();
        Class<?> rawType = typeRef.getRawType();

        if (!(type instanceof ParameterizedType)) {
            @SuppressWarnings("unchecked")
            Codec<T> codec = (Codec<T>) registry.get(rawType);
            return codec;
        }

        List<Type> typeArgs = List.of(((ParameterizedType) type).getActualTypeArguments());

        @SuppressWarnings("unchecked")
        Codec<T> codec = (Codec<T>) registry.get(rawType, typeArgs);
        return codec;
    }

    @Override
    public <T> TypeAdapter<T> get(TypeRef<T> type) {
        Codec<T> codec = getCodec(type);

        if (codec instanceof WrappedTypeAdapter) {
            return ((WrappedTypeAdapter<T>) codec).typeAdapter;
        }

        return new WrappedCodec<>(codec);
    }

    @Override
    public <T> @Nullable KeyAdapter<T> getKey(TypeRef<T> type) {
        return adapters.getKey(type);
    }
}
