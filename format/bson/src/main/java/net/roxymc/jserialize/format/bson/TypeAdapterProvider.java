package net.roxymc.jserialize.format.bson;

import io.leangen.geantyref.TypeFactory;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class TypeAdapterProvider implements CodecProvider {
    private final TypeAdapters adapters;

    public TypeAdapterProvider(TypeAdapters adapters) {
        this.adapters = nonNull(adapters, "adapters");
    }

    @Override
    public <T> @Nullable Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return get(TypeToken.of(clazz), registry);
    }

    @Override
    public <T> @Nullable Codec<T> get(Class<T> clazz, List<Type> typeArguments, CodecRegistry registry) {
        Type[] typeArgs = typeArguments.toArray(Type[]::new);
        Type type = TypeFactory.parameterizedClass(clazz, typeArgs);

        return get(TypeToken.of(type), registry);
    }

    private <T> @Nullable Codec<T> get(TypeToken<? extends T> type, CodecRegistry registry) {
        TypeAdapter<T> adapter = this.adapters.get(type);
        if (adapter == null) {
            return null;
        }

        BsonTypeAdapters adapters = new BsonTypeAdapters(registry);
        return new WrappedTypeAdapter<>(type, adapter, adapters);
    }
}
