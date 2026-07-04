package net.roxymc.jserialize.format.configurate;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializer;

final class ConfigurateTypeAdapters implements TypeAdapters {
    // TODO we should use TypeSerializers when MapLike is gone (see usages)
    final ConfigurationOptions options;
    private final TypeAdapters adapters;

    ConfigurateTypeAdapters(ConfigurationOptions options, TypeAdapters adapters) {
        this.options = options;
        this.adapters = adapters;
    }

    @Override
    public <T> @Nullable TypeAdapter<T> get(TypeRef<T> type) {
        @SuppressWarnings("unchecked")
        TypeSerializer<T> serializer = (TypeSerializer<T>) options.serializers().get(type.getAnnotatedType());

        if (serializer == null) {
            return null;
        }

        if (serializer instanceof TypeAdapterProvider) {
            return ((TypeAdapterProvider) serializer).adapters.getOrThrow(type);
        }

        return new WrappedTypeSerializer<>(type, serializer);
    }

    @Override
    public <T> @Nullable KeyAdapter<T> getKey(TypeRef<T> type) {
        return this.adapters.getKey(type);
    }

    @Override
    public <T> @Nullable TypeAdapter<T> create(TypeRef<T> type) {
        return get(type);
    }

    @Override
    public <T> @Nullable KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters) {
        return this.adapters.createKey(type, adapters);
    }
}
