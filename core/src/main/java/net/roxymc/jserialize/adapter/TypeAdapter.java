package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.type.TypeToken;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.function.Predicate;

public interface TypeAdapter<T> extends TypeReader<T>, TypeWriter<T> {
    static <T> TypeAdapter<T> of(TypeReader<T> reader, TypeWriter<T> writer) {
        return new TypeAdapter<>() {
            private final TypeReader<T> reader0 = reader;
            private final TypeWriter<T> writer0 = writer;

            @Override
            public @Nullable T read(Reader reader, TypeToken<? extends T> type, ReadContext context) throws IOException {
                return reader0.read(reader, type, context);
            }

            @Override
            public void write(Writer writer, TypeToken<? extends T> type, @Nullable T value, WriteContext context) throws IOException {
                writer0.write(writer, type, value, context);
            }
        };
    }

    @Override
    @Nullable T read(Reader reader, TypeToken<? extends T> type, ReadContext context) throws IOException;

    @Override
    void write(Writer writer, TypeToken<? extends T> type, @Nullable T value, WriteContext context) throws IOException;

    interface Mutable<T> extends TypeAdapter<T> {
        @Override
        default @Nullable T read(Reader reader, TypeToken<? extends T> type, ReadContext context) throws IOException {
            return mutate(reader, type, null, context);
        }

        @Contract("_, _, !null, _ -> param3")
        @Nullable T mutate(Reader reader, TypeToken<? extends T> type, @Nullable T value, ReadContext context) throws IOException;
    }

    interface Factory {
        static Factory predicate(Predicate<? super TypeToken<?>> predicate, TypeAdapter<?> adapter) {
            return new PredicateTypeAdapterFactory(predicate, adapter);
        }

        static <T> Factory polymorphic(Class<? super T> type, TypeAdapter<T> adapter) {
            return predicate(subtype -> type.isAssignableFrom(subtype.getRawType()), adapter);
        }

        static <T> Factory polymorphic(TypeToken<? extends T> type, TypeAdapter<T> adapter) {
            return predicate(subtype -> GenericTypeReflector.isSuperType(type.getType(), subtype.getType()), adapter);
        }

        static <T> Factory exact(TypeToken<? extends T> type, TypeAdapter<T> adapter) {
            return predicate(subtype -> type.getType().equals(subtype.getType()), adapter);
        }

        static <T> Factory exactRaw(Class<? super T> type, TypeAdapter<T> adapter) {
            return predicate(subtype -> type.equals(subtype.getRawType()), adapter);
        }

        <T> @Nullable TypeAdapter<T> create(TypeToken<T> type, TypeAdapters adapters);
    }
}
