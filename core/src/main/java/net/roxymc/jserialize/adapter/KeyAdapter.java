package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public interface KeyAdapter<T> extends KeyDecoder<T>, KeyEncoder<T> {
    static <T> KeyAdapter<T> of(KeyDecoder<T> decoder, KeyEncoder<T> encoder) {
        return new KeyAdapter<>() {
            @Override
            public T decode(String value) {
                return decoder.decode(value);
            }

            @Override
            public String encode(T value) {
                return encoder.encode(value);
            }
        };
    }

    @Override
    T decode(String value);

    @Override
    String encode(T value);

    interface Factory {
        static Factory predicate(Predicate<? super TypeToken<?>> predicate, KeyAdapter<?> adapter) {
            return new PredicateKeyAdapterFactory(predicate, adapter);
        }

        static <T> Factory polymorphic(Class<? super T> type, KeyAdapter<T> adapter) {
            return predicate(subtype -> type.isAssignableFrom(subtype.getRawType()), adapter);
        }

        static <T> Factory polymorphic(TypeToken<? extends T> type, KeyAdapter<T> adapter) {
            return predicate(subtype -> GenericTypeReflector.isSuperType(type.getType(), subtype.getType()), adapter);
        }

        static <T> Factory exact(TypeToken<? extends T> type, KeyAdapter<T> adapter) {
            return predicate(subtype -> type.getType().equals(subtype.getType()), adapter);
        }

        static <T> Factory exactRaw(Class<? super T> type, KeyAdapter<T> adapter) {
            return predicate(subtype -> type.equals(subtype.getRawType()), adapter);
        }

        <T> @Nullable KeyAdapter<T> create(TypeToken<T> type, TypeAdapters adapters);
    }
}
