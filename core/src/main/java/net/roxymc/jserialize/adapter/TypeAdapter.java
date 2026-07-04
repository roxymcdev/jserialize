package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.type.TypeRef;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.function.Predicate;

public interface TypeAdapter<T> extends TypeReader<T>, TypeWriter<T> {
    static <T> TypeAdapter<T> of(TypeRef<? extends T> type, TypeReader<? extends T> reader, TypeWriter<? super T> writer) {
        return new TypeAdapter<>() {
            @Override
            public @Nullable T read(Reader reader0, ReadContext context) throws IOException {
                return reader.read(reader0, context);
            }

            @Override
            public void write(Writer writer0, @Nullable T value, WriteContext context) throws IOException {
                writer.write(writer0, value, context);
            }

            @Override
            public TypeRef<? extends T> type() {
                return type;
            }
        };
    }

    @Override
    @Nullable T read(Reader reader, ReadContext context) throws IOException;

    @Override
    void write(Writer writer, @Nullable T value, WriteContext context) throws IOException;

    TypeRef<? extends T> type();

    interface Mutable<T> extends TypeAdapter<T> {
        @Override
        default @Nullable T read(Reader reader, ReadContext context) throws IOException {
            return mutate(reader, null, context);
        }

        @Contract("_, !null, _ -> param2")
        @Nullable T mutate(Reader reader, @Nullable T value, ReadContext context) throws IOException;
    }

    interface Factory {
        static <T> Factory where(Predicate<? super TypeRef<?>> predicate, TypedFactory<? super T> factory) {
            return new PredicateTypeAdapterFactory(predicate, factory);
        }

        static <T> Factory polymorphic(Class<T> type, TypedFactory<? super T> factory) {
            return where(subtype -> type.isAssignableFrom(subtype.getRawType()), factory);
        }

        static <T> Factory polymorphic(TypeRef<T> type, TypedFactory<? super T> factory) {
            return where(subtype -> GenericTypeReflector.isSuperType(type.getType(), subtype.getType()), factory);
        }

        static <T> Factory exact(TypeAdapter<T> adapter) {
            return exact(adapter.type(), (TypedFactory<? super T>) $ -> adapter);
        }

        static <T> Factory exact(TypeRef<T> type, TypedFactory<? super T> factory) {
            return where(subtype -> type.getType().equals(subtype.getType()), factory);
        }

        static <T> Factory exactRaw(Class<T> type, TypedFactory<? super T> factory) {
            return exactRaw(TypeRef.of(type), factory);
        }

        static <T> Factory exactRaw(TypeRef<T> type, TypedFactory<? super T> factory) {
            return where(subtype -> type.getRawType().equals(subtype.getRawType()), factory);
        }

        static Factory composite(Factory... factories) {
            return new CompositeTypeAdapterFactory(factories);
        }

        <T> @Nullable TypeAdapter<T> create(TypeRef<T> type);
    }

    @FunctionalInterface
    interface TypedFactory<T> {
        @Nullable TypeAdapter<T> create(TypeRef<T> type);
    }
}
