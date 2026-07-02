package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class PredicateTypeAdapterFactory implements TypeAdapter.Factory {
    private final Predicate<? super TypeRef<?>> predicate;
    private final TypeAdapter.TypedFactory<?> factory;

    PredicateTypeAdapterFactory(Predicate<? super TypeRef<?>> predicate, TypeAdapter.TypedFactory<?> factory) {
        this.predicate = nonNull(predicate, "predicate");
        this.factory = nonNull(factory, "factory");
    }

    @Override
    public <T> @Nullable TypeAdapter<T> create(TypeRef<T> type) {
        if (!predicate.test(type)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        TypeAdapter<T> adapter = ((TypeAdapter.TypedFactory<T>) this.factory).create(type);
        return adapter;
    }
}
