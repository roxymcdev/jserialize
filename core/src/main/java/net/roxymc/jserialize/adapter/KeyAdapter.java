package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.type.TypeRef;
import net.roxymc.jserialize.util.TypeUtils;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public interface KeyAdapter<T> extends KeyDecoder<T>, KeyEncoder<T> {
    static <T> KeyAdapter<T> of(KeyDecoder<T> decoder, KeyEncoder<T> encoder) {
        return new KeyAdapter<>() {
            @Override
            public @Nullable T decode(@Nullable String value) {
                return decoder.decode(value);
            }

            @Override
            public String encode(@Nullable T value) {
                return encoder.encode(value);
            }
        };
    }

    @Override
    @Nullable T decode(@Nullable String value);

    @Override
    String encode(@Nullable T value);

    interface Factory {
        static Factory predicate(Predicate<? super TypeRef<?>> predicate, KeyAdapter<?> adapter) {
            return new PredicateKeyAdapterFactory(predicate, adapter);
        }

        static <T> Factory polymorphic(Class<? super T> type, KeyAdapter<T> adapter) {
            return predicate(subtype -> type.isAssignableFrom(subtype.getRawType()), adapter);
        }

        static <T> Factory polymorphic(TypeRef<? extends T> type, KeyAdapter<T> adapter) {
            return predicate(subtype -> GenericTypeReflector.isSuperType(type.getType(), subtype.getType()), adapter);
        }

        static <T> Factory exact(TypeRef<? extends T> type, KeyAdapter<T> adapter) {
            return predicate(subtype -> type.getType().equals(subtype.getType()), adapter);
        }

        static <T> Factory exactRaw(Class<? super T> type, KeyAdapter<T> adapter) {
            return predicate(subtype -> type.equals(subtype.getRawType()), adapter);
        }

        static <T> Factory exactBoxed(Class<? super T> type, KeyAdapter<T> adapter) {
            if (TypeUtils.isPrimitive(type)) {
                throw new IllegalArgumentException("type cannot be primitive");
            }

            return predicate(subtype -> type.equals(GenericTypeReflector.box(subtype.getRawType())), adapter);
        }

        static Factory composite(Factory... factories) {
            return new CompositeKeyAdapterFactory(factories);
        }

        <T> @Nullable KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters);
    }
}
