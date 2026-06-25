package net.roxymc.jserialize.adapter.collection;

import net.roxymc.jserialize.type.TypeRef;
import net.roxymc.jserialize.util.VarHandles;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.VarHandle;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.Collection;

import static io.leangen.geantyref.GenericTypeReflector.capture;
import static io.leangen.geantyref.GenericTypeReflector.getExactSuperType;

final class CollectionType<E extends @UnknownNullability Object> {
    private static final VarHandle COLLECTION_FACTORY_HANDLE = VarHandles.find(CollectionType.class, "collectionFactory", CollectionFactory.class);

    private final TypeRef<? extends Collection<E>> collectionType;
    final TypeRef<E> elementType;

    private @Nullable CollectionFactory<E> collectionFactory;

    CollectionType(TypeRef<? extends Collection<?>> collectionType) {
        AnnotatedType type = getExactSuperType(capture(collectionType.getAnnotatedType()), Collection.class);
        if (!(type instanceof AnnotatedParameterizedType)) {
            throw new IllegalStateException(collectionType.getType() + " must be a parameterized Collection");
        }

        AnnotatedParameterizedType ptype = (AnnotatedParameterizedType) type;

        this.collectionType = TypeRef.of(ptype);
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
