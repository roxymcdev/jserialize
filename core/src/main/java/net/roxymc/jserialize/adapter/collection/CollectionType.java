package net.roxymc.jserialize.adapter.collection;

import net.roxymc.jserialize.type.TypeRef;
import net.roxymc.jserialize.util.VarHandles;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AnnotatedParameterizedType;
import java.util.Collection;

import static net.roxymc.jserialize.util.TypeUtils.resolveTypeParams;
import static net.roxymc.jserialize.util.TypeUtils.resolveUpperBound;

final class CollectionType<E> {
    private static final VarHandle COLLECTION_FACTORY_HANDLE = VarHandles.find(CollectionType.class, "collectionFactory", CollectionFactory.class);

    final TypeRef<? extends Collection<E>> collectionType;
    final TypeRef<E> elementType;

    private @Nullable CollectionFactory<E> collectionFactory;

    CollectionType(TypeRef<? extends Collection<E>> collectionType) {
        AnnotatedParameterizedType ptype = resolveUpperBound(resolveTypeParams(collectionType, Collection.class));

        this.collectionType = collectionType;
        this.elementType = TypeRef.of(ptype.getAnnotatedActualTypeArguments()[0]);
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
