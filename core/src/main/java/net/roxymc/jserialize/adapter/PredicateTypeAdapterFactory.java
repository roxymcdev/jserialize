package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class PredicateTypeAdapterFactory implements TypeAdapter.Factory {
    private final TypeAdapter<?> adapter;
    private final Predicate<? super TypeRef<?>> predicate;

    PredicateTypeAdapterFactory(Predicate<? super TypeRef<?>> predicate, TypeAdapter<?> adapter) {
        this.adapter = nonNull(adapter, "adapter");
        this.predicate = nonNull(predicate, "predicate");
    }

    @Override
    public <T> @Nullable TypeAdapter<T> create(TypeRef<T> type) {
        @SuppressWarnings("unchecked")
        TypeAdapter<T> adapter = (TypeAdapter<T>) this.adapter;
        return predicate.test(type) ? adapter : null;
    }
}
