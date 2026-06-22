package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.map.MapAdapter;
import net.roxymc.jserialize.adapter.object.ObjectAdapter;
import net.roxymc.jserialize.type.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface TypeAdapters {
    TypeAdapters DEFAULT = builder()
            .add(MapAdapter.factory())
            .add(ObjectAdapter.factory())
            .build();

    static Builder builder() {
        return new TypeAdaptersImpl.BuilderImpl();
    }

    default <T> @Nullable TypeAdapter<T> get(Class<T> type) {
        return get(TypeToken.of(type));
    }

    <T> @Nullable TypeAdapter<T> get(TypeToken<T> type);

    default <T> TypeAdapter<T> getOrThrow(Class<T> type) {
        return getOrThrow(TypeToken.of(type));
    }

    default <T> TypeAdapter<T> getOrThrow(TypeToken<T> type) {
        TypeAdapter<T> adapter = get(type);
        if (adapter != null) {
            return adapter;
        }

        throw new IllegalStateException("Could not find type adapter for " + type.getAnnotatedType());
    }

    default <T> @Nullable KeyAdapter<T> getKey(Class<T> type) {
        return getKey(TypeToken.of(type));
    }

    <T> @Nullable KeyAdapter<T> getKey(TypeToken<T> type);

    default <T> KeyAdapter<T> getKeyOrThrow(Class<T> type) {
        return getKeyOrThrow(TypeToken.of(type));
    }

    default <T> KeyAdapter<T> getKeyOrThrow(TypeToken<T> type) {
        KeyAdapter<T> adapter = getKey(type);
        if (adapter != null) {
            return adapter;
        }

        throw new IllegalStateException("Could not find key adapter for " + type.getAnnotatedType());
    }

    @ApiStatus.NonExtendable
    interface Builder {
        Builder add(TypeAdapter.Factory factory);

        Builder addKey(KeyAdapter.Factory factory);

        TypeAdapters build();
    }
}
