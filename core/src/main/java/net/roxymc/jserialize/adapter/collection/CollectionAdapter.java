package net.roxymc.jserialize.adapter.collection;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

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

    private <E extends @Nullable Object> CollectionType<E> resolveCollectionType(TypeToken<? extends Collection<?>> type) {
        @SuppressWarnings("unchecked")
        CollectionType<E> collectionType = (CollectionType<E>) cache.computeIfAbsent(type.getType(), $ -> new CollectionType<>(type));
        return collectionType;
    }

    @Override
    public @Nullable Collection<?> mutate(
            Reader reader, TypeToken<? extends Collection<?>> type, @Nullable Collection<?> value, ReadContext ctx
    ) throws IOException {
        return mutate0(reader, type, value, ctx);
    }

    private <E extends @Nullable Object> @Nullable Collection<E> mutate0(
            Reader reader, TypeToken<? extends Collection<?>> type, @Nullable Collection<E> collection, ReadContext ctx
    ) throws IOException {
        if (reader.peek() == TokenType.NULL) {
            reader.readNull();
            return collection;
        }

        CollectionType<E> collectionType = resolveCollectionType(type);

        if (collection == null) {
            collection = collectionType.createCollection(providers);
        }

        TypeAdapter<@NonNull E> elementAdapter = collectionType.elementAdapter(ctx.typeAdapters());

        reader.readArrayStart();

        for (int index = 0; reader.peek() != TokenType.ARRAY_END; index++) {
            E element = elementAdapter.read(reader, collectionType.elementType, ctx);

            collection.add(element);
        }

        reader.readArrayEnd();

        return collection;
    }

    @Override
    public void write(
            Writer writer, TypeToken<? extends Collection<?>> type, @Nullable Collection<?> value, WriteContext ctx
    ) throws IOException {
        write0(writer, type, value, ctx);
    }

    private <E extends @Nullable Object> void write0(
            Writer writer, TypeToken<? extends Collection<?>> type, @Nullable Collection<E> collection, WriteContext ctx
    ) throws IOException {
        if (collection == null) {
            writer.writeNull();
            return;
        }

        CollectionType<E> collectionType = resolveCollectionType(type);

        TypeAdapter<@NonNull E> elementAdapter = collectionType.elementAdapter(ctx.typeAdapters());

        writer.writeArrayStart();

        for (E element : collection) {
            elementAdapter.write(writer, collectionType.elementType, element, ctx);
        }

        writer.writeArrayEnd();
    }
}
