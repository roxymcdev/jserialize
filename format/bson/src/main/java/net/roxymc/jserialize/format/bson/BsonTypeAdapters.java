package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

final class BsonTypeAdapters implements TypeAdapters {
    private final CodecRegistry registry;

    BsonTypeAdapters(CodecRegistry registry) {
        this.registry = registry;
    }

    private <T> Codec<T> getCodec(TypeToken<? extends T> typeToken) {
        Type type = typeToken.getType();
        Class<?> rawType = typeToken.getRawType();

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
    public <T> TypeAdapter<T> get(TypeToken<? extends T> type) {
        Codec<T> codec = getCodec(type);

        if (codec instanceof WrappedTypeAdapter) {
            return ((WrappedTypeAdapter<T>) codec).typeAdapter;
        }

        return new WrappedCodec<>(codec);
    }
}
