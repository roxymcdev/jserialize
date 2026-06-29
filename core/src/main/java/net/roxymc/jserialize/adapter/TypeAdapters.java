package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.array.ArrayAdapter;
import net.roxymc.jserialize.adapter.atomic.AtomicAdapters;
import net.roxymc.jserialize.adapter.collection.CollectionAdapter;
import net.roxymc.jserialize.adapter.map.MapAdapter;
import net.roxymc.jserialize.adapter.object.ObjectAdapter;
import net.roxymc.jserialize.adapter.scalar.EnumAdapter;
import net.roxymc.jserialize.adapter.scalar.EnumKeyAdapter;
import net.roxymc.jserialize.adapter.scalar.ScalarAdapters;
import net.roxymc.jserialize.adapter.scalar.ScalarKeyAdapters;
import net.roxymc.jserialize.adapter.temporal.TemporalAdapters;
import net.roxymc.jserialize.type.TypeRef;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface TypeAdapters extends TypeAdapter.Factory, KeyAdapter.Factory {
    TypeAdapters DEFAULT = builder()
            .add(ScalarAdapters.factory())
            .add(EnumAdapter.factory())
            .add(AtomicAdapters.factory())
            .add(TemporalAdapters.factory())
            .add(ArrayAdapter.factory())
            .add(CollectionAdapter.factory())
            .add(MapAdapter.factory())
            .add(ObjectAdapter.annotatedFactory())
            .addKey(ScalarKeyAdapters.factory())
            .addKey(EnumKeyAdapter.factory())
            .build();

    static Builder builder() {
        return new TypeAdaptersImpl.BuilderImpl();
    }

    default <T> @Nullable TypeAdapter<T> get(Class<T> type) {
        return get(TypeRef.of(type));
    }

    <T> @Nullable TypeAdapter<T> get(TypeRef<T> type);

    default <T> TypeAdapter<T> getOrThrow(Class<T> type) {
        return getOrThrow(TypeRef.of(type));
    }

    default <T> TypeAdapter<T> getOrThrow(TypeRef<T> type) {
        TypeAdapter<T> adapter = get(type);
        if (adapter != null) {
            return adapter;
        }

        throw new IllegalStateException("Could not find type adapter for " + type.getAnnotatedType());
    }

    default <T> @Nullable KeyAdapter<T> getKey(Class<T> type) {
        return getKey(TypeRef.of(type));
    }

    <T> @Nullable KeyAdapter<T> getKey(TypeRef<T> type);

    default <T> KeyAdapter<T> getKeyOrThrow(Class<T> type) {
        return getKeyOrThrow(TypeRef.of(type));
    }

    default <T> KeyAdapter<T> getKeyOrThrow(TypeRef<T> type) {
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

        Builder addAll(TypeAdapters adapters);

        TypeAdapters build();
    }
}
