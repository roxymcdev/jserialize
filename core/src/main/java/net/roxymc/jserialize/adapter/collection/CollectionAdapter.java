package net.roxymc.jserialize.adapter.collection;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.*;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class CollectionAdapter<E> implements TypeAdapter.Mutable<Collection<@Nullable E>> {
    private static final Factory FACTORY = factory(DefaultCollectionProvider.INSTANCE);

    private final CollectionType<E> collectionType;
    private final CollectionProvider[] providers;

    private CollectionAdapter(TypeRef<? extends Collection<E>> collectionType, CollectionProvider[] providers) {
        this.collectionType = new CollectionType<>(collectionType);
        this.providers = providers;
    }

    public static Factory factory() {
        return FACTORY;
    }

    public static Factory factory(CollectionProvider... providers) {
        CollectionProvider[] providers0 = nonNull(providers, "providers").clone();

        @SuppressWarnings({"unchecked", "rawtypes"})
        Factory factory = Factory.polymorphic(Collection.class, type -> new CollectionAdapter(type, providers0));
        return factory;
    }

    @Override
    public @Nullable Collection<@Nullable E> mutate(Reader reader, @Nullable Collection<@Nullable E> collection, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();

            if (collection != null) {
                collection.clear();
            }

            return collection;
        }

        if (collection == null) {
            collection = collectionType.createCollection(providers);
        }

        TypeReader<E> elementReader = ctx.typeAdapters().getOrThrow(collectionType.elementType);

        reader.readArrayStart();

        for (int index = 0; reader.peek() != TokenTypes.ARRAY_END; index++) {
            E element = elementReader.read(reader, ctx);

            collection.add(element);
        }

        reader.readArrayEnd();

        return collection;
    }

    @Override
    public void write(Writer writer, @Nullable Collection<@Nullable E> collection, WriteContext ctx) throws IOException {
        if (collection == null) {
            writer.writeNull();
            return;
        }

        TypeWriter<E> elementWriter = ctx.typeAdapters().getOrThrow(collectionType.elementType);

        writer.writeArrayStart();

        for (E element : collection) {
            elementWriter.write(writer, element, ctx);
        }

        writer.writeArrayEnd();
    }

    @Override
    public TypeRef<? extends Collection<@Nullable E>> type() {
        return collectionType.collectionType;
    }
}
