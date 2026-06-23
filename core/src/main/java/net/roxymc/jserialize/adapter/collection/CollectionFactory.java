package net.roxymc.jserialize.adapter.collection;

import org.jspecify.annotations.Nullable;

import java.util.Collection;

@FunctionalInterface
public interface CollectionFactory<E extends @Nullable Object> {
    Collection<E> create();
}
