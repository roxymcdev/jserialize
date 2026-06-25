package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class CompositeTypeAdapterFactory implements TypeAdapter.Factory {
    private final TypeAdapter.Factory[] factories;

    CompositeTypeAdapterFactory(TypeAdapter.Factory[] factories) {
        this.factories = nonNull(factories, "factories").clone();
    }

    @Override
    public @Nullable <T> TypeAdapter<T> create(TypeRef<T> type, TypeAdapters adapters) {
        for (TypeAdapter.Factory factory : factories) {
            TypeAdapter<T> adapter = factory.create(type, adapters);

            if (adapter != null) {
                return adapter;
            }
        }

        return null;
    }
}
