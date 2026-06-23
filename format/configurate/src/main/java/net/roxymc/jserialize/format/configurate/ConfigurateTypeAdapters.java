package net.roxymc.jserialize.format.configurate;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.Objects;

final class ConfigurateTypeAdapters implements TypeAdapters {
    // TODO we should use TypeSerializers when MapLike is gone (see usages)
    final ConfigurationOptions options;
    private final TypeAdapters adapters;

    ConfigurateTypeAdapters(ConfigurationOptions options, TypeAdapters adapters) {
        this.options = options;
        this.adapters = adapters;
    }

    @Override
    public <T> TypeAdapter<T> get(TypeToken<T> type) {
        @SuppressWarnings("unchecked")
        TypeSerializer<T> serializer = (TypeSerializer<T>) options.serializers().get(type.getAnnotatedType());

        if (serializer instanceof TypeAdapterProvider) {
            return ((TypeAdapterProvider) serializer).adapters.getOrThrow(type);
        }

        Objects.requireNonNull(serializer);
        return new WrappedTypeSerializer<>(serializer);
    }

    @Override
    public <T> @Nullable KeyAdapter<T> getKey(TypeToken<T> type) {
        return adapters.getKey(type);
    }
}
