package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class PredicateKeyAdapterFactory implements KeyAdapter.Factory {
    private final KeyAdapter<?> adapter;
    private final Predicate<? super TypeToken<?>> predicate;

    PredicateKeyAdapterFactory(Predicate<? super TypeToken<?>> predicate, KeyAdapter<?> adapter) {
        this.adapter = nonNull(adapter, "adapter");
        this.predicate = nonNull(predicate, "predicate");
    }

    @Override
    public <T> @Nullable KeyAdapter<T> create(TypeToken<T> type, TypeAdapters adapters) {
        @SuppressWarnings("unchecked")
        KeyAdapter<T> adapter = (KeyAdapter<T>) this.adapter;
        return predicate.test(type) ? adapter : null;
    }
}
