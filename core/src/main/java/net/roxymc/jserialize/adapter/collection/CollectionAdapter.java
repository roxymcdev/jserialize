package net.roxymc.jserialize.adapter.collection;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.*;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;
import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

public final class CollectionAdapter implements TypeAdapter.Mutable<Collection<?>> {
    private static final TypeAdapter.Factory FACTORY = factory(DefaultCollectionProvider.INSTANCE);

    private final Map<Type, CollectionType<?>> cache = new ConcurrentHashMap<>();
    private final CollectionProvider[] providers;

    public CollectionAdapter(CollectionProvider... providers) {
        this.providers = nonNull(providers, "providers").clone();
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    public static TypeAdapter.Factory factory(CollectionProvider... providers) {
        return TypeAdapter.Factory.polymorphic(Collection.class, new CollectionAdapter(providers));
    }

    private <E extends @Nullable Object> CollectionType<E> resolveCollectionType(TypeRef<? extends Collection<?>> type) {
        @SuppressWarnings("unchecked")
        CollectionType<E> collectionType = (CollectionType<E>) cache.computeIfAbsent(type.getType(), $ -> new CollectionType<>(type));
        return collectionType;
    }

    @Override
    public @Nullable Collection<?> mutate(
            Reader reader, TypeRef<? extends Collection<?>> type, @Nullable Collection<?> value, ReadContext ctx
    ) throws IOException {
        return mutate0(reader, type, value, ctx);
    }

    private <E extends @Nullable Object> @Nullable Collection<E> mutate0(
            Reader reader, TypeRef<? extends Collection<?>> type, @Nullable Collection<E> collection, ReadContext ctx
    ) throws IOException {
        checkAssignable(Collection.class, type.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();

            if (collection != null) {
                collection.clear();
            }

            return collection;
        }

        CollectionType<E> collectionType = resolveCollectionType(type);

        if (collection == null) {
            collection = collectionType.createCollection(providers);
        }

        TypeReader<@NonNull E> elementReader = ctx.typeAdapters().getOrThrow(collectionType.elementType);

        reader.readArrayStart();

        for (int index = 0; reader.peek() != TokenTypes.ARRAY_END; index++) {
            E element = elementReader.read(reader, collectionType.elementType, ctx);

            collection.add(element);
        }

        reader.readArrayEnd();

        return collection;
    }

    @Override
    public void write(
            Writer writer, TypeRef<? extends Collection<?>> type, @Nullable Collection<?> value, WriteContext ctx
    ) throws IOException {
        write0(writer, type, value, ctx);
    }

    private <E extends @Nullable Object> void write0(
            Writer writer, TypeRef<? extends Collection<?>> type, @Nullable Collection<E> collection, WriteContext ctx
    ) throws IOException {
        checkAssignable(Collection.class, type.getRawType());

        if (collection == null) {
            writer.writeNull();
            return;
        }

        CollectionType<E> collectionType = resolveCollectionType(type);

        TypeWriter<@NonNull E> elementWriter = ctx.typeAdapters().getOrThrow(collectionType.elementType);

        writer.writeArrayStart();

        for (E element : collection) {
            elementWriter.write(writer, collectionType.elementType, element, ctx);
        }

        writer.writeArrayEnd();
    }
}
