package net.roxymc.jserialize.adapter.collection;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;
import net.roxymc.jserialize.util.VarHandles;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Collection;

import static io.leangen.geantyref.GenericTypeReflector.capture;
import static io.leangen.geantyref.GenericTypeReflector.resolveType;

final class CollectionType<E extends @UnknownNullability Object> {
    private static final AnnotatedType COLLECTION_TYPE = GenericTypeReflector.annotate(Collection.class);
    private static final VarHandle COLLECTION_FACTORY_HANDLE = VarHandles.find(CollectionType.class, "collectionFactory", CollectionFactory.class);

    final TypeToken<? extends Collection<E>> collectionType;
    final TypeToken<E> elementType;
    private @Nullable CollectionFactory<E> collectionFactory;

    CollectionType(TypeToken<? extends Collection<?>> collectionType) {
        AnnotatedType type = resolveType(COLLECTION_TYPE, capture(collectionType.getAnnotatedType()));
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(collectionType.getRawType() + " must be parameterized");
        }

        AnnotatedParameterizedType ptype = (AnnotatedParameterizedType) type;

        this.collectionType = TypeToken.of(ptype);
        this.elementType = TypeToken.of(ptype.getAnnotatedActualTypeArguments()[0]);
    }

    TypeAdapter<@NonNull E> elementAdapter(TypeAdapters adapters) {
        return adapters.getOrThrow(elementType);
    }

    @SuppressWarnings("unchecked")
    Collection<E> createCollection(CollectionProvider[] providers) {
        CollectionFactory<E> value = (CollectionFactory<E>) COLLECTION_FACTORY_HANDLE.getAcquire(this);

        if (value == null) {
            CollectionFactory<E> resolved = null;

            for (CollectionProvider provider : providers) {
                CollectionFactory<E> factory = provider.resolve(collectionType);

                if (factory != null) {
                    resolved = factory;
                    break;
                }
            }

            if (resolved == null) {
                resolved = () -> {
                    throw new IllegalStateException("Could not find collection factory for " + collectionType.getType());
                };
            }

            CollectionFactory<E> witness = (CollectionFactory<E>) COLLECTION_FACTORY_HANDLE.compareAndExchangeRelease(this, null, resolved);
            value = witness != null ? witness : resolved;
        }

        return value.create();
    }
}
