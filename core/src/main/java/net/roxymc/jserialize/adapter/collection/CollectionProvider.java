package net.roxymc.jserialize.adapter.collection;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

public interface CollectionProvider {
    <E extends @Nullable Object> @Nullable CollectionFactory<E> resolve(TypeRef<? extends Collection<E>> type);
}
