package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class PredicateKeyAdapterFactory implements KeyAdapter.Factory {
    private final Predicate<? super TypeRef<?>> predicate;
    private final KeyAdapter.TypedFactory<?> factory;

    PredicateKeyAdapterFactory(Predicate<? super TypeRef<?>> predicate, KeyAdapter.TypedFactory<?> factory) {
        this.predicate = nonNull(predicate, "predicate");
        this.factory = nonNull(factory, "factory");
    }

    @Override
    public <T> @Nullable KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters) {
        if (!predicate.test(type)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        KeyAdapter<T> adapter = ((KeyAdapter.TypedFactory<T>) this.factory).createKey(type, adapters);
        return adapter;
    }
}
