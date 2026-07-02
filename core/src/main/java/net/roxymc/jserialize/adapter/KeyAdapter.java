package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public interface KeyAdapter<T> extends KeyDecoder<T>, KeyEncoder<T> {
    @Override
    @Nullable T decode(@Nullable String value);

    @Override
    String encode(@Nullable T value);

    TypeRef<? extends T> type();

    interface Factory {
        static <T> Factory where(Predicate<? super TypeRef<?>> predicate, TypedFactory<? super T> factory) {
            return new PredicateKeyAdapterFactory(predicate, factory);
        }

        static <T> Factory polymorphic(Class<T> type, TypedFactory<? super T> factory) {
            return where(subtype -> type.isAssignableFrom(subtype.getRawType()), factory);
        }

        static <T> Factory polymorphic(TypeRef<T> type, TypedFactory<? super T> factory) {
            return where(subtype -> GenericTypeReflector.isSuperType(type.getType(), subtype.getType()), factory);
        }

        static <T> Factory exact(KeyAdapter<T> adapter) {
            return Factory.<T>where(subtype -> adapter.type().getType().equals(subtype.getType()), ($1, $2) -> adapter);
        }

        static <T> Factory exactRaw(Class<T> type, TypedFactory<? super T> factory) {
            return where(subtype -> type.equals(subtype.getRawType()), factory);
        }

        static Factory composite(Factory... factories) {
            return new CompositeKeyAdapterFactory(factories);
        }

        <T> @Nullable KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters);
    }

    @FunctionalInterface
    interface TypedFactory<T> {
        @Nullable KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters);
    }
}
