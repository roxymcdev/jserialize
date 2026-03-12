package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.adapter.object.ObjectAdapter;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public interface TypeAdapters {
    TypeAdapters DEFAULT = builder()
            .add(ObjectAdapter.factory())
            .build();

    static Builder builder() {
        return new TypeAdaptersImpl.BuilderImpl();
    }

    default <T> @Nullable TypeAdapter<T> get(Class<? extends T> type) {
        return get(TypeToken.of(type));
    }

    <T> @Nullable TypeAdapter<T> get(TypeToken<? extends T> type);

    default <T> TypeAdapter<T> getOrThrow(Class<? extends T> type) {
        return getOrThrow(TypeToken.of(type));
    }

    default <T> TypeAdapter<T> getOrThrow(TypeToken<? extends T> type) {
        TypeAdapter<T> adapter = get(type);
        if (adapter != null) {
            return adapter;
        }

        throw new IllegalStateException("Could not find type adapter for " + type.getAnnotatedType());
    }

    interface Builder {
        default Builder add(Predicate<? super TypeToken<?>> predicate, TypeAdapter<?> adapter) {
            return add(TypeAdapter.Factory.predicate(predicate, adapter));
        }

        default <T> Builder add(Class<? extends T> type, TypeAdapter<T> adapter) {
            return add(TypeToken.of(type), adapter);
        }

        default <T> Builder add(TypeToken<? extends T> type, TypeAdapter<T> adapter) {
            return add(TypeAdapter.Factory.polymorphic(type, adapter));
        }

        default <T> Builder addExact(Class<? extends T> type, TypeAdapter<T> adapter) {
            return add(TypeToken.of(type), adapter);
        }

        default <T> Builder addExact(TypeToken<? extends T> type, TypeAdapter<T> adapter) {
            return add(TypeAdapter.Factory.exact(type, adapter));
        }

        Builder add(TypeAdapter.Factory factory);

        TypeAdapters build();
    }
}
