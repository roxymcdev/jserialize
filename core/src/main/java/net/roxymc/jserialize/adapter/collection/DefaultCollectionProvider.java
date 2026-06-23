package net.roxymc.jserialize.adapter.collection;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;

public final class DefaultCollectionProvider implements CollectionProvider {
    public static final DefaultCollectionProvider INSTANCE = new DefaultCollectionProvider();

    private DefaultCollectionProvider() {
    }

    @Override
    public <E extends @Nullable Object> @Nullable CollectionFactory<E> resolve(TypeRef<? extends Collection<E>> type) {
        Class<?> rawType = type.getRawType();

        if (rawType.isAssignableFrom(ArrayList.class)) {
            return ArrayList::new;
        }

        if (rawType.isAssignableFrom(LinkedHashSet.class)) {
            return LinkedHashSet::new;
        }

        if (rawType.isAssignableFrom(TreeSet.class)) {
            return TreeSet::new;
        }

        if (rawType.isAssignableFrom(ArrayDeque.class)) {
            return ArrayDeque::new;
        }

        if (rawType.isAssignableFrom(CopyOnWriteArrayList.class)) {
            return CopyOnWriteArrayList::new;
        }

        if (rawType.isAssignableFrom(CopyOnWriteArraySet.class)) {
            return CopyOnWriteArraySet::new;
        }

        if (rawType.isAssignableFrom(ConcurrentHashMap.KeySetView.class)) {
            return ConcurrentHashMap::newKeySet;
        }

        if (rawType.isAssignableFrom(ConcurrentSkipListSet.class)) {
            return ConcurrentSkipListSet::new;
        }

        if (rawType.isAssignableFrom(ConcurrentLinkedQueue.class)) {
            return ConcurrentLinkedQueue::new;
        }

        if (rawType.isAssignableFrom(ConcurrentLinkedDeque.class)) {
            return ConcurrentLinkedDeque::new;
        }

        return null;
    }
}
